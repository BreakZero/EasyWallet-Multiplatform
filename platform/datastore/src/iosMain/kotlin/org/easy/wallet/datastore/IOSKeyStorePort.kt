@file:OptIn(ExperimentalForeignApi::class)

package org.easy.wallet.datastore

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import org.easy.wallet.datastore.common.checkStatus
import org.easy.wallet.datastore.common.secureRandomBytes
import org.easy.wallet.datastore.common.toNSData
import platform.CoreCrypto.kCCKeySizeAES256
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Security.SecItemAdd
import platform.Security.SecItemDelete
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.darwin.OSStatus

class IOSKeyStorePort(
  private val serviceName: String = NSBundle.mainBundle.bundleIdentifier ?: "org.easy.wallet",
  private val aesKeySizeBytes: Int = kCCKeySizeAES256.toInt()
) : KeyStorePort {
  private val ephemeralKey: ByteArray = secureRandomBytes(aesKeySizeBytes)

  override suspend fun store(alias: String, plaintext: ByteArray) {
    delete(alias)
    val attrsStore = mapOf<Any?, Any?>(
      kSecClass to kSecClassGenericPassword,
      kSecAttrAccount to alias,
      kSecAttrService to serviceName,
      kSecAttrAccessible to kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly,
      kSecValueData to plaintext.toNSData()
    )
    val status: OSStatus = SecItemAdd(attrsStore as CFDictionaryRef, null)
    checkStatus(status, "Keychain store failed")
  }

  override suspend fun load(alias: String): ByteArray = memScoped {
    val attrsLoad = mapOf<Any?, Any?>(
      kSecClass to kSecClassGenericPassword,
      kSecAttrAccount to alias,
      kSecAttrService to serviceName,
      kSecReturnData to kCFBooleanTrue,
      kSecMatchLimit to kSecMatchLimitOne
    )
    val out = alloc<ObjCObjectVar<Any?>>()
    val status: OSStatus = SecItemCopyMatching(attrsLoad as CFDictionaryRef, out.ptr)
    when (status) {
      errSecSuccess -> (out.value as NSData).toByteArray()
      errSecItemNotFound -> error("Keychain item not found for alias: $alias")
      else -> error("Keychain load failed: $status")
    }
  }

  override suspend fun delete(alias: String) {
    val attrsDelete = mapOf<Any?, Any?>(
      kSecClass to kSecClassGenericPassword,
      kSecAttrAccount to alias,
      kSecAttrService to serviceName
    )
    val status = SecItemDelete(attrsDelete as CFDictionaryRef)
    if (status != errSecSuccess && status != errSecItemNotFound) {
      error("Keychain delete failed: $status")
    }
  }

  override suspend fun encryptEphemeral(plaintext: ByteArray): EncryptedBlob {
    val iv = secureRandomBytes(kCCBlockSizeAES128.toInt()) // 16 bytes
    val cipher = aesCbcPkcs7(
      encrypt = true,
      input = plaintext,
      key = ephemeralKey,
      iv = iv
    )
    return EncryptedBlob(iv = iv, ciphertext = cipher)
  }

  override suspend fun decryptEphemeral(blob: EncryptedBlob): ByteArray {
    return aesCbcPkcs7(
      encrypt = false,
      input = blob.ciphertext,
      key = ephemeralKey,
      iv = blob.iv
    )
  }

  private fun aesCbcPkcs7(
    encrypt: Boolean,
    input: ByteArray,
    key: ByteArray,
    iv: ByteArray
  ): ByteArray = memScoped {
    require(iv.size == kCCBlockSizeAES128.toInt()) { "IV must be 16 bytes for AES-CBC" }

    val outCapacity = if (encrypt) input.size + kCCBlockSizeAES128.toInt() else input.size
    val out = ByteArray(outCapacity)

    val outMoved = alloc<size_tVar>()
    val status = key.usePinned { keyPinned ->
      iv.usePinned { ivPinned ->
        input.usePinned { inPinned ->
          out.usePinned { outPinned ->
            CCCrypt(
              /* op        */ if (encrypt) kCCEncrypt else kCCDecrypt,
              /* alg       */ kCCAlgorithmAES,
              /* options   */ kCCOptionPKCS7Padding,
              /* key       */ keyPinned.addressOf(0),
              /* keyLen    */ key.size.convert(),
              /* iv        */ ivPinned.addressOf(0),
              /* dataIn    */ inPinned.addressOf(0),
              /* dataInLen */ input.size.convert(),
              /* dataOut   */ outPinned.addressOf(0),
              /* outAvail  */ outCapacity.convert(),
              /* outMoved  */ outMoved.ptr
            )
          }
        }
      }
    }
    if (status != kCCSuccess) error("CCCrypt ${if (encrypt) "encrypt" else "decrypt"} failed: $status")
    out.copyOf(outMoved.value.toInt())
  }
}

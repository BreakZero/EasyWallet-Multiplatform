package org.easy.wallet.datastore

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.cstr
import kotlinx.cinterop.usePinned
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.dataWithBytes
import platform.Security.SecItemAdd
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleWhenUnlockedThisDeviceOnly
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword

actual class PasswordStorage {
  @OptIn(ExperimentalForeignApi::class)
  actual fun save(
    service: String,
    account: String,
    password: String
  ): Boolean {
    val query = CFDictionaryCreateMutable(null, 5, null, null).also {
      CFDictionaryAddValue(it, kSecClassGenericPassword, kSecClass)
      CFDictionaryAddValue(it, service.cstr, kSecAttrService)
      CFDictionaryAddValue(it, kSecAttrAccessibleWhenUnlockedThisDeviceOnly, kSecAttrAccessible)
    }

    SecItemDelete(query)
    return SecItemAdd(query, null) == errSecSuccess
  }

  actual fun retrieve(service: String, account: String): String? {
    TODO("Not yet implemented")
  }

  actual fun delete(service: String, account: String): Boolean {
    TODO("Not yet implemented")
  }
}

private fun NSData.stringValue(encoding: ULong): String? = NSString.create(data = this, encoding = encoding)?.toString()

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData = this.usePinned {
  NSData.dataWithBytes(it.addressOf(0), it.get().size.toULong())
}
package org.easy.wallet.datastore


import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.GeneralSecurityException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class AndroidKeyStorePort(
  private val context: Context,
  private val masterAlias: String = DEFAULT_MASTER_ALIAS,
  private val preferStrongBox: Boolean = true,
  /**
   * 是否启用“解锁门槛”：
   * - 0：不要求用户认证（后台任务、无交互适用）
   * - >0：要求设备解锁/凭证，且在该秒数有效期内可无感访问（无需 BiometricPrompt）
   */
  private val authValiditySeconds: Int = 0,
  private val prefs: SharedPreferences =
    context.getSharedPreferences("keystore_port_store", Context.MODE_PRIVATE)
) : KeyStorePort {

  companion object {
    private const val DEFAULT_MASTER_ALIAS = "ks_port_master_key"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val PREF_KEY_PREFIX = "enc_"         // 存放密文
    private const val PREF_IV_PREFIX = "iv_"           // 存放 IV
    private const val GCM_TAG_LENGTH_BITS = 128        // GCM tag 16 bytes
    private const val IV_LENGTH_BYTES = 12             // 标准推荐 12 字节
  }

  override suspend fun store(alias: String, plaintext: ByteArray) = withContext(Dispatchers.IO) {
    val blob = encryptEphemeral(plaintext)
    persist(alias, blob)
  }

  override suspend fun load(alias: String): ByteArray = withContext(Dispatchers.IO) {
    val blob = loadPersisted(alias)
    decryptEphemeral(blob)
  }

  override suspend fun delete(alias: String) {
    prefs.edit()
      .remove(PREF_KEY_PREFIX + alias)
      .remove(PREF_IV_PREFIX + alias)
      .apply()
  }

  override suspend fun encryptEphemeral(plaintext: ByteArray): EncryptedBlob =
    withContext(Dispatchers.Default) {
      val cipher = Cipher.getInstance(TRANSFORMATION)
      cipher.init(Cipher.ENCRYPT_MODE, getOrCreateMasterKey())
      val iv = cipher.iv ?: ByteArray(0) // 12 bytes
      val ciphertext = cipher.doFinal(plaintext)
      EncryptedBlob(iv = iv, ciphertext = ciphertext)
    }

  override suspend fun decryptEphemeral(blob: EncryptedBlob): ByteArray =
    withContext(Dispatchers.Default) {
      try {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, blob.iv)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateMasterKey(), spec)
        cipher.doFinal(blob.ciphertext)
      } catch (e: KeyPermanentlyInvalidatedException) {
        throw IllegalStateException(
          "MasterKey permanently invalidated; data must be re-encrypted.",
          e
        )
      } catch (e: GeneralSecurityException) {
        throw IllegalStateException("Decryption failed: ${e.message}", e)
      }
    }

  private fun persist(alias: String, blob: EncryptedBlob) {
    val b64Ct = Base64.encodeToString(blob.ciphertext, Base64.NO_WRAP)
    val b64Iv = Base64.encodeToString(blob.iv, Base64.NO_WRAP)
    prefs.edit()
      .putString(PREF_KEY_PREFIX + alias, b64Ct)
      .putString(PREF_IV_PREFIX + alias, b64Iv)
      .apply()
  }

  private fun loadPersisted(alias: String): EncryptedBlob {
    val ct = prefs.getString(PREF_KEY_PREFIX + alias, null)
      ?: error("Encrypted data not found for alias: $alias")
    val iv = prefs.getString(PREF_IV_PREFIX + alias, null)
      ?: error("IV not found for alias: $alias")
    return EncryptedBlob(
      iv = Base64.decode(iv, Base64.NO_WRAP),
      ciphertext = Base64.decode(ct, Base64.NO_WRAP)
    )
  }

  private fun getOrCreateMasterKey(): SecretKey {
    val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    ks.getKey(masterAlias, null)?.let { return it as SecretKey }

    val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
    val builder = KeyGenParameterSpec.Builder(
      masterAlias,
      KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
      .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
      .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
      .setRandomizedEncryptionRequired(true)

    if (authValiditySeconds > 0) {
      // 要求设备凭证/生物认证后，在有效期内可直接使用
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        builder.setUserAuthenticationRequired(true)
          .setUserAuthenticationParameters(
            authValiditySeconds,
            KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL
          )
      } else {
        @Suppress("DEPRECATION")
        builder.setUserAuthenticationRequired(true)
          .setUserAuthenticationValidityDurationSeconds(authValiditySeconds)
      }
    }

    if (preferStrongBox && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      try {
        builder.setIsStrongBoxBacked(true)
      } catch (_: Throwable) {
        // 部分设备/ROM 不支持 StrongBox，忽略
      }
    }

    keyGen.init(builder.build())
    return keyGen.generateKey()
  }

}
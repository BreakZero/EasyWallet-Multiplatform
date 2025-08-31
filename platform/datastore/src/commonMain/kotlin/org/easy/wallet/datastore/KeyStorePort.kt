package org.easy.wallet.datastore

interface KeyStorePort {
  suspend fun store(alias: String, plaintext: ByteArray)

  suspend fun load(alias: String): ByteArray

  suspend fun delete(alias: String)

  suspend fun encryptEphemeral(plaintext: ByteArray): EncryptedBlob

  suspend fun decryptEphemeral(blob: EncryptedBlob): ByteArray
}

data class EncryptedBlob(
  val iv: ByteArray,
  val ciphertext: ByteArray
)
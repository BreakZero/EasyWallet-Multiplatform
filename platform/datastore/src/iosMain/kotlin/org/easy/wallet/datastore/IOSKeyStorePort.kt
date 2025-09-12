package org.easy.wallet.datastore

class IOSKeyStorePort() : KeyStorePort {
  override suspend fun store(alias: String, plaintext: ByteArray) {

  }

  override suspend fun load(alias: String): ByteArray {
    return byteArrayOf()
  }

  override suspend fun delete(alias: String) {

  }

  override suspend fun encryptEphemeral(plaintext: ByteArray): EncryptedBlob {
    return EncryptedBlob(byteArrayOf(), byteArrayOf())
  }

  override suspend fun decryptEphemeral(blob: EncryptedBlob): ByteArray {
    return byteArrayOf()
  }
}

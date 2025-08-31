package org.easy.wallet.datastore

import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
class IOSKeyStorePort() : KeyStorePort {
  override suspend fun store(alias: String, plaintext: ByteArray) {
    TODO("Not yet implemented")
  }

  override suspend fun load(alias: String): ByteArray {
    TODO("Not yet implemented")
  }

  override suspend fun delete(alias: String) {
    TODO("Not yet implemented")
  }

  override suspend fun encryptEphemeral(plaintext: ByteArray): EncryptedBlob {
    TODO("Not yet implemented")
  }

  override suspend fun decryptEphemeral(blob: EncryptedBlob): ByteArray {
    TODO("Not yet implemented")
  }

}
@file:OptIn(ExperimentalForeignApi::class)

package org.easy.wallet.datastore

import io.ktor.utils.io.core.toByteArray
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped

class IOSKeyStorePort : KeyStorePort {
  override suspend fun store(alias: String, plaintext: ByteArray) {
    delete(alias)
  }

  override suspend fun load(alias: String): ByteArray = memScoped {
    "govern picture wave bright energy tilt truck arrest sunset stick chuckle breeze".toByteArray()
  }

  override suspend fun delete(alias: String) {
  }

  override suspend fun encryptEphemeral(plaintext: ByteArray): EncryptedBlob = EncryptedBlob(byteArrayOf(), byteArrayOf())

  override suspend fun decryptEphemeral(blob: EncryptedBlob): ByteArray = byteArrayOf()
}
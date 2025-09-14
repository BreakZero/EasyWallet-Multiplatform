@file:OptIn(ExperimentalForeignApi::class)

package org.easy.wallet.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.ktor.utils.io.core.toByteArray
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.first

/**
 * TODO iOS encryption implementation
 */
class IOSKeyStorePort(
  private val dataStore: DataStore<Preferences>
) : KeyStorePort {
  override suspend fun store(alias: String, plaintext: ByteArray) {
    dataStore.edit {
      it[stringPreferencesKey(alias)] = plaintext.decodeToString()
    }
  }

  override suspend fun load(alias: String): ByteArray = dataStore.data.first()[stringPreferencesKey(alias)]?.toByteArray() ?: byteArrayOf()

  override suspend fun delete(alias: String) {
    dataStore.edit {
      it[stringPreferencesKey(alias)] = ""
    }
  }

  override suspend fun encryptEphemeral(plaintext: ByteArray): EncryptedBlob = EncryptedBlob(byteArrayOf(), byteArrayOf())

  override suspend fun decryptEphemeral(blob: EncryptedBlob): ByteArray = byteArrayOf()
}
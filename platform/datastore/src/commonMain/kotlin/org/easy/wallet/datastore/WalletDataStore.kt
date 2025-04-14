package org.easy.wallet.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

internal const val DATA_STORE_FILE_NAME = "wallet.preferences_pb"

class WalletDataStore internal constructor(
  private val dataStore: DataStore<Preferences>
) {
  fun activeWallet(walletName: String): Flow<String> = dataStore.data.map {
    it[stringPreferencesKey(walletName)]
      ?: throw NoSuchElementException("Wallet $walletName not found!")
  }

  fun getWalletName(): Flow<String?> = dataStore.data.map { it[PreferencesKeys.WALLET_NAME_KEY]?.toMutableSet()?.firstOrNull() }

  fun walletMnemonic(): Flow<String?> = getWalletName().flatMapLatest { walletName ->
    dataStore.data.map {
      it[stringPreferencesKey(walletName.orEmpty())]
    }
  }

  suspend fun addWallet(walletName: String, value: String) {
    dataStore.edit {
      val updateWalletNames =
        it[PreferencesKeys.WALLET_NAME_KEY]?.toMutableSet()?.apply { add(walletName) } ?: setOf(
          walletName
        )
      it[PreferencesKeys.WALLET_NAME_KEY] = updateWalletNames
      it[stringPreferencesKey(walletName)] = value
    }
  }
}

internal fun createDataStore(producePath: () -> String): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
  produceFile = { producePath().toPath() }
)
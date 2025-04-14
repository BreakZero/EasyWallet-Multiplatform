package org.easy.wallet.data.repository

import co.touchlab.kermit.Logger
import com.trustwallet.core.HDWallet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.easy.wallet.datastore.WalletDataStore

class WalletRepositoryImpl internal constructor(
  private val walletDataStore: WalletDataStore
) : WalletRepository {
  override fun generateMnemonic(): String {
    val hdWallet = HDWallet(128, "")
    return hdWallet.mnemonic
  }

  override suspend fun saveWallet(name: String, value: String) {
    Logger.d { "wallet name: $name, mnemonic: $value" }
    walletDataStore.addWallet(name, value)
  }

  override fun hasActivatedWallet(): Flow<Boolean> = walletDataStore.getWalletName().flatMapLatest { walletName ->
    walletDataStore
      .activeWallet(walletName.orEmpty())
      .map { !it.isNullOrBlank() }
      .catch { emit(false) }
  }

  override fun walletName(): Flow<String?> = walletDataStore.getWalletName()

  override fun walletMnemonic(): Flow<String?> = walletDataStore.walletMnemonic()
}
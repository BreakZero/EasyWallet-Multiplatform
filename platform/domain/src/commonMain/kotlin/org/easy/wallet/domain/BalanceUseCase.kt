package org.easy.wallet.domain

import com.trustwallet.core.CoinType
import com.trustwallet.core.HDWallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.easy.wallet.data.repository.AssetsRepository
import org.easy.wallet.data.repository.BalanceRepository
import org.easy.wallet.data.repository.WalletRepository
import org.easy.wallet.model.Balance
import org.easy.wallet.model.toBalance

class BalanceUseCase internal constructor(
  private val walletRepository: WalletRepository,
  private val assetsRepository: AssetsRepository,
  private val balanceRepository: BalanceRepository
) {
  operator fun invoke(): Flow<List<Balance>> {
    val assetsFlow = assetsRepository.loadAllAssets()
    val walletFlow = walletRepository.walletMnemonic().map {
      it?.let { HDWallet(it, "") }
    }

    return combine(assetsFlow, walletFlow) { assets, wallet ->
      if (wallet != null) {
        coroutineScope {
          assets
            .map { asset ->
              async {
                val balance = balanceRepository.fetchBalance(
                  wallet.getAddressForCoin(CoinType.Ethereum),
                  asset.contractAddress
                )
                asset.toBalance(balance)
              }
            }.awaitAll()
        }
      } else {
        assets.map { it.toBalance() }
      }
    }.flowOn(Dispatchers.IO)
  }
}
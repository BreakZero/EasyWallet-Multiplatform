package org.easy.wallet.domain

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.trustwallet.core.HDWallet
import org.easy.wallet.data.repository.AssetRepository
import org.easy.wallet.model.AssetBalance
import org.easy.wallet.model.WalletAccount
import org.easy.wallet.model.zeroBalance

class LoadAssetBalancesUseCase(
  private val assetRepository: AssetRepository
) {
  operator fun invoke(walletAccount: WalletAccount): Flow<List<AssetBalance>> = flow {
    val hdWallet = HDWallet(walletAccount.mnemonic, "")
    val assets = assetRepository.listAssets()

    emit(
      assets.map { asset ->
        asset.zeroBalance(hdWallet.address(asset.chainId))
      }
    )

    val balances = coroutineScope {
      assets.map { asset ->
        async {
          val address = hdWallet.address(asset.chainId)
          assetRepository.getBalance(asset, address) ?: asset.zeroBalance(address)
        }
      }.awaitAll()
    }
    emit(balances)
  }
}

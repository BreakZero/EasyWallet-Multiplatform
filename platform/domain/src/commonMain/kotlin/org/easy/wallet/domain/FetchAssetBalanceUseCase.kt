package org.easy.wallet.domain

import com.trustwallet.core.HDWallet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.easy.wallet.data.repository.AccountRepositoryImpl
import org.easy.wallet.data.repository.AssetRepository
import org.easy.wallet.model.AssetBalance
import org.easy.wallet.model.AssetId
import org.easy.wallet.model.zeroBalance

class FetchAssetBalanceUseCase(
  private val accountRepository: AccountRepositoryImpl,
  private val assetRepository: AssetRepository
) {
  operator fun invoke(assetId: AssetId): Flow<AssetBalance?> = flow {
    val account = accountRepository.activeAccount() ?: run {
      emit(null)
      return@flow
    }
    val asset = assetRepository.getAsset(assetId) ?: run {
      emit(null)
      return@flow
    }
    val address = HDWallet(account.mnemonic, "").address(asset.chainId)
    emit(asset.zeroBalance(address))
    emit(assetRepository.getBalance(asset, address) ?: asset.zeroBalance(address))
  }
}

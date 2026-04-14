package org.easy.wallet.di

import org.easy.wallet.feature.account.AccountViewModel
import org.easy.wallet.feature.assets.AssetsViewModel
import org.easy.wallet.feature.assets.detail.AssetDetailViewModel
import org.easy.wallet.feature.send.SendFlowViewModel
import org.easy.wallet.feature.wallet.create.GenerateSeedViewModel
import org.easy.wallet.feature.wallet.passcode.CreatePassCodeViewModel
import org.easy.wallet.feature.wallet.restore.WalletRestoreViewModel
import org.easy.wallet.model.AssetId
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
  viewModelOf(::AccountViewModel)
  viewModelOf(::AssetsViewModel)
  viewModelOf(::GenerateSeedViewModel)
  viewModelOf(::WalletRestoreViewModel)
  viewModelOf(::CreatePassCodeViewModel)

  viewModel { (assetId: AssetId) ->
    AssetDetailViewModel(
      fetchAssetBalanceUseCase = get(),
      assetId = assetId
    )
  }

  viewModel { (assetId: AssetId) ->
    SendFlowViewModel(
      fetchAssetBalanceUseCase = get(),
      estimateTransactionFeeUseCase = get(),
      sendTokenUseCase = get(),
      validateAddressUseCase = get(),
      assetId = assetId
    )
  }
}

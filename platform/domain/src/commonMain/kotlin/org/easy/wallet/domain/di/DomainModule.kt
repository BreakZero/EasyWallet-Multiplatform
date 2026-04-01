package org.easy.wallet.domain.di

import org.easy.wallet.domain.FetchAssetBalanceUseCase
import org.easy.wallet.domain.LoadAssetBalancesUseCase
import org.easy.wallet.domain.usecase.ConnectDAppUseCase
import org.easy.wallet.domain.usecase.EstimateTransactionFeeUseCase
import org.easy.wallet.domain.usecase.SendTokenUseCase
import org.easy.wallet.domain.usecase.ValidateAddressUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
  singleOf(::LoadAssetBalancesUseCase)
  singleOf(::FetchAssetBalanceUseCase)
  singleOf(::SendTokenUseCase)
  singleOf(::EstimateTransactionFeeUseCase)
  singleOf(::ValidateAddressUseCase)
  singleOf(::ConnectDAppUseCase)
}

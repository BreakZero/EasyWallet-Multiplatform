package org.easy.wallet.domain.di

import org.easy.wallet.domain.FetchTokenInformationUseCase
import org.easy.wallet.domain.LoadAllBalancesUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
  singleOf(::LoadAllBalancesUseCase)
  singleOf(::FetchTokenInformationUseCase)
}
package org.easy.wallet.domain.di

import org.easy.wallet.domain.BalanceUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
  singleOf(::BalanceUseCase)
}
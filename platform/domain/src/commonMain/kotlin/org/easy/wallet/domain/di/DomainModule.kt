package org.easy.wallet.domain.di

import org.easy.wallet.domain.usecase.ConnectDAppUseCase
import org.easy.wallet.domain.usecase.EstimateTransactionFeeUseCase
import org.easy.wallet.domain.usecase.GetTransactionHistoryUseCase
import org.easy.wallet.domain.usecase.SendTokenUseCase
import org.koin.dsl.module

val domainModule = module {
  // Use cases
  single { GetTransactionHistoryUseCase(transactionService = get()) }
  single { SendTokenUseCase(transactionService = get()) }
  single { EstimateTransactionFeeUseCase(transactionService = get()) }
  single { ConnectDAppUseCase(web3InjectionService = get(), chainContextManager = get()) }
}

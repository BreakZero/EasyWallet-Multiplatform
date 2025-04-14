package org.easy.wallet.di

import org.easy.wallet.data.di.dataModule
import org.easy.wallet.domain.di.domainModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
  appDeclaration()
  modules(dataModule, domainModule, viewModelModule)
}
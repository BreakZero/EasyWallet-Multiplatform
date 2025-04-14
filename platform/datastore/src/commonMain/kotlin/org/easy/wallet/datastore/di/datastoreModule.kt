package org.easy.wallet.datastore.di

import org.easy.wallet.datastore.WalletDataStore
import org.easy.wallet.datastore.assets.AssetDataSource
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val platformDataStoreModule: Module

private val dataStoreModules = module {
  single {
    WalletDataStore(get())
  }
  single {
    AssetDataSource()
  }
}

val storeModules: Module
  get() = module {
    includes(dataStoreModules + platformDataStoreModule)
  }
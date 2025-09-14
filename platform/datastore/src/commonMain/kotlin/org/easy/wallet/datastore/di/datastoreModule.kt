package org.easy.wallet.datastore.di

import org.easy.wallet.datastore.PreferencesRepository
import org.easy.wallet.datastore.UserPreferencesRepository
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect val platformDataStoreModule: Module

private val dataStoreModules = module {
  single { UserPreferencesRepository(get()) } bind PreferencesRepository::class
}

val storeModules: Module
  get() = module {
    includes(dataStoreModules + platformDataStoreModule)
  }
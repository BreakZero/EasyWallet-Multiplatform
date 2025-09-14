package org.easy.wallet.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import org.easy.wallet.datastore.IOSKeyStorePort
import org.easy.wallet.datastore.KeyStorePort
import org.easy.wallet.datastore.createDataStore
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

@OptIn(ExperimentalForeignApi::class)
internal actual val platformDataStoreModule: Module = module {
  single<DataStore<Preferences>> {
    createDataStore()
  }

  single { IOSKeyStorePort(get()) } bind KeyStorePort::class
}
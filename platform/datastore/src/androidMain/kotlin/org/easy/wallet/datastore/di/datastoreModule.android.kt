package org.easy.wallet.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.easy.wallet.datastore.AndroidKeyStorePort
import org.easy.wallet.datastore.KeyStorePort
import org.easy.wallet.datastore.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformDataStoreModule: Module = module {
  single<DataStore<Preferences>> {
    createDataStore(androidContext())
  }

  single { AndroidKeyStorePort(androidContext()) } bind KeyStorePort::class
}
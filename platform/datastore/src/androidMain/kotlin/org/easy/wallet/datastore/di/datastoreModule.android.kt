package org.easy.wallet.datastore.di

import org.easy.wallet.datastore.AndroidKeyStorePort
import org.easy.wallet.datastore.DATA_STORE_FILE_NAME
import org.easy.wallet.datastore.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformDataStoreModule: Module = module {
  single {
    createDataStore {
      androidContext().filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
    }
  }
  single { AndroidKeyStorePort(androidContext()) }
}
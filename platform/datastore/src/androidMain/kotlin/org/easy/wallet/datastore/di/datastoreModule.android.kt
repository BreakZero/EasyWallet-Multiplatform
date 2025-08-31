package org.easy.wallet.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import org.easy.wallet.datastore.AndroidKeyStorePort
import org.easy.wallet.datastore.DATA_STORE_FILE_NAME
import org.easy.wallet.datastore.KeyStorePort
import org.easy.wallet.datastore.USER_PREFERENCES_NAME
import org.easy.wallet.datastore.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformDataStoreModule: Module = module {
  single {
    createDataStore {
      androidContext().filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
    }
  }

  single<DataStore<Preferences>> {
    PreferenceDataStoreFactory.create(
      produceFile = {
        androidContext().filesDir.resolve(USER_PREFERENCES_NAME)
      }
    )
  }

  single { AndroidKeyStorePort(androidContext()) } bind KeyStorePort::class
}
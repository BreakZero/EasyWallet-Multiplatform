package org.easy.wallet.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import org.easy.wallet.datastore.DATA_STORE_FILE_NAME
import org.easy.wallet.datastore.IOSKeyStorePort
import org.easy.wallet.datastore.KeyStorePort
import org.easy.wallet.datastore.USER_PREFERENCES_NAME
import org.easy.wallet.datastore.createDataStore
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask


@OptIn(ExperimentalForeignApi::class)
private fun documentsPath(filename: String): String {
  val dir = NSFileManager.defaultManager.URLForDirectory(
    directory = NSDocumentDirectory,
    inDomain = NSUserDomainMask,
    appropriateForURL = null,
    create = false,
    error = null
  )
  return requireNotNull(dir?.URLByAppendingPathComponent(filename)?.path)
}

@OptIn(ExperimentalForeignApi::class)
internal actual val platformDataStoreModule: Module = module {
  single {
    createDataStore {
      val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
      )
      requireNotNull(documentDirectory).path + "/$DATA_STORE_FILE_NAME"
    }

  }

  single<DataStore<Preferences>> {
    PreferenceDataStoreFactory.createWithPath(
      corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
      produceFile = { documentsPath(USER_PREFERENCES_NAME).toPath() }
    )
  }

  single { IOSKeyStorePort() } bind KeyStorePort::class
}
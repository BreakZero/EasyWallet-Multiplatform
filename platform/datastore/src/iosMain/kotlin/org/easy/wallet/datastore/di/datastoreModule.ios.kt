package org.easy.wallet.datastore.di

import kotlinx.cinterop.ExperimentalForeignApi
import org.easy.wallet.datastore.DATA_STORE_FILE_NAME
import org.easy.wallet.datastore.IOSKeyStorePort
import org.easy.wallet.datastore.createDataStore
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

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

  single { IOSKeyStorePort() }
}
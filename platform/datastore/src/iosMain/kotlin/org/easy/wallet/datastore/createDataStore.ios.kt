@file:OptIn(ExperimentalForeignApi::class)

package org.easy.wallet.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

internal fun documentsPath(filename: String): String {
  val dir = NSFileManager.defaultManager.URLForDirectory(
    directory = NSDocumentDirectory,
    inDomain = NSUserDomainMask,
    appropriateForURL = null,
    create = false,
    error = null
  )
  return requireNotNull(dir).path + "/$filename"
}

internal fun createDataStore(): DataStore<Preferences> = createDataStore(
  producePath = { documentsPath(DATA_STORE_FILENAME) }
)
package org.easy.wallet.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

internal fun createDataStore(context: Context): DataStore<Preferences> = createDataStore(
  producePath = { context.filesDir.resolve(DATA_STORE_FILENAME).absolutePath }
)
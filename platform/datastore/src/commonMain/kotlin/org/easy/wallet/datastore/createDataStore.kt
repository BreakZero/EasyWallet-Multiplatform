package org.easy.wallet.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import okio.Path.Companion.toPath

fun createDataStore(producePath: () -> String): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
  corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
  produceFile = { producePath().toPath() }
)

internal const val dataStoreFileName = "dice.preferences_pb"
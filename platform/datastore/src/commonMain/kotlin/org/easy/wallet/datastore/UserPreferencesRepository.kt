package org.easy.wallet.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.easy.wallet.datastore.model.DefaultPreferences
import org.easy.wallet.datastore.model.UserPreferences

class UserPreferencesRepository internal constructor(
  private val dataStore: DataStore<Preferences>
) : PreferencesRepository {
  private object K {
    val PASS_CODE = stringPreferencesKey("user.passcode")
    val LANGUAGE = stringPreferencesKey("prefs.language")
  }

  override val preferences: Flow<UserPreferences>
    get() = dataStore.data.map {
      UserPreferences(
        language = it[K.LANGUAGE] ?: DefaultPreferences.language,
        passcode = it[K.PASS_CODE] ?: DefaultPreferences.passcode
      )
    }

  override suspend fun update(transform: (UserPreferences) -> UserPreferences) {
    val prefs = preferences.first()
    val nv = transform(prefs)
    writeAll(nv)
  }

  override suspend fun set(prefs: UserPreferences) = writeAll(prefs)

  override suspend fun clear() {
    dataStore.edit { it.clear() }
    writeAll(DefaultPreferences)
  }

  private suspend fun writeAll(p: UserPreferences) {
    dataStore.edit { e ->
      e[K.LANGUAGE] = p.language
      e[K.PASS_CODE] = p.passcode
    }
  }
}
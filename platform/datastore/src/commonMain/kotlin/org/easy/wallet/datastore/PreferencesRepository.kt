package org.easy.wallet.datastore

import kotlinx.coroutines.flow.Flow
import org.easy.wallet.datastore.model.UserPreferences

interface PreferencesRepository {
  val preferences: Flow<UserPreferences>

  suspend fun update(transform: (UserPreferences) -> UserPreferences)

  suspend fun set(prefs: UserPreferences)

  suspend fun clear()
}
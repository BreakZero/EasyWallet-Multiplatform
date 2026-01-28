package org.easy.wallet.feature.apps.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.easy.wallet.feature.apps.DAppsScreen
import kotlinx.serialization.Serializable

@Serializable
data object DAppsRoute : NavKey

fun EntryProviderScope<NavKey>.appsSection(
  appsNestedGraph: EntryProviderScope<NavKey>.() -> Unit
) {
  entry<DAppsRoute> {
    DAppsScreen()
  }
  appsNestedGraph()
}
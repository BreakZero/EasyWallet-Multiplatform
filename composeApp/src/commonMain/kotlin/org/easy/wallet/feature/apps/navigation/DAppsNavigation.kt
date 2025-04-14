package org.easy.wallet.feature.apps.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import org.easy.wallet.feature.apps.DAppsScreen

@Serializable
data object DAppsBaseRoute

@Serializable
data object DAppsRoute

fun NavController.navigateToDApps(navOptions: NavOptions) = navigate(route = DAppsRoute, navOptions)

fun NavGraphBuilder.appsSection(appsNestedGraph: NavGraphBuilder.() -> Unit,) {
  navigation<DAppsBaseRoute>(startDestination = DAppsRoute) {
    composable<DAppsRoute> {
      DAppsScreen()
    }
    appsNestedGraph()
  }
}
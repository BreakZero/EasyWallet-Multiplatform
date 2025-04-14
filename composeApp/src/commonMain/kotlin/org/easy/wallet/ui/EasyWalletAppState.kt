package org.easy.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import kotlinx.coroutines.CoroutineScope
import org.easy.wallet.feature.account.navigation.navigateToAccount
import org.easy.wallet.feature.assets.navigation.navigateToAssets
import org.easy.wallet.feature.news.navigation.navigateToNews
import org.easy.wallet.navhost.TopLevelDestination

@Composable
fun rememberAppState(
  navController: NavHostController = rememberNavController(),
  coroutineScope: CoroutineScope = rememberCoroutineScope()
): EasyAppState = remember(navController) {
  EasyAppState(navController, coroutineScope)
}

@Stable
class EasyAppState(
  val navController: NavHostController,
  coroutineScope: CoroutineScope
) {
  private val previousDestination = mutableStateOf<NavDestination?>(null)

  val currentDestination: NavDestination?
    @Composable get() {
      val currentEntry = navController.currentBackStackEntryFlow
        .collectAsState(initial = null)

      return currentEntry.value?.destination.also { destination ->
        if (destination != null) {
          previousDestination.value = destination
        }
      } ?: previousDestination.value
    }

  val currentTopLevelDestination: TopLevelDestination?
    @Composable get() {
      return TopLevelDestination.entries.firstOrNull { topLevelDestination ->
        currentDestination?.hasRoute(route = topLevelDestination.route) == true
      }
    }

  val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

  fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
    val topLevelNavOptions = navOptions {
      popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
      }
      launchSingleTop = true
      restoreState = true
    }

    when (topLevelDestination) {
      TopLevelDestination.Assets -> navController.navigateToAssets(topLevelNavOptions)
      TopLevelDestination.News -> navController.navigateToNews(topLevelNavOptions)
//      TopLevelDestination.DApps -> navController.navigateToDApps(topLevelNavOptions)
      TopLevelDestination.Account -> navController.navigateToAccount(topLevelNavOptions)
    }
  }
}
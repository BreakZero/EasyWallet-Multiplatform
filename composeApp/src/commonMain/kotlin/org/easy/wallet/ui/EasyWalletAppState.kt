package org.easy.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import org.easy.wallet.feature.account.navigation.AccountRoute
import org.easy.wallet.feature.assets.navigation.AssetsRoute
import org.easy.wallet.feature.news.navigation.NewsRoute
import org.easy.wallet.navhost.NavigationState
import org.easy.wallet.navhost.Navigator
import org.easy.wallet.navhost.TopLevelDestination
import org.easy.wallet.navhost.rememberNavigationState

@Composable
fun rememberAppState(): EasyAppState {
  val topLevelRoutes = setOf(
    AssetsRoute,
    NewsRoute,
    AccountRoute
  )
  val navigationState = rememberNavigationState(
    startRoute = AssetsRoute,
    topLevelRoutes = topLevelRoutes
  )
  val navigator = remember { Navigator(navigationState) }
  return remember(navigationState, navigator, topLevelRoutes) {
    EasyAppState(navigationState, navigator, topLevelRoutes)
  }
}

@Stable
class EasyAppState(
  val navigationState: NavigationState,
  val navigator: Navigator,
  private val topLevelRoutes: Set<NavKey>
) {
  val currentTopLevelDestination: TopLevelDestination?
    get() {
      val currentRoute = navigationState.topLevelRoute
      return TopLevelDestination.entries.firstOrNull { topLevelDestination ->
        topLevelDestination.route == currentRoute
      }
    }

  val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

  /**
   * Returns true if the NavigationBar should be visible.
   * The NavigationBar is visible only when the current route is a top-level route.
   * This is a Composable property to ensure it's properly tracked when navigation state changes.
   */
  @Composable
  fun shouldShowNavigationBar(): Boolean = navigationState.isCurrentRouteTopLevel(topLevelRoutes)

  fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
    navigator.navigate(topLevelDestination.route)
  }
}
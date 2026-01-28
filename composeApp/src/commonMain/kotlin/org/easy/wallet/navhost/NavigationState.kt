package org.easy.wallet.navhost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.easy.wallet.feature.account.navigation.AccountRoute
import org.easy.wallet.feature.apps.navigation.DAppsRoute
import org.easy.wallet.feature.assets.navigation.AssetsRoute
import org.easy.wallet.feature.news.navigation.NewsRoute

/**
 * Creates the SavedStateConfiguration with polymorphic serialization for top-level NavKey types.
 * This is required for multiplatform support (iOS, web) where reflection-based serialization is not available.
 * Only top-level routes need to be registered here, as nested routes are handled automatically by the back stack.
 */
@Composable
fun rememberNavKeySavedStateConfiguration(): SavedStateConfiguration {
  return remember {
    SavedStateConfiguration {
      serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
          // Top-level routes only
          subclass(AssetsRoute::class, AssetsRoute.serializer())
          subclass(NewsRoute::class, NewsRoute.serializer())
          subclass(AccountRoute::class, AccountRoute.serializer())
          subclass(DAppsRoute::class, DAppsRoute.serializer())
        }
      }
    }
  }
}

/**
 * Create a navigation state that persists config changes and process death.
 */
@Composable
fun rememberNavigationState(
  startRoute: NavKey,
  topLevelRoutes: Set<NavKey>
): NavigationState {
  val config = rememberNavKeySavedStateConfiguration()
  val topLevelRoute = remember {
    mutableStateOf(startRoute)
  }
  val backStacks = topLevelRoutes.associateWith { key ->
    rememberNavBackStack(config, key)
  }
  return remember(startRoute, topLevelRoutes) {
    NavigationState(
      startRoute = startRoute,
      topLevelRoute = topLevelRoute,
      backStacks = backStacks
    )
  }
}

/**
 * State holder for navigation state.
 *
 * @param startRoute - the start route. The user will exit the app through this route.
 * @param topLevelRoute - the current top level route
 * @param backStacks - the back stacks for each top level route
 */
class NavigationState(
  val startRoute: NavKey,
  topLevelRoute: MutableState<NavKey>,
  val backStacks: Map<NavKey, NavBackStack<NavKey>>
) {
  var topLevelRoute: NavKey by topLevelRoute

  val stacksInUse: List<NavKey>
    get() = if (topLevelRoute == startRoute) {
      listOf(startRoute)
    } else {
      listOf(startRoute, topLevelRoute)
    }

  /**
   * Returns the current route (the last item in the current back stack).
   */
  val currentRoute: NavKey
    get() = backStacks[topLevelRoute]?.lastOrNull() ?: topLevelRoute

  /**
   * Checks if the current route is a top-level route.
   */
  fun isCurrentRouteTopLevel(topLevelRoutes: Set<NavKey>): Boolean {
    return currentRoute in topLevelRoutes
  }

  /**
   * Convert NavigationState into NavEntries.
   */
  @Composable
  fun toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>
  ): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = backStacks.mapValues { (_, stack) ->
      val decorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
      )
      rememberDecoratedNavEntries(
        backStack = stack,
        entryDecorators = decorators,
        entryProvider = entryProvider
      )
    }
    return stacksInUse.flatMap { decoratedEntries[it] ?: emptyList() }
      .toMutableStateList()
  }
}

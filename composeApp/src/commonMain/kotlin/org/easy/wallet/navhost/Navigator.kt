package org.easy.wallet.navhost

import androidx.navigation3.runtime.NavKey

/**
 * Handles navigation events (forward and back) by updating the navigation state.
 */
class Navigator(
  val state: NavigationState
) {
  fun navigate(route: NavKey) {
    if (route in state.backStacks.keys) {
      // This is a top level route, just switch to it.
      state.topLevelRoute = route
    } else {
      state.backStacks[state.topLevelRoute]?.add(route)
    }
  }

  fun goBack() {
    val currentStack = state.backStacks[state.topLevelRoute]
      ?: error("Stack for ${state.topLevelRoute} not found")
    val currentRoute = currentStack.last()
    if (currentRoute == state.topLevelRoute) {
      state.topLevelRoute = state.startRoute
    } else {
      currentStack.removeLastOrNull()
    }
  }

  fun popBackTo(predicate: (NavKey) -> Boolean, inclusive: Boolean = false) {
    val currentStack = state.backStacks[state.topLevelRoute]
      ?: error("Stack for ${state.topLevelRoute} not found")
    while (currentStack.size > 1) {
      val last = currentStack.last()
      if (predicate(last)) {
        if (inclusive) currentStack.removeLastOrNull()
        return
      }
      currentStack.removeLastOrNull()
    }
  }
}
package org.easy.wallet.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import org.easy.wallet.feature.assets.navigation.AssetsBaseRoute
import org.easy.wallet.navhost.WalletNavHost
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.reflect.KClass

@OptIn(ExperimentalHazeApi::class)
@Composable
fun EasyWalletApp(appState: EasyAppState) {
  val currentDestination = appState.currentDestination
  val hazeState = remember { HazeState() }
  val style = HazeStyle(
    blurRadius = 20.dp,
    backgroundColor = MaterialTheme.colorScheme.surface,
    tint = HazeTint(
      MaterialTheme.colorScheme.surface.copy(alpha = if (MaterialTheme.colorScheme.surface.luminance() >= 0.5) 0.56f else 0.62f)
    )
  )

  Box(modifier = Modifier.fillMaxSize()) {
    WalletNavHost(
      modifier = Modifier.fillMaxSize().hazeSource(hazeState),
      navController = appState.navController,
      startDestination = AssetsBaseRoute
    )

    AnimatedVisibility(
      modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
      visible = appState.currentTopLevelDestination != null,
      enter = slideInVertically { it },
      exit = slideOutVertically { it }
    ) {
      NavigationBar(
        modifier = Modifier
          .hazeEffect(state = hazeState, style = style) {
            this.inputScale = HazeInputScale.Default
          }.fillMaxWidth(),
        containerColor = Color.Transparent
      ) {
        appState.topLevelDestinations.forEach { destination ->
          val selected = currentDestination
            .isRouteInHierarchy(destination.baseRoute)
          NavigationBarItem(
            selected = selected,
            onClick = { appState.navigateToTopLevelDestination(destination) },
            label = { Text(stringResource(destination.titleTextId)) },
            icon = {
              Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(destination.selectedIcon),
                contentDescription = null
              )
            }
          )
        }
      }
    }
  }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) = this?.hierarchy?.any {
  it.hasRoute(route)
} ?: false
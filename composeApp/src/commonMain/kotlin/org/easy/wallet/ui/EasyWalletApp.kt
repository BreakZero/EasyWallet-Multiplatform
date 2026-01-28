package org.easy.wallet.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.ExperimentalHazeApi
import org.easy.wallet.navhost.WalletNavHost
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalHazeApi::class)
@Composable
fun EasyWalletApp(appState: EasyAppState) {
  EasyWalletTheme {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      containerColor = Color.Transparent,
      contentWindowInsets = WindowInsets(0, 0, 0, 0),
      bottomBar = {
        AnimatedVisibility(
          modifier = Modifier.fillMaxWidth(),
          visible = appState.shouldShowNavigationBar(),
          enter = slideInVertically { it },
          exit = slideOutVertically { it }
        ) {
          NavigationBar(
            modifier = Modifier
              .fillMaxWidth()
              .shadow(1.dp),
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.66f),
            tonalElevation = 3.dp
          ) {
            appState.topLevelDestinations.forEach { destination ->
              val selected = destination.route == appState.navigationState.topLevelRoute
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
    ) {
      WalletNavHost(
        modifier = Modifier.fillMaxSize().padding(it),
        navigationState = appState.navigationState,
        navigator = appState.navigator
      )
    }
  }
}
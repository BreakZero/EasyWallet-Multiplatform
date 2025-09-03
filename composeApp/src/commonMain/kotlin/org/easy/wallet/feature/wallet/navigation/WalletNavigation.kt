package org.easy.wallet.feature.wallet.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.easy.wallet.feature.wallet.WalletOptionScreen
import org.easy.wallet.feature.wallet.create.GenerateSeedScreen
import org.easy.wallet.feature.wallet.passcode.CreatePassCodeScreen
import org.easy.wallet.feature.wallet.restore.WalletRestoreScreen

@Serializable
data object WalletOptionRoute

@Serializable
data class SetPassCodeRoute(val isRestore: Boolean)

@Serializable
data class GenerateSeedRoute(val passcode: String)

@Serializable
data class WalletRestoreRoute(val passcode: String)


fun NavGraphBuilder.attachWalletGraph(navController: NavController) {
  composable<WalletOptionRoute> {
    WalletOptionScreen(
      onCreateWallet = { navController.navigate(route = SetPassCodeRoute(isRestore = false)) },
      onRestoreWallet = { navController.navigate(route = SetPassCodeRoute(isRestore = true)) },
      popBackStack = navController::popBackStack
    )
  }

  composable<SetPassCodeRoute> {
    val route: SetPassCodeRoute = it.toRoute<SetPassCodeRoute>()
    CreatePassCodeScreen(
      popBackStack = navController::popBackStack,
      toNext = { passcode ->
        navController.navigate(
          route = if (route.isRestore) WalletRestoreRoute(passcode) else GenerateSeedRoute(passcode)
        ) {
          popUpTo(route) {
            inclusive = true
          }
        }
      }
    )
  }

  composable<GenerateSeedRoute> {
    val passcode = it.toRoute<GenerateSeedRoute>().passcode
    GenerateSeedScreen(
      passcode = passcode,
      popBackStack = navController::popBackStack,
    )
  }

  composable<WalletRestoreRoute> {
    WalletRestoreScreen()
  }
}
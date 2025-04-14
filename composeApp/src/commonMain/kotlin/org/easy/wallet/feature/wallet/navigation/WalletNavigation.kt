package org.easy.wallet.feature.wallet.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.easy.wallet.feature.wallet.WalletOptionScreen
import org.easy.wallet.feature.wallet.create.GenerateSeedScreen
import org.easy.wallet.feature.wallet.create.SetPasswordScreen
import org.easy.wallet.feature.wallet.restore.WalletRestoreScreen

@Serializable
data object WalletOptionRoute

@Serializable
data object SetPasswordRoute

@Serializable
data object GenerateSeedRoute

@Serializable
data object WalletRestoreRoute

fun NavGraphBuilder.attachWalletGraph(navController: NavController) {
  composable<WalletOptionRoute> {
    WalletOptionScreen(
      onCreateWallet = { navController.navigate(SetPasswordRoute) },
      onRestoreWallet = { navController.navigate(SetPasswordRoute) },
      popBackStack = navController::popBackStack
    )
  }

  composable<SetPasswordRoute> {
    SetPasswordScreen(
      onContinue = {
        navController.navigate(GenerateSeedRoute)
      }
    )
  }

  composable<GenerateSeedRoute> {
    GenerateSeedScreen()
  }

  composable<WalletRestoreRoute> {
    WalletRestoreScreen()
  }
}
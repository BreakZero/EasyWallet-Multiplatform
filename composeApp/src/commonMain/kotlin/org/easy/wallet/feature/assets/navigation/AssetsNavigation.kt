package org.easy.wallet.feature.assets.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import org.easy.wallet.feature.assets.AssetsScreen
import org.easy.wallet.feature.wallet.navigation.WalletRestoreRoute

@Serializable
data object AssetsBaseRoute

@Serializable
data object AssetsRoute

fun NavController.navigateToAssets(navOptions: NavOptions) = navigate(route = AssetsRoute, navOptions)

fun NavGraphBuilder.assetsSection(navController: NavController, assertNestedGraph: NavGraphBuilder.() -> Unit,) {
  navigation<AssetsBaseRoute>(startDestination = AssetsRoute) {
    composable<AssetsRoute> {
      AssetsScreen(
        onAssetClick = {},
        onCreateWallet = {},
        onRestoreWallet = { navController.navigate(WalletRestoreRoute) }
      )
    }
    assertNestedGraph()
  }
}
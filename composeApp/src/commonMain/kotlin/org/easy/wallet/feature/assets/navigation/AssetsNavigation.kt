package org.easy.wallet.feature.assets.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.easy.wallet.feature.assets.AssetsScreen
import org.easy.wallet.feature.assets.detail.AssetDetailScreen
import org.easy.wallet.feature.wallet.navigation.SetPassCodeRoute
import org.easy.wallet.model.TokenId

@Serializable
data object AssetsBaseRoute

@Serializable
data object AssetsRoute

@Serializable
private data class AssetDetailRoute(
  val tokenId: String
)

fun NavController.navigateToAssets(navOptions: NavOptions? = null) = navigate(route = AssetsRoute, navOptions)

fun NavController.navigateToAssetDetail(tokenId: TokenId, navOptions: NavOptions? = null) =
  navigate(route = AssetDetailRoute(tokenId.value), navOptions)

fun NavGraphBuilder.assetsSection(navController: NavController, assertNestedGraph: NavGraphBuilder.() -> Unit,) {
  navigation<AssetsBaseRoute>(startDestination = AssetsRoute) {
    composable<AssetsRoute> {
      AssetsScreen(
        onAssetClick = { navController.navigateToAssetDetail(tokenId = it.id) },
        onCreateWallet = { navController.navigate(route = SetPassCodeRoute(false)) },
        onRestoreWallet = { navController.navigate(SetPassCodeRoute(true)) }
      )
    }

    composable<AssetDetailRoute> {
      val route = it.toRoute<AssetDetailRoute>()
      AssetDetailScreen(
        tokenId = TokenId(route.tokenId),
        popup = navController::popBackStack
      )
    }
    assertNestedGraph()
  }
}
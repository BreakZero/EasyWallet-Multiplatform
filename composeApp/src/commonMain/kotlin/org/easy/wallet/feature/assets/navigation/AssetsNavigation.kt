package org.easy.wallet.feature.assets.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.easy.wallet.feature.assets.AssetsScreen
import org.easy.wallet.feature.assets.detail.AssetDetailScreen
import org.easy.wallet.feature.send.navigation.navigateToSendFlow
import org.easy.wallet.feature.wallet.navigation.SetPassCodeRoute
import org.easy.wallet.model.TokenId
import org.easy.wallet.navhost.Navigator

@Serializable
data object AssetsRoute : NavKey

@Serializable
internal data class AssetDetailRoute(
  val tokenId: String
) : NavKey

private fun Navigator.navigateToAssetDetail(tokenId: TokenId) = navigate(AssetDetailRoute(tokenId.value))

fun EntryProviderScope<NavKey>.assetsSection(navigator: Navigator) {
  entry<AssetsRoute> {
    AssetsScreen(
      onAssetClick = { navigator.navigateToAssetDetail(tokenId = it.id) },
      onCreateWallet = { navigator.navigate(SetPassCodeRoute(false)) },
      onRestoreWallet = { navigator.navigate(SetPassCodeRoute(true)) }
    )
  }

  entry<AssetDetailRoute> { key ->
    AssetDetailScreen(
      tokenId = TokenId(key.tokenId),
      onSend = { tokenId -> navigator.navigateToSendFlow(tokenId) },
      onPopBack = navigator::goBack
    )
  }
}
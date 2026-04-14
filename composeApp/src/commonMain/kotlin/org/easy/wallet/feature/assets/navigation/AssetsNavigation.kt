package org.easy.wallet.feature.assets.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.easy.wallet.feature.assets.AssetsScreen
import org.easy.wallet.feature.assets.detail.AssetDetailScreen
import org.easy.wallet.feature.send.navigation.navigateToSendFlow
import org.easy.wallet.feature.wallet.navigation.SetPassCodeRoute
import org.easy.wallet.model.AssetId
import org.easy.wallet.model.SupportedAsset
import org.easy.wallet.navhost.Navigator

@Serializable
data object AssetsRoute : NavKey

@Serializable
internal data class AssetDetailRoute(
  val assetId: String
) : NavKey

private fun Navigator.navigateToAssetDetail(asset: SupportedAsset) = navigate(AssetDetailRoute(asset.id.value))

fun EntryProviderScope<NavKey>.assetsSection(navigator: Navigator) {
  entry<AssetsRoute> {
    AssetsScreen(
      onAssetClick = { navigator.navigateToAssetDetail(it) },
      onCreateWallet = { navigator.navigate(SetPassCodeRoute(false)) },
      onRestoreWallet = { navigator.navigate(SetPassCodeRoute(true)) }
    )
  }

  entry<AssetDetailRoute> { key ->
    AssetDetailScreen(
      assetId = AssetId(key.assetId),
      onSend = { assetId -> navigator.navigateToSendFlow(assetId) },
      onPopBack = navigator::goBack
    )
  }
}

package org.easy.wallet.feature.assets

import org.easy.wallet.model.TokenMeta

sealed interface AssetEvent {
  data object OnRestoreWallet : AssetEvent

  data object OnCreateWallet : AssetEvent

  data class OnItemClick(
    val asset: TokenMeta
  ) : AssetEvent
}
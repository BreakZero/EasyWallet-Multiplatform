package org.easy.wallet.feature.assets.detail

import org.easy.wallet.model.AssetId

sealed interface AssetDetailEvent {
  data object OnPopBack : AssetDetailEvent

  data class OnSend(
    val assetId: AssetId
  ) : AssetDetailEvent

  data object OnReceive : AssetDetailEvent
}

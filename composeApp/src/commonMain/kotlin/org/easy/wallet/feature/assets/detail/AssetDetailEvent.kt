package org.easy.wallet.feature.assets.detail

import org.easy.wallet.model.TokenId

sealed interface AssetDetailEvent {
  data object OnPopBack : AssetDetailEvent

  data class OnSend(
    val tokenId: TokenId
  ) : AssetDetailEvent

  data object OnReceive : AssetDetailEvent
}
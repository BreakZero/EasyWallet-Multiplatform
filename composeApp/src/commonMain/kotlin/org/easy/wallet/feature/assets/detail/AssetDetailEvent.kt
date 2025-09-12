package org.easy.wallet.feature.assets.detail

sealed interface AssetDetailEvent {
  data object Popup : AssetDetailEvent

  data object OnSend : AssetDetailEvent

  data object OnReceive : AssetDetailEvent
}
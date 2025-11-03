package org.easy.wallet.feature.send

sealed interface SendFlowEvent {
  data class OnError(
    val error: String
  ) : SendFlowEvent

  data object Popup : SendFlowEvent

  data class NavigateTo(
    val route: String
  ) : SendFlowEvent
}
package org.easy.wallet.feature.send

sealed interface SendFlowEvent {
  data object GoBack : SendFlowEvent

  data object NavigateToEnterAmount : SendFlowEvent

  data object NavigateToReview : SendFlowEvent

  data object NavigateToResult : SendFlowEvent

  data object NavigateToHome : SendFlowEvent

  data class ShowError(
    val message: String
  ) : SendFlowEvent
}
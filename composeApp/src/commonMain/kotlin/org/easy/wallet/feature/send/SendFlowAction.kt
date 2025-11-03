package org.easy.wallet.feature.send

sealed interface SendFlowAction {
  data object Popup : SendFlowAction

  data class OnRecipientChange(
    val recipient: String
  ) : SendFlowAction

  data class OnSendAmountChange(
    val amount: String
  ) : SendFlowAction

  data class OnNext(
    val route: String
  ) : SendFlowAction
}
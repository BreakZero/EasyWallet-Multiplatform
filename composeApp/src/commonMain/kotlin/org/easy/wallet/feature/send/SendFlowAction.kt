package org.easy.wallet.feature.send

sealed interface SendFlowAction {
  data object GoBack : SendFlowAction

  data class OnRecipientChange(
    val recipient: String
  ) : SendFlowAction

  data object ContinueToAmount : SendFlowAction

  data class OnSendAmountChange(
    val amount: String
  ) : SendFlowAction

  data class OnMemoChange(
    val memo: String
  ) : SendFlowAction

  data object UseMaxAmount : SendFlowAction

  data object ReviewTransaction : SendFlowAction

  data object ConfirmSend : SendFlowAction

  data object DismissResult : SendFlowAction
}
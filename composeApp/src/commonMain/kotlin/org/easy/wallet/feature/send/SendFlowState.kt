package org.easy.wallet.feature.send

import androidx.compose.runtime.Stable
import org.easy.wallet.model.FeePolicy
import org.easy.wallet.model.AssetBalance

@Stable
data class SendFlowState(
  val assetBalance: AssetBalance? = null,
  val isLoading: Boolean = true,
  val recipientAddress: String = "",
  val addressError: AddressError? = null,
  val amount: String = "",
  val amountError: AmountError? = null,
  val memo: String = "",
  val feePolicy: FeePolicy? = null,
  val isEstimatingFee: Boolean = false,
  val isSending: Boolean = false,
  val sendResult: SendResult? = null,
  val error: String? = null
) {
  val isAddressValid: Boolean
    get() = recipientAddress.isNotBlank() && addressError == null

  val isAmountValid: Boolean
    get() = amount.isNotBlank() && amountError == null
}

enum class AddressError {
  EMPTY,
  INVALID_FORMAT,
  SAME_AS_SENDER
}

enum class AmountError {
  EMPTY,
  INVALID_FORMAT,
  ZERO,
  EXCEEDS_BALANCE
}

sealed interface SendResult {
  data class Success(
    val txHash: String
  ) : SendResult

  data class Failure(
    val message: String
  ) : SendResult
}

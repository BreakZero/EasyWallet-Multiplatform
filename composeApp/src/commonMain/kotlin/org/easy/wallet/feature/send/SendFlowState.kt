package org.easy.wallet.feature.send

import androidx.compose.runtime.Stable
import org.easy.wallet.model.TokenHolding

@Stable
data class SendFlowState(
  val tokenHolding: TokenHolding? = null,
  val isLoading: Boolean = true,
  val recipientAddress: String? = null,
  val amount: String? = null,
  val memo: String? = null,
  val error: String? = null
)
package org.easy.wallet.feature.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustwallet.core.AnySigner
import com.trustwallet.core.CoinType
import com.trustwallet.core.ethereum.SigningInput
import com.trustwallet.core.ethereum.SigningOutput
import com.trustwallet.core.ethereum.Transaction
import com.trustwallet.core.sign
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import okio.ByteString.Companion.decodeHex
import org.easy.wallet.domain.FetchTokenInformationUseCase
import org.easy.wallet.model.TokenId

class SendFlowViewModel(
  private val fetchTokenInformationUseCase: FetchTokenInformationUseCase,
  private val tokenId: TokenId
) : ViewModel() {
  private val eventChannel = Channel<SendFlowEvent>()
  val event = eventChannel.receiveAsFlow()

  private val _uiState = MutableStateFlow(SendFlowState())
  val uiState: StateFlow<SendFlowState> = _uiState.asStateFlow()

  init {
    loadTokenInformation()
    signTransaction()
  }

  private fun loadTokenInformation() {
    fetchTokenInformationUseCase(tokenId)
      .onEach { tokenHolding ->
        _uiState.value = _uiState.value.copy(
          tokenHolding = tokenHolding,
          isLoading = false,
          error = null
        )
      }.launchIn(viewModelScope)
  }

  fun handleAction(action: SendFlowAction) {
    when (action) {
      is SendFlowAction.OnRecipientChange -> {
        _uiState.update { it.copy(recipientAddress = action.recipient) }
      }

      is SendFlowAction.OnSendAmountChange -> {
        _uiState.update { it.copy(amount = action.amount) }
      }

      SendFlowAction.Popup -> {
        eventChannel.trySend(SendFlowEvent.Popup)
      }

      is SendFlowAction.OnNext -> {
        eventChannel.trySend(SendFlowEvent.NavigateTo(route = action.route))
      }
    }
  }

  private fun signTransaction() {
    val signingInput = SigningInput(
      private_key = "4646464646464646464646464646464646464646464646464646464646464646".decodeHex(),
      to_address = "0x3535353535353535353535353535353535353535",
      chain_id = "01".decodeHex(),
      nonce = "21".decodeHex(),
      gas_price = "04a817c800".decodeHex(),
      gas_limit = "5208".decodeHex(),
      transaction = Transaction(
        transfer = Transaction.Transfer(
          amount = "0de0b6b3a7640000".decodeHex()
        )
      )
    )
    val output = AnySigner.sign(signingInput, CoinType.Ethereum, SigningOutput.ADAPTER)
    val encoded = "0x${output.encoded.hex()}"
    println("======= $encoded")
  }
}
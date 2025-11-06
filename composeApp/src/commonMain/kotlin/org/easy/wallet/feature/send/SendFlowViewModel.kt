package org.easy.wallet.feature.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustwallet.core.AnySigner
import com.trustwallet.core.BitcoinScript
import com.trustwallet.core.BitcoinSigHashType
import com.trustwallet.core.CoinType
import com.trustwallet.core.bitcoin.OutPoint
import com.trustwallet.core.bitcoin.SigningInput
import com.trustwallet.core.bitcoin.SigningOutput
import com.trustwallet.core.bitcoin.UnspentTransaction
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
    printInfo()
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

  private fun printInfo() {
    val input = SigningInput(
      amount = 55_000,
      hash_type = BitcoinSigHashType.All.value.toInt(),
      to_address = "1Bp9U1ogV3A14FMvKbRJms7ctyso4Z4Tcx",
      change_address = "1FQc5LdgGHMHEN9nwkjmz6tWkxhPpxBvBU",
      byte_fee = 10
    )
    BitcoinScript.lockScriptForAddress("1Bp9U1ogV3A14FMvKbRJms7ctyso4Z4Tcx", CoinType.Bitcoin)

    val utxoKey0 = "bbc27228ddcb9209d7fd6f36b02f7dfa6252af40bb2f1cbc7a557da8027ff866".decodeHex()
    val utxoKey1 = "619c335025c7f4012e556c2a58b2506e30b8511b53ade95ea316fd8c3286feb9".decodeHex()
    val input1 = input.copy(private_key = listOf(utxoKey0, utxoKey1))

    val output0 = OutPoint(
      hash = "fff7f7881a8099afa6940d42d1e7f6362bec38171ea3edf433541db4e4ad969f".decodeHex(),
      index = 0,
      sequence = Long.MAX_VALUE.toInt()
    )

    val utxo0 = UnspentTransaction(
      amount = 625_000_000,
      out_point = output0,
      script = "2103c9f4836b9a4f77fc0d81f7bcb01b7f1b35916864b9476c241ce9fc198bd25432ac".decodeHex()
    )

    val output1 = OutPoint(
      hash = "ef51e1b804cc89d182d279655c3aa89e815b1b309fe287d9b2b55d57b90ec68a".decodeHex(),
      index = 1,
      sequence = Long.MAX_VALUE.toInt()
    )

    val utxo1 = UnspentTransaction(
      amount = 600_000_000,
      out_point = output1,
      script = "00141d0f172a0ecb48aee1be1f2687d2963ae33f71a1".decodeHex()
    )

    val input2 = input1.copy(utxo = listOf(utxo0, utxo1))

    val output = AnySigner.sign(input2, CoinType.Bitcoin, SigningOutput.ADAPTER)

    val signedTransaction = output.transaction
    val encoded = output.encoded

    encoded.toByteArray().toHexString().also { println("===== $it") }
  }
}
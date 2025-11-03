package org.easy.wallet.feature.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
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
}
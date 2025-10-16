package org.easy.wallet.feature.send

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.easy.wallet.domain.FetchTokenInformationUseCase
import org.easy.wallet.model.TokenHolding
import org.easy.wallet.model.TokenId

data class SendFlowUiState(
  val tokenHolding: TokenHolding? = null,
  val isLoading: Boolean = true,
  val error: String? = null
)

class SendFlowViewModel(
  private val fetchTokenInformationUseCase: FetchTokenInformationUseCase,
  private val tokenId: TokenId
) : ViewModel() {
  init {
    println("=====$tokenId")
    Color.Transparent
  }

  private val _uiState = MutableStateFlow(SendFlowUiState())
  val uiState: StateFlow<SendFlowUiState> = _uiState.asStateFlow()

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

  fun getAvailableBalance(): String = _uiState.value.tokenHolding
    ?.amount
    ?.format() ?: "0.00"

  fun getTokenSymbol(): String = _uiState.value.tokenHolding
    ?.asset
    ?.symbol ?: ""
}
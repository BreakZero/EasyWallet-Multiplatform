package org.easy.wallet.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.easy.wallet.data.repository.WalletRepository

class AccountViewModel(
  private val walletRepository: WalletRepository
) : ViewModel() {
  val state = walletRepository
    .walletName()
    .map {
      AccountUiState.Info(walletName = it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3_000), AccountUiState.NoSetup)
}

sealed interface AccountUiState {
  data object NoSetup : AccountUiState

  data class Info(
    val walletName: String? = null
  ) : AccountUiState
}
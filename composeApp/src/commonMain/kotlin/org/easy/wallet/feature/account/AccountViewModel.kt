package org.easy.wallet.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.easy.wallet.data.repository.AccountRepositoryImpl
import org.easy.wallet.datastore.PreferencesRepository

class AccountViewModel(
  private val accountRepository: AccountRepositoryImpl,
  private val preferencesRepository: PreferencesRepository
) : ViewModel() {
  fun listAccounts() {
    viewModelScope.launch {
      accountRepository.listAccounts().onEach {
        println("===== $it")
      }
    }
  }

  val state = combine(
    accountRepository.getCurrentAccount(),
    preferencesRepository.preferences
  ) { account, preferences ->
    account?.let {
      AccountUiState.Info(
        walletName = it.name,
        debugMode = preferences.debugMode
      )
    } ?: AccountUiState.NoSetup
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3_000), AccountUiState.NoSetup)

  fun toggleDebugMode(enabled: Boolean) {
    viewModelScope.launch {
      preferencesRepository.update { it.copy(debugMode = enabled) }
    }
  }
}

sealed interface AccountUiState {
  data object NoSetup : AccountUiState

  data class Info(
    val walletName: String? = null,
    val debugMode: Boolean = false
  ) : AccountUiState
}
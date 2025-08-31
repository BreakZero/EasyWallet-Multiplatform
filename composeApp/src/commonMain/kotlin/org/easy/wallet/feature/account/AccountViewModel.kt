package org.easy.wallet.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustwallet.core.HDWallet
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.easy.wallet.data.repository.AccountRepositoryImpl
import org.easy.wallet.data.repository.TokenRepository
import org.easy.wallet.data.repository.WalletRepository

class AccountViewModel(
  walletRepository: WalletRepository,
  private val accountRepository: AccountRepositoryImpl,
  private val tokenRepository: TokenRepository
) : ViewModel() {

  init {
    viewModelScope.launch {
      tokenRepository.getByChain("evm:1", onlyEnabled = false)
        .onEach {
          println("====== $it")
        }
    }
  }

  fun listAccounts() {
    viewModelScope.launch {
      accountRepository.listAccounts().onEach {
        println("===== $it")
      }
    }
  }

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
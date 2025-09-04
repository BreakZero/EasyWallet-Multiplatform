package org.easy.wallet.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.easy.wallet.data.repository.AccountRepositoryImpl

class AccountViewModel(
  private val accountRepository: AccountRepositoryImpl,
) : ViewModel() {
  //  init {
//    viewModelScope.launch {
//      tokenRepository.upsert(
//        Token(
//          tokenId = "btc:main/native",
//          chainId = ChainId.BTC_MAINNET,
//          standard = TokenStandard.NATIVE,
//          contract = null,
//          symbol = "BTC",
//          name = "Bitcoin",
//          decimals = 8,
//          iconUrl = "https://assets.coingecko.com/coins/images/1/thumb/bitcoin.png",
//          enabled = true,
//          sortOrder = 1,
//          createdAt = 0,
//          updatedAt = 0
//        )
//      )
//    }
//  }

  fun listAccounts() {
    viewModelScope.launch {
      accountRepository.listAccounts().onEach {
        println("===== $it")
      }
    }
  }

  val state = accountRepository
    .getCurrentAccount()
    .map { account ->
      account?.let {
        AccountUiState.Info(walletName = it.name)
      } ?: AccountUiState.NoSetup
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3_000), AccountUiState.NoSetup)
}

sealed interface AccountUiState {
  data object NoSetup : AccountUiState

  data class Info(
    val walletName: String? = null
  ) : AccountUiState
}
package org.easy.wallet.feature.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.easy.wallet.data.repository.AccountRepositoryImpl
import org.easy.wallet.domain.LoadAllBalancesUseCase
import org.easy.wallet.model.Balance

@OptIn(ExperimentalCoroutinesApi::class)
class AssetsViewModel(
  val accountRepository: AccountRepositoryImpl,
  val allBalancesUseCase: LoadAllBalancesUseCase
) : ViewModel() {
  val state = accountRepository
    .getCurrentAccount()
    .map { account ->
      if (account != null) {
        val balances = allBalancesUseCase(account)
        AssetsUiState.WalletAssets(
          walletName = account.name,
          assets = balances
        )
      } else {
        AssetsUiState.EmptyWallet
      }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3_000), AssetsUiState.Fetching)
}

sealed interface AssetsUiState {
  data object Fetching : AssetsUiState

  data object EmptyWallet : AssetsUiState

  data class WalletAssets(
    val walletName: String,
    val assets: List<Balance>
  ) : AssetsUiState
}
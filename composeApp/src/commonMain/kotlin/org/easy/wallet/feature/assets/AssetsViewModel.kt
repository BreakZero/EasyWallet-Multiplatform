package org.easy.wallet.feature.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.easy.wallet.data.repository.AccountRepositoryImpl
import org.easy.wallet.domain.LoadAllBalancesUseCase
import org.easy.wallet.model.TokenHolding

@OptIn(ExperimentalCoroutinesApi::class)
class AssetsViewModel(
  accountRepository: AccountRepositoryImpl,
  val allBalancesUseCase: LoadAllBalancesUseCase
) : ViewModel() {
  val state = accountRepository
    .getCurrentAccount()
    .map { account ->
      println("===== $account")
      if (account != null) {
        val balances = allBalancesUseCase(account)
        AssetsUiState.WalletAssets(
          walletName = account.name,
          assetTokenHoldings = balances
        )
      } else {
        AssetsUiState.EmptyWallet
      }
    }.catch {
      println("===== $it")
      emit(AssetsUiState.EmptyWallet)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3_000), AssetsUiState.Fetching)
}

sealed interface AssetsUiState {
  data object Fetching : AssetsUiState

  data object EmptyWallet : AssetsUiState

  data class WalletAssets(
    val walletName: String,
    val assetTokenHoldings: List<TokenHolding>
  ) : AssetsUiState
}
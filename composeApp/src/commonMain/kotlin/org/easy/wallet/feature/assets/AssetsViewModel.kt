package org.easy.wallet.feature.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import org.easy.wallet.data.repository.WalletRepository
import org.easy.wallet.domain.BalanceUseCase
import org.easy.wallet.model.Balance

@OptIn(ExperimentalCoroutinesApi::class)
class AssetsViewModel(
  private val walletRepository: WalletRepository,
  private val balanceUseCase: BalanceUseCase
) : ViewModel() {
  val state = combine(
    walletRepository.hasActivatedWallet().flatMapLatest {
      walletRepository.walletName()
    },
    balanceUseCase()
  ) { walletName, balances ->
    AssetsUiState.WalletAssets(
      walletName = walletName,
      assets = balances
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3_000), AssetsUiState.Fetching)
}

sealed interface AssetsUiState {
  data object Fetching : AssetsUiState

  data class WalletAssets(
    val walletName: String?,
    val assets: List<Balance>
  ) : AssetsUiState
}
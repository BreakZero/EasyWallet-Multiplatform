package org.easy.wallet.feature.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import org.easy.wallet.data.repository.AccountRepositoryImpl
import org.easy.wallet.datastore.PreferencesRepository
import org.easy.wallet.domain.LoadAssetBalancesUseCase
import org.easy.wallet.model.AssetBalance

@OptIn(ExperimentalCoroutinesApi::class)
class AssetsViewModel(
  accountRepository: AccountRepositoryImpl,
  val loadAssetBalancesUseCase: LoadAssetBalancesUseCase,
  private val preferencesRepository: PreferencesRepository
) : ViewModel() {
  val state: StateFlow<AssetsUiState> =
    combine(
      accountRepository.getCurrentAccount(),
      preferencesRepository.preferences.map { it.debugMode }.distinctUntilChanged()
    ) { account, debugMode ->
      account to debugMode
    }.transformLatest { (account, _) ->
      delay(2_000)
      if (account == null) {
        emit(AssetsUiState.EmptyWallet)
      } else {
        emitAll(
          loadAssetBalancesUseCase(account)
            .map { balances ->
              AssetsUiState.WalletAssets(
                walletName = account.name,
                assetBalances = balances
              )
            }
        )
      }
    }.catch { emit(AssetsUiState.EmptyWallet) }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(3_000),
        initialValue = AssetsUiState.Fetching
      )
}

sealed interface AssetsUiState {
  data object Fetching : AssetsUiState

  data object EmptyWallet : AssetsUiState

  data class WalletAssets(
    val walletName: String,
    val assetBalances: List<AssetBalance>
  ) : AssetsUiState
}

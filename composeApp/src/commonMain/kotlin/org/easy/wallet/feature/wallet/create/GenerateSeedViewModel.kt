package org.easy.wallet.feature.wallet.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trustwallet.core.HDWallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.easy.wallet.data.repository.AccountRepositoryImpl
import org.easy.wallet.datastore.PreferencesRepository
import org.easy.wallet.datastore.model.UserPreferences

class GenerateSeedViewModel internal constructor(
  private val accountRepository: AccountRepositoryImpl,
  private val preferencesRepository: PreferencesRepository
) : ViewModel() {
  val mnemonic = flow {
    emit(HDWallet(128, "").mnemonic)
  }.stateIn(viewModelScope, SharingStarted.Lazily, "")

  fun createWallet(passcode: String) {
    viewModelScope.launch {
      preferencesRepository.set(UserPreferences(passcode = passcode))
      accountRepository.create("Wallet1", mnemonic = mnemonic.value)
    }
  }
}
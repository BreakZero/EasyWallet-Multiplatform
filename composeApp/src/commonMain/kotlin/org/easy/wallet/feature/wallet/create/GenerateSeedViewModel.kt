package org.easy.wallet.feature.wallet.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustwallet.core.HDWallet
import kotlinx.coroutines.flow.SharingStarted
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
  private val _mnemonic = HDWallet(128, "").mnemonic

  val mnemonic = flow {
    emit(_mnemonic)
  }.stateIn(viewModelScope, SharingStarted.Lazily, _mnemonic)

  fun createWallet(passcode: String, onResult: () -> Unit) {
    viewModelScope
      .launch {
        preferencesRepository.set(UserPreferences(passcode = passcode))
        accountRepository.create("Wallet1", mnemonic = _mnemonic)
      }.invokeOnCompletion { onResult() }
  }
}
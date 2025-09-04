package org.easy.wallet.feature.wallet.restore

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.insert
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustwallet.core.Mnemonic
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.easy.wallet.common.ClipboardManager
import org.easy.wallet.data.repository.AccountRepositoryImpl
import org.easy.wallet.datastore.PreferencesRepository
import org.easy.wallet.datastore.model.UserPreferences

class WalletRestoreViewModel internal constructor(
  private val accountRepository: AccountRepositoryImpl,
  private val preferencesRepository: PreferencesRepository
) : ViewModel() {
  val mnemonicTextField = TextFieldState()

  val isMnemonicValid = snapshotFlow {
    val mnemonic = mnemonicTextField.text.toString()
    Mnemonic.isValid(mnemonic)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  fun copyFromClipboard() {
    val clipboardText = ClipboardManager.getClipboardText() ?: return
    mnemonicTextField.edit { insert(0, clipboardText) }
  }

  fun restoreWallet(passcode: String, onResult: () -> Unit) {
    val mnemonic = mnemonicTextField.text.toString()

    viewModelScope
      .launch {
        preferencesRepository.set(UserPreferences(passcode = passcode))
        accountRepository.create("Wallet1", mnemonic = mnemonic)
      }.invokeOnCompletion { onResult() }
  }
}
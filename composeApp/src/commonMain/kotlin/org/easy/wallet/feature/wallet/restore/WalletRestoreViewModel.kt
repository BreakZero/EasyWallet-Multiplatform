package org.easy.wallet.feature.wallet.restore

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.insert
import androidx.lifecycle.ViewModel
import org.easy.wallet.common.ClipboardManager

class WalletRestoreViewModel : ViewModel() {
  val mnemonic = TextFieldState()

  fun copyFromClipboard() {
    val clipboardText = ClipboardManager.getClipboardText() ?: return
    mnemonic.edit { insert(0, clipboardText) }
  }
}
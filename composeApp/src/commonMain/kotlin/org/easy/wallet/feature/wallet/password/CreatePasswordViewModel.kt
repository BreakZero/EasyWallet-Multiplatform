package org.easy.wallet.feature.wallet.password

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.lifecycle.ViewModel

class CreatePasswordViewModel : ViewModel() {
  val password: TextFieldState = TextFieldState()

  fun enterNumber(number: String) {
    password.edit {
      append(number)
    }
  }

  fun delete() {
    val text = password.text
    if (text.isEmpty()) return
    password.edit { delete(text.length - 1, text.length) }
  }
}
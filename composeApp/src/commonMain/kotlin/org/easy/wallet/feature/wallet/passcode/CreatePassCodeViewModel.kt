package org.easy.wallet.feature.wallet.passcode

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CreatePassCodeViewModel : ViewModel() {
  private val _state = MutableStateFlow<PasswordUiState>(PasswordUiState.Settle(TextFieldState()))
  val state = _state.asStateFlow()

  fun enterNumber(number: Char, onSuccess: (String) -> Unit) {
    when (val value = _state.value) {
      is PasswordUiState.Settle -> {
        value.password.edit { append(number) }
        if (!value.error.isNullOrBlank()) {
          _state.update { (it as PasswordUiState.Settle).copy(error = null) }
        }
        val finalText = value.password.text.toString()
        if (finalText.length >= 6) {
          _state.update {
            PasswordUiState.WaitConfirmPassCode(
              finalText,
              TextFieldState()
            )
          }
        }
      }

      is PasswordUiState.WaitConfirmPassCode -> {
        value.confirmPassword.edit { append(number) }
        val finalText = value.confirmPassword.text.toString()
        if (finalText.length >= 6) {
          if (finalText != value.origin) {
            _state.update { PasswordUiState.Settle(TextFieldState(), error = "Pass code not match") }
          } else {
            onSuccess(finalText)
          }
        }
      }
    }
  }

  fun delete() {
    when (val value = _state.value) {
      is PasswordUiState.Settle -> {
        val text = value.password.text
        if (text.isEmpty()) return
        value.password.edit { delete(text.length - 1, text.length) }
      }

      is PasswordUiState.WaitConfirmPassCode -> {
        val text = value.confirmPassword.text
        if (text.isEmpty()) return
        value.confirmPassword.edit { delete(text.length - 1, text.length) }
      }
    }
  }
}

sealed interface PasswordUiState {
  data class Settle(
    val password: TextFieldState,
    val error: String? = null
  ) : PasswordUiState

  data class WaitConfirmPassCode(
    val origin: String,
    val confirmPassword: TextFieldState
  ) : PasswordUiState
}
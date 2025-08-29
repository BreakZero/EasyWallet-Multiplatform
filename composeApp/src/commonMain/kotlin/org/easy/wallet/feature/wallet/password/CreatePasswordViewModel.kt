package org.easy.wallet.feature.wallet.password

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CreatePasswordViewModel : ViewModel() {
  private val _state = MutableStateFlow<PasswordUiState>(PasswordUiState.SetUp(TextFieldState()))
  val state = _state.asStateFlow()

  fun enterNumber(number: Char) {
    when (val value = _state.value) {
      is PasswordUiState.SetUp -> {
        value.password.edit { append(number) }
        if (!value.error.isNullOrBlank()) {
          _state.update { (it as PasswordUiState.SetUp).copy(error = null) }
        }
        val finalText = value.password.text.toString()
        if (finalText.length >= 6) {
          _state.update {
            PasswordUiState.WaitConfirm(
              finalText,
              TextFieldState()
            )
          }
        }
      }

      is PasswordUiState.WaitConfirm -> {
        value.confirmPassword.edit { append(number) }
        val finalText = value.confirmPassword.text.toString()
        if (finalText.length >= 6) {
          if (finalText != value.origin) {
            println("Password not match: ${value.origin}, $finalText")
            _state.update { PasswordUiState.SetUp(TextFieldState(), error = "Not Match") }
          } else {
            println("Password match: $finalText")
          }
        }
      }
    }
  }

  fun delete() {
    when (val value = _state.value) {
      is PasswordUiState.SetUp -> {
        val text = value.password.text
        if (text.isEmpty()) return
        value.password.edit { delete(text.length - 1, text.length) }
      }

      is PasswordUiState.WaitConfirm -> {
        val text = value.confirmPassword.text
        if (text.isEmpty()) return
        value.confirmPassword.edit { delete(text.length - 1, text.length) }
      }
    }
  }
}

sealed interface PasswordUiState {
  data class SetUp(
    val password: TextFieldState,
    val error: String? = null
  ) : PasswordUiState

  data class WaitConfirm(
    val origin: String,
    val confirmPassword: TextFieldState
  ) : PasswordUiState
}
package org.easy.wallet.feature.wallet.passcode

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import easywallet.composeapp.generated.resources.Res
import easywallet.composeapp.generated.resources.password_confirm
import easywallet.composeapp.generated.resources.password_enter
import org.easy.wallet.components.EasyTopAppBar
import org.easy.wallet.components.NumberGridView
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreatePassCodeScreen(popBackStack: () -> Unit, toNext: (String) -> Unit) {
  val viewModel: CreatePassCodeViewModel = koinViewModel()
  val state by viewModel.state.collectAsStateWithLifecycle()
  Scaffold(
    contentWindowInsets = WindowInsets(0),
    bottomBar = {
      NumberGridView(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        onDeleteClicked = viewModel::delete,
        onNumberClicked = { viewModel.enterNumber(it, onSuccess = toNext) }
      )
    },
    topBar = {
      EasyTopAppBar(onBack = popBackStack)
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(it)
        .padding(bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      val (tipsRes, len) = when (state) {
        is PasswordUiState.Settle -> {
          (Res.string.password_enter to (state as PasswordUiState.Settle).password.text.length)
        }

        is PasswordUiState.WaitConfirmPassCode -> {
          (Res.string.password_confirm to (state as PasswordUiState.WaitConfirmPassCode).confirmPassword.text.length)
        }
      }
      Text(text = stringResource(tipsRes))
      Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
      ) {
        repeat(6) { index ->
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(24.dp)
              .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
          ) {
            if (index < len) {
              Box(
                modifier = Modifier
                  .size(12.dp)
                  .background(MaterialTheme.colorScheme.primary, CircleShape)
              )
            }
          }
        }
      }
      if (state is PasswordUiState.Settle) {
        (state as PasswordUiState.Settle).error?.let { errorMsg ->
          Text(text = errorMsg, color = MaterialTheme.colorScheme.error)
        }
      }
    }
  }
}
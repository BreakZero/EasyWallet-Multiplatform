package org.easy.wallet.feature.wallet.password

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun CreatePasswordScreen(
  popBackStack: () -> Unit
) {
  val viewModel: CreatePasswordViewModel = koinViewModel()
  val state by viewModel.state.collectAsStateWithLifecycle()
  Scaffold(
    contentWindowInsets = WindowInsets(0),
    bottomBar = {
      NumberGridView(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        onDeleteClicked = viewModel::delete,
        onNumberClicked = { viewModel.enterNumber(it) }
      )
    },
    topBar = {
      EasyTopAppBar(onBack = popBackStack)
    }
  ) {
    Column(
      modifier = Modifier.fillMaxSize()
        .padding(it)
        .padding(bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      val (tipsRes, len) = when (state) {
        is PasswordUiState.SetUp -> {
          (Res.string.password_enter to (state as PasswordUiState.SetUp).password.text.length)
        }

        is PasswordUiState.WaitConfirm -> {
          (Res.string.password_confirm to (state as PasswordUiState.WaitConfirm).confirmPassword.text.length)
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
              .border(1.dp, Color.Gray, CircleShape)
          ) {
            if (index < len) {
              Box(
                modifier = Modifier
                  .size(12.dp)
                  .background(Color.Black, CircleShape)
              )
            }
          }
        }
      }
    }
  }
}
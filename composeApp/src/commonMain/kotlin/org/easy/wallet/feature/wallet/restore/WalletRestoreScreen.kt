package org.easy.wallet.feature.wallet.restore

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import easywallet.composeapp.generated.resources.Res
import easywallet.composeapp.generated.resources.action_parse
import easywallet.composeapp.generated.resources.button_confirm
import easywallet.composeapp.generated.resources.general_hint
import easywallet.composeapp.generated.resources.restore_wallet
import org.easy.wallet.components.EasyTopAppBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletRestoreScreen(
  passcode: String,
  onComplete: () -> Unit,
  popBackStack: () -> Unit
) {
  val viewModel: WalletRestoreViewModel = koinViewModel()
  val isValid by viewModel.isMnemonicValid.collectAsStateWithLifecycle()

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      EasyTopAppBar(
        title = { Text(stringResource(Res.string.restore_wallet)) },
        onBack = popBackStack,
        backIcon = Icons.Default.Close
      )
    },
    bottomBar = {
      Button(
        modifier = Modifier
          .fillMaxWidth()
          .navigationBarsPadding()
          .imePadding()
          .padding(bottom = 16.dp)
          .height(56.dp)
          .padding(horizontal = 16.dp),
        onClick = {
          viewModel.restoreWallet(passcode, onResult = onComplete)
        },
        enabled = isValid
      ) {
        Text(text = stringResource(Res.string.button_confirm))
      }
    }
  ) { innerPaddings ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPaddings)
        .padding(top = 24.dp)
    ) {
      BasicTextField(
        state = viewModel.mnemonicTextField,
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 100.dp)
          .padding(horizontal = 16.dp),
        decorator = { innerTextField ->
          TextFieldDefaults.DecorationBox(
            value = viewModel.mnemonicTextField.text.toString(),
            innerTextField = innerTextField,
            enabled = true,
            singleLine = false,
            placeholder = { Text(stringResource(Res.string.general_hint)) },
            interactionSource = remember { MutableInteractionSource() },
            visualTransformation = VisualTransformation.None
          )
        }
      )

      OutlinedButton(
        onClick = viewModel::copyFromClipboard,
        modifier = Modifier.wrapContentSize().align(Alignment.End).padding(end = 16.dp)
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(imageVector = Icons.Default.Call, contentDescription = null)
          Text(stringResource(Res.string.action_parse))
        }
      }
    }
  }
}
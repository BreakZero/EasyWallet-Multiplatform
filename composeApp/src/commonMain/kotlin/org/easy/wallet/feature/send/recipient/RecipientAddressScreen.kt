package org.easy.wallet.feature.send.recipient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import easywallet.composeapp.generated.resources.Res
import easywallet.composeapp.generated.resources.button_continue
import easywallet.composeapp.generated.resources.hint_enter_recipient
import easywallet.composeapp.generated.resources.label_recipient
import easywallet.composeapp.generated.resources.send_error_address_empty
import easywallet.composeapp.generated.resources.send_error_address_invalid
import easywallet.composeapp.generated.resources.send_error_address_same
import easywallet.composeapp.generated.resources.title_enter_recipient
import org.easy.wallet.components.EasyTopAppBar
import org.easy.wallet.feature.send.AddressError
import org.easy.wallet.feature.send.SendFlowAction
import org.easy.wallet.feature.send.SendFlowState
import org.jetbrains.compose.resources.stringResource

@Composable
fun RecipientTypingScreen(state: SendFlowState, onAction: (SendFlowAction) -> Unit) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      EasyTopAppBar(
        onBack = { onAction(SendFlowAction.GoBack) },
        title = {
          Text(
            text = stringResource(Res.string.title_enter_recipient),
            style = MaterialTheme.typography.titleLarge
          )
        }
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(
        text = stringResource(Res.string.label_recipient),
        style = MaterialTheme.typography.bodyLarge
      )

      OutlinedTextField(
        value = state.recipientAddress,
        onValueChange = { onAction(SendFlowAction.OnRecipientChange(it)) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = stringResource(Res.string.hint_enter_recipient)) },
        singleLine = true,
        isError = state.addressError != null,
        supportingText = state.addressError?.let { error ->
          {
            Text(
              text = stringResource(
                when (error) {
                  AddressError.EMPTY -> Res.string.send_error_address_empty
                  AddressError.INVALID_FORMAT -> Res.string.send_error_address_invalid
                  AddressError.SAME_AS_SENDER -> Res.string.send_error_address_same
                }
              ),
              color = MaterialTheme.colorScheme.error
            )
          }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
      )

      Spacer(modifier = Modifier.weight(1f))

      Button(
        onClick = { onAction(SendFlowAction.ContinueToAmount) },
        modifier = Modifier.fillMaxWidth().height(52.dp),
        enabled = state.recipientAddress.isNotBlank() && state.addressError == null
      ) {
        Text(text = stringResource(Res.string.button_continue))
      }
    }
  }
}
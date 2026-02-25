package org.easy.wallet.feature.send.amount

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import easywallet.composeapp.generated.resources.Res
import easywallet.composeapp.generated.resources.send_amount_available
import easywallet.composeapp.generated.resources.send_amount_label
import easywallet.composeapp.generated.resources.send_amount_memo_hint
import easywallet.composeapp.generated.resources.send_amount_memo_label
import easywallet.composeapp.generated.resources.send_amount_review
import easywallet.composeapp.generated.resources.send_amount_title
import easywallet.composeapp.generated.resources.send_amount_to
import easywallet.composeapp.generated.resources.send_amount_use_max
import easywallet.composeapp.generated.resources.send_error_amount_exceeds
import easywallet.composeapp.generated.resources.send_error_amount_invalid
import easywallet.composeapp.generated.resources.send_error_amount_zero
import org.easy.wallet.components.EasyTopAppBar
import org.easy.wallet.feature.send.AmountError
import org.easy.wallet.feature.send.SendFlowAction
import org.easy.wallet.feature.send.SendFlowState
import org.jetbrains.compose.resources.stringResource

@Composable
fun EnterAmountScreen(state: SendFlowState, onAction: (SendFlowAction) -> Unit) {
  val tokenHolding = state.tokenHolding ?: return
  val symbol = tokenHolding.asset.symbol

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      EasyTopAppBar(
        onBack = { onAction(SendFlowAction.GoBack) },
        title = {
          Text(
            text = stringResource(Res.string.send_amount_title),
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
      if (state.recipientAddress.isNotBlank()) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
          )
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = stringResource(Res.string.send_amount_to),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = state.recipientAddress,
              style = MaterialTheme.typography.bodySmall,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = stringResource(Res.string.send_amount_available),
          style = MaterialTheme.typography.bodyMedium
        )
        Text(
          text = "${tokenHolding.amount.format()} $symbol",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.primary
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
      ) {
        OutlinedTextField(
          value = state.amount,
          onValueChange = { onAction(SendFlowAction.OnSendAmountChange(it)) },
          modifier = Modifier.weight(1f),
          label = { Text(stringResource(Res.string.send_amount_label)) },
          suffix = { Text(symbol) },
          isError = state.amountError != null,
          supportingText = state.amountError?.let { error ->
            {
              Text(
                text = stringResource(
                  when (error) {
                    AmountError.EMPTY, AmountError.INVALID_FORMAT ->
                      Res.string.send_error_amount_invalid
                    AmountError.ZERO -> Res.string.send_error_amount_zero
                    AmountError.EXCEEDS_BALANCE -> Res.string.send_error_amount_exceeds
                  }
                ),
                color = MaterialTheme.colorScheme.error
              )
            }
          },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true
        )
        OutlinedButton(
          onClick = { onAction(SendFlowAction.UseMaxAmount) },
          modifier = Modifier.padding(top = 8.dp)
        ) {
          Text(text = stringResource(Res.string.send_amount_use_max))
        }
      }

      OutlinedTextField(
        value = state.memo,
        onValueChange = { onAction(SendFlowAction.OnMemoChange(it)) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(Res.string.send_amount_memo_label)) },
        placeholder = { Text(stringResource(Res.string.send_amount_memo_hint)) },
        singleLine = true
      )

      Spacer(modifier = Modifier.weight(1f))

      Button(
        onClick = { onAction(SendFlowAction.ReviewTransaction) },
        modifier = Modifier.fillMaxWidth().height(52.dp),
        enabled = state.isAmountValid && !state.isEstimatingFee
      ) {
        if (state.isEstimatingFee) {
          CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onPrimary,
            strokeWidth = 2.dp
          )
        } else {
          Text(
            text = stringResource(Res.string.send_amount_review),
            style = MaterialTheme.typography.bodyLarge
          )
        }
      }
    }
  }
}
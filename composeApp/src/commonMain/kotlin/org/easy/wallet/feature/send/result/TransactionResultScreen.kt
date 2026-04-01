package org.easy.wallet.feature.send.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import easywallet.composeapp.generated.resources.Res
import easywallet.composeapp.generated.resources.send_result_done
import easywallet.composeapp.generated.resources.send_result_failure_subtitle
import easywallet.composeapp.generated.resources.send_result_failure_title
import easywallet.composeapp.generated.resources.send_result_success_subtitle
import easywallet.composeapp.generated.resources.send_result_success_title
import easywallet.composeapp.generated.resources.send_result_try_again
import easywallet.composeapp.generated.resources.send_result_tx_hash
import org.easy.wallet.feature.send.SendFlowAction
import org.easy.wallet.feature.send.SendFlowState
import org.easy.wallet.feature.send.SendResult
import org.jetbrains.compose.resources.stringResource

@Composable
fun TransactionResultScreen(state: SendFlowState, onAction: (SendFlowAction) -> Unit) {
  val result = state.sendResult ?: return

  Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      when (result) {
        is SendResult.Success -> SuccessContent(
          txHash = result.txHash,
          state = state
        )
        is SendResult.Failure -> FailureContent(message = result.message)
      }

      Spacer(modifier = Modifier.height(32.dp))

      Button(
        onClick = {
          when (result) {
            is SendResult.Success -> onAction(SendFlowAction.DismissResult)
            is SendResult.Failure -> onAction(SendFlowAction.GoBack)
          }
        },
        modifier = Modifier.fillMaxWidth().height(52.dp)
      ) {
        Text(
          text = stringResource(
            when (result) {
              is SendResult.Success -> Res.string.send_result_done
              is SendResult.Failure -> Res.string.send_result_try_again
            }
          ),
          style = MaterialTheme.typography.bodyLarge
        )
      }
    }
  }
}

@Composable
private fun SuccessContent(txHash: String, state: SendFlowState) {
  val symbol = state.assetBalance
    ?.asset
    ?.symbol
    .orEmpty()

  Icon(
    imageVector = Icons.Default.CheckCircle,
    contentDescription = null,
    modifier = Modifier.size(72.dp),
    tint = MaterialTheme.colorScheme.primary
  )
  Spacer(modifier = Modifier.height(16.dp))
  Text(
    text = stringResource(Res.string.send_result_success_title),
    style = MaterialTheme.typography.headlineSmall,
    fontWeight = FontWeight.Bold
  )
  Spacer(modifier = Modifier.height(8.dp))
  Text(
    text = stringResource(Res.string.send_result_success_subtitle, state.amount, symbol),
    style = MaterialTheme.typography.bodyLarge,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign = TextAlign.Center
  )
  Spacer(modifier = Modifier.height(24.dp))
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
        text = stringResource(Res.string.send_result_tx_hash),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = txHash,
        style = MaterialTheme.typography.bodySmall,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
private fun FailureContent(message: String) {
  Icon(
    imageVector = Icons.Default.Warning,
    contentDescription = null,
    modifier = Modifier.size(72.dp),
    tint = MaterialTheme.colorScheme.error
  )
  Spacer(modifier = Modifier.height(16.dp))
  Text(
    text = stringResource(Res.string.send_result_failure_title),
    style = MaterialTheme.typography.headlineSmall,
    fontWeight = FontWeight.Bold,
    color = MaterialTheme.colorScheme.error
  )
  Spacer(modifier = Modifier.height(8.dp))
  Text(
    text = stringResource(Res.string.send_result_failure_subtitle),
    style = MaterialTheme.typography.bodyLarge,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign = TextAlign.Center
  )
  Spacer(modifier = Modifier.height(8.dp))
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.errorContainer
    )
  ) {
    Text(
      text = message,
      modifier = Modifier.padding(16.dp),
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onErrorContainer
    )
  }
}

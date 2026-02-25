package org.easy.wallet.feature.send.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import easywallet.composeapp.generated.resources.Res
import easywallet.composeapp.generated.resources.button_confirm
import easywallet.composeapp.generated.resources.send_review_title
import org.easy.wallet.components.EasyTopAppBar
import org.easy.wallet.feature.send.SendFlowAction
import org.easy.wallet.feature.send.SendFlowState
import org.easy.wallet.model.AmountFormatter
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReviewTransactionScreen(state: SendFlowState, onAction: (SendFlowAction) -> Unit) {
  val tokenHolding = state.tokenHolding ?: return
  val symbol = tokenHolding.asset.symbol

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      EasyTopAppBar(
        onBack = { onAction(SendFlowAction.GoBack) },
        title = {
          Text(
            text = stringResource(Res.string.send_review_title),
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
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          ReviewRow(
            label = "From",
            value = tokenHolding.address?.value.orEmpty(),
            isTruncated = true
          )
          HorizontalDivider()
          ReviewRow(
            label = "To",
            value = state.recipientAddress,
            isTruncated = true
          )
          HorizontalDivider()
          ReviewRow(
            label = "Amount",
            value = "${state.amount} $symbol"
          )

          val feeAmount = state.feePolicy?.feeAmount
          if (feeAmount != null) {
            HorizontalDivider()
            ReviewRow(
              label = "Network Fee",
              value = "${AmountFormatter.formatBaseUnits(
                feeAmount,
                tokenHolding.asset.decimals
              )} $symbol"
            )
          }

          if (state.memo.isNotBlank()) {
            HorizontalDivider()
            ReviewRow(label = "Memo", value = state.memo)
          }
        }
      }

      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer
        )
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Total",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
          Text(
            text = "${state.amount} $symbol",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
        }
      }

      Spacer(modifier = Modifier.weight(1f))

      Button(
        onClick = { onAction(SendFlowAction.ConfirmSend) },
        modifier = Modifier.fillMaxWidth().height(52.dp),
        enabled = !state.isSending
      ) {
        if (state.isSending) {
          CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onPrimary,
            strokeWidth = 2.dp
          )
        } else {
          Text(
            text = stringResource(Res.string.button_confirm),
            style = MaterialTheme.typography.bodyLarge
          )
        }
      }
    }
  }
}

@Composable
private fun ReviewRow(
  label: String,
  value: String,
  isTruncated: Boolean = false
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.Medium,
      maxLines = 1,
      overflow = if (isTruncated) TextOverflow.Ellipsis else TextOverflow.Clip,
      modifier = if (isTruncated) Modifier.fillMaxWidth(0.6f) else Modifier
    )
  }
}
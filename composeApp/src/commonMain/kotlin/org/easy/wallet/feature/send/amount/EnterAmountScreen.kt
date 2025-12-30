package org.easy.wallet.feature.send.amount

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import org.easy.wallet.components.EasyTopAppBar
import org.easy.wallet.feature.send.SendFlowAction
import org.easy.wallet.feature.send.SendFlowState

@Composable
fun EnterAmountScreen(state: SendFlowState, onAction: (SendFlowAction) -> Unit) {
  val tokenHolding = state.tokenHolding ?: return

  val isEnableNext by remember(state.amount) {
    derivedStateOf {
      runCatching {
        BigDecimal.parseString(state.amount.orEmpty()) > BigDecimal.ZERO
      }.fold(
        onSuccess = { true },
        onFailure = { false }
      )
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      EasyTopAppBar(
        onBack = { onAction(SendFlowAction.Popup) },
        title = {
          Text("Send Amount", style = MaterialTheme.typography.titleLarge)
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
      // Recipient info card
      if (!state.recipientAddress.isNullOrBlank()) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
          )
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "To:",
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

      // Available balance
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Available Balance:",
          style = MaterialTheme.typography.bodyMedium
        )
        Text(
          text = "${tokenHolding.amount.format()} ${tokenHolding.asset.symbol}",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.primary
        )
      }

      // Amount input
      OutlinedTextField(
        value = state.amount.orEmpty(),
        onValueChange = { onAction(SendFlowAction.OnSendAmountChange(it)) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Amount") },
        suffix = { Text(tokenHolding.asset.symbol) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true
      )

      Button(
        onClick = {
          onAction(SendFlowAction.OverviewTransaction)
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = isEnableNext
      ) {
        Text("Review Transaction")
      }
    }
  }
}
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import org.easy.wallet.components.EasyTopAppBar

@Composable
fun EnterAmountScreen(
  recipientAddress: String = "",
  tokenSymbol: String = "ETH",
  availableBalance: String = "0.00",
  onAmountConfirmed: (BigDecimal) -> Unit = {},
  onBack: () -> Unit = {}
) {
  var amount by remember { mutableStateOf("") }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      EasyTopAppBar(
        onBack = onBack,
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
      if (recipientAddress.isNotEmpty()) {
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
              text = recipientAddress,
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
          text = "$availableBalance $tokenSymbol",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.primary
        )
      }

      // Amount input
      OutlinedTextField(
        value = amount,
        onValueChange = { amount = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Amount") },
        suffix = { Text(tokenSymbol) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true
      )

      Button(
        onClick = {
          try {
            val amountBigDecimal = BigDecimal.parseString(amount)
            onAmountConfirmed(amountBigDecimal)
          } catch (e: Exception) {
            // Handle invalid amount
          }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = amount.isNotBlank() &&
          try {
            BigDecimal.parseString(amount)
            true
          } catch (e: Exception) {
            false
          }
      ) {
        Text("Review Transaction")
      }
    }
  }
}
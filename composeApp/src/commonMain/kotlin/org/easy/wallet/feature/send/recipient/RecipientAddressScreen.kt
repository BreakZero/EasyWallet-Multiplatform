package org.easy.wallet.feature.send.recipient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.easy.wallet.components.EasyTopAppBar

@Composable
fun RecipientTypingScreen(onAddressEntered: (String) -> Unit = {}, onBack: () -> Unit = {}) {
  var address by remember { mutableStateOf("") }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      EasyTopAppBar(
        onBack = onBack,
        title = {
          Text("Enter Recipient Address", style = MaterialTheme.typography.titleLarge)
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
        text = "Recipient Address",
        style = MaterialTheme.typography.bodyLarge
      )

      OutlinedTextField(
        value = address,
        onValueChange = { address = it },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Enter wallet address or scan QR code") },
        singleLine = true
      )

      Button(
        onClick = { onAddressEntered(address) },
        modifier = Modifier.fillMaxWidth(),
        enabled = address.isNotBlank()
      ) {
        Text("Continue")
      }
    }
  }
}
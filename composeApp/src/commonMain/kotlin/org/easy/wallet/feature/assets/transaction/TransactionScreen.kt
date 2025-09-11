package org.easy.wallet.feature.assets.transaction

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TransactionScreen() {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      Row(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = {}) {
          Text(text = "Send")
        }
        Button(onClick = {}) {
          Text(text = "Receive")
        }
      }
    }
  ) { }
}
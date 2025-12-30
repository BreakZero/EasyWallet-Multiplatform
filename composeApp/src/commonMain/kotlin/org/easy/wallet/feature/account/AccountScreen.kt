package org.easy.wallet.feature.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import easywallet.composeapp.generated.resources.Res
import easywallet.composeapp.generated.resources.create_wallet
import easywallet.composeapp.generated.resources.text_account
import easywallet.composeapp.generated.resources.text_wallet
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AccountScreen(navigateToWallet: () -> Unit) {
  val viewModel: AccountViewModel = koinViewModel()
  val uiState by viewModel.state.collectAsStateWithLifecycle()
  var showDebugConfirmDialog by remember { mutableStateOf(false) }
  var pendingDebugValue by remember { mutableStateOf(false) }

  AccountTabScreen(
    uiState = uiState,
    onWalletClick = {
      viewModel.listAccounts()
    },
    onDebugModeToggle = { newValue ->
      pendingDebugValue = newValue
      showDebugConfirmDialog = true
    }
  )

  if (showDebugConfirmDialog) {
    DebugModeConfirmDialog(
      enabled = pendingDebugValue,
      onConfirm = {
        viewModel.toggleDebugMode(pendingDebugValue)
        showDebugConfirmDialog = false
      },
      onDismiss = {
        showDebugConfirmDialog = false
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountTabScreen(
  uiState: AccountUiState,
  onWalletClick: () -> Unit,
  onDebugModeToggle: (Boolean) -> Unit
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Text(stringResource(Res.string.text_account), style = MaterialTheme.typography.titleLarge)
        }
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier.fillMaxSize().padding(paddingValues)
    ) {
      when (uiState) {
        is AccountUiState.Info -> {
          ListItem(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onWalletClick),
            headlineContent = { Text(stringResource(Res.string.text_wallet)) },
            trailingContent = { Text(uiState.walletName.orEmpty()) }
          )

          HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

          // Developer Settings区域
          SettingSection(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            title = "Developer Settings"
          ) {
            ListItem(
              modifier = Modifier.fillMaxWidth(),
              headlineContent = { Text("Debug Mode") },
              supportingContent = {
                Text(
                  "Switch to testnet for development and testing",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              },
              trailingContent = {
                Switch(
                  checked = uiState.debugMode,
                  onCheckedChange = onDebugModeToggle
                )
              }
            )

            // Debug模式激活警告
            if (uiState.debugMode) {
              Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.errorContainer
                )
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                  )
                  Spacer(Modifier.width(8.dp))
                  Text(
                    "Testnet Active - Assets have no real value",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                  )
                }
              }
            }
          }
        }

        AccountUiState.NoSetup -> {
          ListItem(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onWalletClick),
            headlineContent = { Text(stringResource(Res.string.create_wallet)) }
          )
        }
      }
    }
  }
}

@Composable
private fun SettingSection(
  modifier: Modifier = Modifier,
  title: String,
  content: @Composable ColumnScope.() -> Unit
) {
  Column(modifier = modifier) {
    Text(
      title,
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.padding(bottom = 8.dp)
    )
    content()
  }
}

@Composable
private fun DebugModeConfirmDialog(
  enabled: Boolean,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Icon(
        imageVector = Icons.Default.Warning,
        contentDescription = null,
        tint = if (enabled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
      )
    },
    title = {
      Text(if (enabled) "Enable Debug Mode?" else "Disable Debug Mode?")
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          if (enabled) {
            "Enabling debug mode will switch all blockchain networks to testnet. " +
              "All assets and transactions will use test networks."
          } else {
            "Disabling debug mode will switch back to mainnet."
          }
        )

        if (enabled) {
          Spacer(Modifier.height(8.dp))
          Text(
            "Affected networks:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
          )
          Text(
            "• Ethereum → Sepolia\n" +
              "• Polygon → Amoy\n" +
              "• Arbitrum → Sepolia\n" +
              "• Bitcoin → Testnet\n" +
              "• Solana → Testnet",
            style = MaterialTheme.typography.bodySmall
          )
          Spacer(Modifier.height(8.dp))
          Text(
            "Note: Asset balances will be reloaded immediately.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
          )
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onConfirm) {
        Text("Confirm")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
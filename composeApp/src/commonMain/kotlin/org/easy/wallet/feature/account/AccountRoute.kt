package org.easy.wallet.feature.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import easywallet.composeapp.generated.resources.Res
import easywallet.composeapp.generated.resources.tab_account
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AccountScreen(navigateToWallet: () -> Unit) {
  val viewModel: AccountViewModel = koinViewModel()
  val uiState by viewModel.state.collectAsStateWithLifecycle()

  AccountTabScreen(uiState = uiState, onEvent = {
    viewModel.listAccounts()
  })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountTabScreen(uiState: AccountUiState, onEvent: () -> Unit) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Text(stringResource(Res.string.tab_account), style = MaterialTheme.typography.titleLarge)
        }
      )
    }
  ) {
    Column(
      modifier = Modifier.fillMaxSize().padding(it)
    ) {
      when (uiState) {
        is AccountUiState.Info -> {
          ListItem(
            modifier = Modifier
              .fillMaxWidth().clickable(onClick = onEvent),
            headlineContent = {
              Text("Wallet")
            },
            trailingContent = {
              Text(uiState.walletName.orEmpty())
            }
          )
        }

        AccountUiState.NoSetup -> {
          ListItem(
            modifier = Modifier
              .fillMaxWidth()
              .clickable(onClick = onEvent),
            headlineContent = {
              if (uiState is AccountUiState.NoSetup) {
                Text("Create Wallet")
              }
            }
          )
        }
      }
    }
  }
}
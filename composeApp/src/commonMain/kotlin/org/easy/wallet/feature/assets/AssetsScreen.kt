package org.easy.wallet.feature.assets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.easy.wallet.components.AssetsItemView
import org.easy.wallet.components.OverlayLoadingWheel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AssetsScreen() {
  val viewModel: AssetsViewModel = koinViewModel()
  val state by viewModel.state.collectAsStateWithLifecycle()
  AssetsScreen(state)
}

@Composable
private fun AssetsScreen(state: AssetsUiState) {
  Scaffold(modifier = Modifier.fillMaxSize()) {
    when (state) {
      AssetsUiState.Fetching -> {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          OverlayLoadingWheel(contentDesc = "")
        }
      }

      is AssetsUiState.WalletAssets -> {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(
            top = it.calculateTopPadding(),
            bottom = it.calculateBottomPadding() + 100.dp,
            start = 16.dp,
            end = 16.dp
          ),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          if (state.walletName.isNullOrBlank()) {
            item {
              Box(
                modifier = Modifier.fillMaxWidth().heightIn(100.dp),
                contentAlignment = Alignment.Center
              ) {
                Button(modifier = Modifier.fillMaxWidth(), onClick = {}) {
                  Text("Create Wallet")
                }
              }
            }
          } else {
            item {
              Box(
                modifier = Modifier.fillMaxWidth().heightIn(100.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(text = state.walletName, style = MaterialTheme.typography.titleLarge)
              }
            }
          }
          items(state.assets, key = { it.id }) { assets ->
            AssetsItemView(
              assets = assets,
              modifier = Modifier.fillMaxWidth(),
              onItemClick = {}
            )
          }
        }
      }
    }
  }
}
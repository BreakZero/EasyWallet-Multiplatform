package org.easy.wallet.feature.assets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import easywallet.composeapp.generated.resources.Res
import easywallet.composeapp.generated.resources.create_wallet
import easywallet.composeapp.generated.resources.home_tips
import easywallet.composeapp.generated.resources.home_tips_desc
import easywallet.composeapp.generated.resources.restore_wallet
import org.easy.wallet.components.AssetsItemView
import org.easy.wallet.model.TokenMeta
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AssetsScreen(
  onCreateWallet: () -> Unit,
  onRestoreWallet: () -> Unit,
  onAssetClick: (TokenMeta) -> Unit
) {
  val viewModel: AssetsViewModel = koinViewModel()
  val state by viewModel.state.collectAsStateWithLifecycle()
  AssetsScreen(
    state,
    onEvent = {
      when (it) {
        AssetEvent.OnCreateWallet -> onCreateWallet()
        is AssetEvent.OnItemClick -> onAssetClick(it.asset)
        AssetEvent.OnRestoreWallet -> onRestoreWallet()
      }
    }
  )
}

@Composable
private fun AssetsScreen(state: AssetsUiState, onEvent: (AssetEvent) -> Unit = {}) {
  Scaffold(modifier = Modifier.fillMaxSize()) { it ->
    when (state) {
      AssetsUiState.Fetching -> {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          LoadingIndicator()
        }
      }

      AssetsUiState.EmptyWallet -> {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
        ) {
          Spacer(Modifier.weight(1f))

          Text(stringResource(Res.string.home_tips), style = MaterialTheme.typography.headlineSmall)
          Text(
            stringResource(Res.string.home_tips_desc),
            style = MaterialTheme.typography.bodyLarge
          )
          Spacer(Modifier.height(12.dp))
          Button(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            onClick = { onEvent(AssetEvent.OnCreateWallet) }
          ) {
            Text(stringResource(Res.string.create_wallet))
          }
          Spacer(Modifier.height(12.dp))
          OutlinedButton(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            onClick = { onEvent(AssetEvent.OnRestoreWallet) }
          ) {
            Text(stringResource(Res.string.restore_wallet))
          }
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
          item {
            Box(
              modifier = Modifier.fillMaxWidth().heightIn(100.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(text = state.walletName, style = MaterialTheme.typography.titleLarge)
            }
          }
          items(state.assetTokenHoldings, key = { it.asset.id.value }) { holding ->
            AssetsItemView(
              tokenHolding = holding,
              modifier = Modifier.fillMaxWidth(),
              onItemClick = { onEvent(AssetEvent.OnItemClick(holding.asset)) }
            )
          }
        }
      }
    }
  }
}
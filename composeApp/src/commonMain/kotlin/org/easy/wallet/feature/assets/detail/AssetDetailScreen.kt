package org.easy.wallet.feature.assets.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import easywallet.composeapp.generated.resources.Res
import easywallet.composeapp.generated.resources.action_copy
import easywallet.composeapp.generated.resources.action_receive
import easywallet.composeapp.generated.resources.action_send
import easywallet.composeapp.generated.resources.action_to_explorer
import easywallet.composeapp.generated.resources.label_to_explorer
import easywallet.composeapp.generated.resources.text_recipient
import easywallet.composeapp.generated.resources.text_transfer_history
import org.easy.wallet.components.EasyTopAppBar
import org.easy.wallet.model.TokenId
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AssetDetailScreen(tokenId: TokenId, popup: () -> Unit) {
  val viewModel: AssetDetailViewModel = koinViewModel { parametersOf(tokenId) }
  val state by viewModel.state.collectAsStateWithLifecycle()

  AssetDetailScreen(
    state = state,
    onEvent = { event ->
      when (event) {
        AssetDetailEvent.Popup -> popup()
        AssetDetailEvent.OnReceive -> Unit
        AssetDetailEvent.OnSend -> Unit
      }
    }
  )
}

@Composable
private fun AssetDetailScreen(state: AssetDetailUiState, onEvent: (AssetDetailEvent) -> Unit) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      EasyTopAppBar(
        onBack = { onEvent(AssetDetailEvent.Popup) }
      )
    }
  ) {
    Column(
      modifier = Modifier.fillMaxSize().padding(it),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(
        text = state.tokenHolding?.amount?.format() ?: "0.00",
        style = MaterialTheme.typography.displayMedium,
        modifier = Modifier.padding(horizontal = 16.dp)
      )
      ActionRow(
        onSend = { onEvent(AssetDetailEvent.OnSend) },
        onReceive = { onEvent(AssetDetailEvent.OnReceive) }
      )
      HorizontalDivider(thickness = Dp.Hairline)
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          stringResource(Res.string.text_recipient),
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
          Text(
            text = state.tokenHolding
              ?.address
              ?.value
              .orEmpty(),
            modifier = Modifier.weight(1f).alpha(0.66f),
            style = MaterialTheme.typography.bodySmall
          )
          Box(
            modifier = Modifier
              .wrapContentSize(align = Alignment.Center)
              .clip(RoundedCornerShape(50))
              .background(MaterialTheme.colorScheme.primary)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = stringResource(Res.string.action_copy),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onPrimary
            )
          }
        }
      }
      HorizontalDivider(thickness = Dp.Hairline)

      Text(
        text = stringResource(Res.string.text_transfer_history),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp)
      )

      Column(
        modifier = Modifier.fillMaxWidth().weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.CenterVertically)
      ) {
        Text(text = stringResource(Res.string.label_to_explorer))
        OutlinedButton(onClick = {}) {
          Text(text = stringResource(Res.string.action_to_explorer))
        }
      }
    }
  }
}

@Composable
private fun ActionRow(
  modifier: Modifier = Modifier,
  onSend: () -> Unit,
  onReceive: () -> Unit
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceAround
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      IconButton(
        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.tertiary),
        onClick = onSend
      ) {
        Icon(
          imageVector = Icons.Default.ArrowUpward,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onTertiary
        )
      }
      Text(stringResource(Res.string.action_send))
    }

    Column(
      verticalArrangement = Arrangement.spacedBy(4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      IconButton(
        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.tertiary),
        onClick = onReceive
      ) {
        Icon(
          imageVector = Icons.Default.ArrowDownward,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onTertiary
        )
      }
      Text(stringResource(Res.string.action_receive))
    }
  }
}

@Preview
@Composable
private fun TransactionScreenPreview() {
  AssetDetailScreen(state = AssetDetailUiState(), onEvent = {})
}
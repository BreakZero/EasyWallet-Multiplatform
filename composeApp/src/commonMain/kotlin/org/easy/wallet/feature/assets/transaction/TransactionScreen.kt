package org.easy.wallet.feature.assets.transaction

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.easy.wallet.components.EasyTopAppBar
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TransactionScreen() {
  TransactionScreen("")
}

@Composable
private fun TransactionScreen(balance: String) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      EasyTopAppBar { }
    }
  ) {
    Column(
      modifier = Modifier.fillMaxSize().padding(it),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(text = balance, style = MaterialTheme.typography.displayMedium)
      ActionRow(
        onSend = {},
        onReceive = {}
      )
      HorizontalDivider(thickness = Dp.Hairline)
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text("收款地址", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
          Text(
            text = "123456789\n134546576u",
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
              text = "复制",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onPrimary
            )
          }
        }
      }
      HorizontalDivider(thickness = Dp.Hairline)

      Text(
        text = "交易历史",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp)
      )

      Column(
        modifier = Modifier.fillMaxWidth().weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.CenterVertically)
      ) {
        Text("更多交易历史，可前往浏览器查看")
        OutlinedButton(onClick = {}) {
          Text(text = "前往浏览器")
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
      Text("Send")
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
      Text("Receive")
    }
  }
}

@Preview
@Composable
private fun TransactionScreenPreview() {
  TransactionScreen("12.88")
}
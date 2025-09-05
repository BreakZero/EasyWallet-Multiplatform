package org.easy.wallet.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.easy.wallet.model.Balance

@Composable
fun AssetsItemView(
  asset: Balance,
  modifier: Modifier = Modifier,
  onItemClick: () -> Unit
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    onClick = onItemClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(
          horizontal = 16.dp,
          vertical = 12.dp
        ),
      verticalAlignment = Alignment.CenterVertically
    ) {
      DynamicAsyncImage(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape),
        imageUrl = asset.logoUrl,
        contentDescription = asset.coinName
      )
      Text(
        modifier = Modifier.padding(start = 12.dp),
        text = asset.coinName,
        style = MaterialTheme.typography.titleMedium
      )
      Spacer(modifier = Modifier.weight(1.0f))
      Text(
        text = "${asset.displayBalance()} ${asset.symbol}",
        style = MaterialTheme.typography.titleLarge
      )
    }
  }
}
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
import org.easy.wallet.model.TokenHolding

@Composable
fun AssetsItemView(
  tokenHolding: TokenHolding,
  modifier: Modifier = Modifier,
  onItemClick: () -> Unit
) {
  val assetMeta = tokenHolding.asset
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
        imageUrl = assetMeta.logoUrl,
        contentDescription = assetMeta.name
      )
      Text(
        modifier = Modifier.padding(start = 12.dp),
        text = assetMeta.name,
        style = MaterialTheme.typography.titleMedium
      )
      Spacer(modifier = Modifier.weight(1.0f))
      Text(
        text = "${tokenHolding.amount.format()} ${assetMeta.symbol}",
        style = MaterialTheme.typography.titleLarge
      )
    }
  }
}
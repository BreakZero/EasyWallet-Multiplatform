package org.easy.wallet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.integer.BigInteger
import org.easy.wallet.model.Address
import org.easy.wallet.model.Amount
import org.easy.wallet.model.ChainId
import org.easy.wallet.model.FungibleTokenMeta
import org.easy.wallet.model.TokenHolding
import org.easy.wallet.model.TokenId
import org.easy.wallet.model.TokenStandard

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
    shape = RoundedCornerShape(12.dp),
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
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.background)
      ) {
        DynamicAsyncImage(
          modifier = Modifier
            .fillMaxSize(1f)
            .padding(4.dp)
            .clip(CircleShape),
          imageUrl = assetMeta.logoUrl,
          contentDescription = assetMeta.name
        )
      }
      Column(
        modifier = Modifier.padding(start = 12.dp)
      ) {
        Text(
          text = assetMeta.name,
          style = MaterialTheme.typography.titleMedium
        )
        Text(
          text = "$29,450.00",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.secondary
        )
      }
      Spacer(modifier = Modifier.weight(1.0f))
      Column {
        Text(
          text = "${tokenHolding.amount.format()} ${assetMeta.symbol}",
          style = MaterialTheme.typography.titleLarge
        )
        Text(
          text = "$29,450.00",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.secondary
        )
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun AssetItemPreview() {
  AssetsItemView(
    tokenHolding = TokenHolding(
      asset = FungibleTokenMeta(
        id = TokenId(
          value = "evm:1"
        ),
        chainId = ChainId("1"),
        name = "Ethereum",
        symbol = "ETH",
        decimals = 18,
        logoUrl = null,
        standard = TokenStandard.NATIVE,
        contract = Address("0x0000000000000000000000000000000000000000")
      ),
      amount = Amount(raw = BigInteger.TEN, decimals = 18),
      address = Address("")
    ),
    onItemClick = {}
  )
}
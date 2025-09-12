package org.easy.wallet.data.mapper

import org.easy.wallet.database.Tokens
import org.easy.wallet.model.ChainId
import org.easy.wallet.model.Token
import org.easy.wallet.model.TokenId
import org.easy.wallet.model.TokenStandard

internal fun Tokens.toExternal(): Token = Token(
  tokenId = TokenId(token_id),
  chainId = ChainId(chain_id),
  standard = TokenStandard.valueOf(standard),
  contract = contract,
  symbol = symbol,
  name = name,
  decimals = decimals.toInt(),
  iconUrl = icon_url,
  enabled = enabled == 1L,
  sortOrder = sort_order.toInt(),
  createdAt = created_at,
  updatedAt = updated_at
)
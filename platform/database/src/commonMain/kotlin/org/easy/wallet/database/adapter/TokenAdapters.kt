package org.easy.wallet.database.adapter

import app.cash.sqldelight.ColumnAdapter
import org.easy.wallet.model.TokenStandard

val TokenStandardAdapter = object : ColumnAdapter<TokenStandard, String> {
  override fun decode(databaseValue: String): TokenStandard = TokenStandard.valueOf(databaseValue)

  override fun encode(value: TokenStandard): String = value.name
}
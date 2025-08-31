package org.easy.wallet.data.repository

import org.easy.wallet.data.mapper.toExternal
import org.easy.wallet.database.DatabaseDriverFactory
import org.easy.wallet.database.EasyWalletDatabase
import org.easy.wallet.model.Token
import org.easy.wallet.model.TokenStandard

class TokenRepositoryImpl internal constructor(
  driverFactory: DatabaseDriverFactory
) : TokenRepository {
  private val database = EasyWalletDatabase(driverFactory.createDriver())
  private val tokenQueries = database.tokensQueries

  override suspend fun upsert(token: Token) {
    tokenQueries.upsertToken(
      token_id = token.tokenId,
      chain_id = token.chainId,
      standard = token.standard.name,
      contract = token.contract,
      symbol = token.symbol,
      name = token.name,
      decimals = token.decimals.toLong(),
      icon_url = token.iconUrl,
      enabled = if (token.enabled) 1 else 0,
      sort_order = token.sortOrder.toLong(),
      created_at = token.createdAt,
      updated_at = token.updatedAt,
    )
  }

  override suspend fun upsertAll(tokens: List<Token>) {
    database.transaction {
      tokens.forEach { token ->
        tokenQueries.upsertToken(
          token_id = token.tokenId,
          chain_id = token.chainId,
          standard = token.standard.name,
          contract = token.contract,
          symbol = token.symbol,
          name = token.name,
          decimals = token.decimals.toLong(),
          icon_url = token.iconUrl,
          enabled = if (token.enabled) 1 else 0,
          sort_order = token.sortOrder.toLong(),
          created_at = token.createdAt,
          updated_at = token.updatedAt,
        )
      }
    }
  }

  override suspend fun getById(tokenId: String): Token? =
    tokenQueries.selectTokenById(tokenId).executeAsOneOrNull()?.toExternal()

  override suspend fun getByChain(
    chainId: String,
    onlyEnabled: Boolean
  ): List<Token> {
    return if (onlyEnabled)
      tokenQueries.selectEnabledByChain(chainId).executeAsList().map { it.toExternal() }
    else
      tokenQueries.selectByChain(chainId).executeAsList().map { it.toExternal() }
  }

  override suspend fun getByContract(
    chainId: String,
    standard: TokenStandard,
    contract: String
  ): Token? {
    return tokenQueries.selectByContract(chainId, standard.name, contract.lowercase())
      .executeAsOneOrNull()?.toExternal()
  }

  override suspend fun search(
    chainId: String,
    keyword: String,
    limit: Int,
    offset: Int
  ): List<Token> {
    TODO("Not yet implemented")
  }

  override suspend fun setEnabled(tokenId: String, enabled: Boolean) {
    TODO("Not yet implemented")
  }

  override suspend fun setSortOrder(tokenId: String, sortOrder: Int) {
    TODO("Not yet implemented")
  }

  override suspend fun delete(tokenId: String) {
    TODO("Not yet implemented")
  }
}
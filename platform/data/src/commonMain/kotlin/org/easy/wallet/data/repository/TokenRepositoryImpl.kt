package org.easy.wallet.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.easy.wallet.data.mapper.toExternal
import org.easy.wallet.database.DatabaseDriverFactory
import org.easy.wallet.database.EasyWalletDatabase
import org.easy.wallet.model.Token
import org.easy.wallet.model.TokenStandard
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class TokenRepositoryImpl internal constructor(
  driverFactory: DatabaseDriverFactory
) : TokenRepository {
  private val database = EasyWalletDatabase(driverFactory.createDriver())
  private val tokenQueries = database.tokensQueries

  override suspend fun allTokens(): List<Token> = tokenQueries.selectAllTokens().executeAsList().map { it.toExternal() }

  override fun streamTokens(): Flow<List<Token>> = tokenQueries
    .selectAllTokens()
    .asFlow()
    .mapToList(Dispatchers.IO)
    .map { it.map { it.toExternal() } }

  override suspend fun upsert(token: Token) {
    tokenQueries.upsertToken(
      token_id = token.tokenId.value,
      chain_id = token.chainId.value,
      standard = token.standard.name,
      contract = token.contract,
      symbol = token.symbol,
      name = token.name,
      decimals = token.decimals.toLong(),
      icon_url = token.iconUrl,
      enabled = if (token.enabled) 1 else 0,
      sort_order = token.sortOrder.toLong(),
      created_at = token.createdAt,
      updated_at = token.updatedAt
    )
  }

  override suspend fun upsertAll(tokens: List<Token>) {
    database.transaction {
      tokens.forEach { token ->
        tokenQueries.upsertToken(
          token_id = token.tokenId.value,
          chain_id = token.chainId.value,
          standard = token.standard.name,
          contract = token.contract,
          symbol = token.symbol,
          name = token.name,
          decimals = token.decimals.toLong(),
          icon_url = token.iconUrl,
          enabled = if (token.enabled) 1 else 0,
          sort_order = token.sortOrder.toLong(),
          created_at = token.createdAt,
          updated_at = token.updatedAt
        )
      }
    }
  }

  override suspend fun getById(tokenId: String): Token? = tokenQueries.selectTokenById(tokenId).executeAsOneOrNull()?.toExternal()

  override suspend fun getByChain(chainId: String, onlyEnabled: Boolean): List<Token> = if (onlyEnabled) {
    tokenQueries.selectEnabledByChain(chainId).executeAsList().map { it.toExternal() }
  } else {
    tokenQueries.selectByChain(chainId).executeAsList().map { it.toExternal() }
  }

  override suspend fun getByContract(
    chainId: String,
    standard: TokenStandard,
    contract: String
  ): Token? = tokenQueries
    .selectByContract(chainId, standard.name, contract.lowercase())
    .executeAsOneOrNull()
    ?.toExternal()

  override suspend fun setEnabled(tokenId: String, enabled: Boolean) {
    tokenQueries.updateTokenEnabled(
      if (enabled) 1 else 0,
      Clock.System.now().toEpochMilliseconds(),
      tokenId
    )
  }

  override suspend fun setSortOrder(tokenId: String, sortOrder: Int) {
    tokenQueries.updateTokenSort(
      sortOrder.toLong(),
      Clock.System.now().toEpochMilliseconds(),
      tokenId
    )
  }

  override suspend fun delete(tokenId: String) {
    tokenQueries.deleteToken(token_id = tokenId)
  }
}
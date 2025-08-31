package org.easy.wallet.data.repository

import org.easy.wallet.model.Token
import org.easy.wallet.model.TokenStandard

interface TokenRepository {
  suspend fun upsert(token: Token)
  suspend fun upsertAll(tokens: List<Token>)
  suspend fun getById(tokenId: String): Token?
  suspend fun getByChain(chainId: String, onlyEnabled: Boolean = true): List<Token>
  suspend fun getByContract(chainId: String, standard: TokenStandard, contract: String): Token?
  suspend fun setEnabled(tokenId: String, enabled: Boolean)
  suspend fun setSortOrder(tokenId: String, sortOrder: Int)
  suspend fun delete(tokenId: String)
}
package org.easy.wallet.data.chain

import org.easy.wallet.data.interfaces.IChainAdapter
import org.easy.wallet.data.repository.TokenRepository
import org.easy.wallet.model.ChainId
import org.easy.wallet.model.TokenId
import org.easy.wallet.model.TokenStandard

/**
 * Central registry for all chain adapters.
 * Provides lookup and discovery of adapters by chain ID, token ID, or token standard.
 *
 * This abstraction allows for dynamic adapter registration and easy extension
 * to new chains without modifying existing code.
 */
class AdapterRegistry(
  private val adapters: Map<String, IChainAdapter>,
  private val tokenRepository: TokenRepository,
  private val chainRouter: ChainRouter
) {
  /**
   * Get adapter by ChainId.
   * Uses ChainRouter to automatically route to the correct network (mainnet/testnet).
   * @throws IllegalArgumentException if no adapter found for the chain
   */
  fun getAdapter(chainId: ChainId): IChainAdapter {
    val routedChainId = chainRouter.route(chainId)
    return adapters[routedChainId.value]
      ?: throw IllegalArgumentException("No adapter registered for chain: ${routedChainId.value}")
  }

  /**
   * Get adapter by TokenId.
   * Looks up the token's chain and returns the appropriate adapter.
   */
  suspend fun getAdapterForToken(tokenId: TokenId): IChainAdapter {
    val chainId = getChainIdForToken(tokenId)
    return getAdapter(chainId)
  }

  /**
   * Get the ChainId for a given TokenId.
   */
  suspend fun getChainIdForToken(tokenId: TokenId): ChainId {
    val token = tokenRepository.getById(tokenId.value)
      ?: error("Token not found: ${tokenId.value}")
    return token.chainId
  }

  /**
   * Get all registered adapters.
   */
  fun getAllAdapters(): List<IChainAdapter> = adapters.values.toList()

  /**
   * Get adapters that support a specific token standard.
   */
  fun getAdaptersByStandard(standard: TokenStandard): List<IChainAdapter> = adapters.values.filter { adapter ->
    standard in adapter.supportedStandards
  }

  /**
   * Get all supported chain IDs.
   */
  fun getSupportedChains(): Set<ChainId> = adapters.keys.map { ChainId(it) }.toSet()

  /**
   * Check if a chain is supported.
   */
  fun isChainSupported(chainId: ChainId): Boolean = chainId.value in adapters

  /**
   * Get adapter by ChainId, returning null if not found.
   */
  fun getAdapterOrNull(chainId: ChainId): IChainAdapter? = adapters[chainId.value]
}
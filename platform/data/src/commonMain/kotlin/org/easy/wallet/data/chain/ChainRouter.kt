package org.easy.wallet.data.chain

import kotlinx.coroutines.flow.StateFlow
import org.easy.wallet.model.ChainId

/**
 * Chain Router: Dynamically routes logical ChainIds to physical ChainIds based on debug mode.
 *
 * Core idea: Token database ChainIds remain unchanged (mainnet),
 * but at request time, they are transparently routed to testnet adapters when debug mode is enabled.
 *
 * This allows switching between mainnet and testnet without modifying the database.
 */
class ChainRouter(
  private val isDebugMode: StateFlow<Boolean>
) {
  /**
   * Routes a logical ChainId to the actual ChainId that should be used.
   * - Debug mode: mainnet ChainId → testnet ChainId
   * - Normal mode: ChainId remains unchanged
   */
  fun route(logicalChainId: ChainId): ChainId {
    if (!isDebugMode.value) return logicalChainId

    return ChainId.getTestnetVariant(logicalChainId) ?: run {
      // If already testnet or no testnet variant exists, keep unchanged
      logicalChainId
    }
  }

  /**
   * Reverse routing: Gets the logical ChainId from a physical ChainId.
   * Useful for reverse lookups.
   */
  fun reverseRoute(physicalChainId: ChainId): ChainId {
    if (!isDebugMode.value) return physicalChainId

    return ChainId.getMainnetVariant(physicalChainId)
  }
}
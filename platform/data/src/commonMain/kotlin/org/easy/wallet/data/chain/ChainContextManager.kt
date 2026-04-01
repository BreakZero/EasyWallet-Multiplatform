package org.easy.wallet.data.chain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.easy.wallet.model.ChainId

/**
 * Manages the current chain context based on user operations.
 * Automatically detects which coin/chain the user is operating on and provides
 * the appropriate chain adapter for operations.
 *
 * This is the central component that routes all chain-specific operations.
 */
class ChainContextManager(
  private val adapterRegistry: AdapterRegistry
) {
  private val _currentChainContext = MutableStateFlow<ChainContext?>(null)
  val currentChainContext: StateFlow<ChainContext?> = _currentChainContext.asStateFlow()

  /**
   * Set the current chain context directly by ChainId.
   * Useful for operations not tied to a specific token.
   */
  fun setContextByChainId(chainId: ChainId) {
    val adapter = adapterRegistry.getAdapter(chainId)
    _currentChainContext.value = ChainContext(
      chainId = chainId,
      adapter = adapter
    )
  }

  /**
   * Get the current chain context or throw if none is set.
   * Use this when you expect a context to be set.
   */
  fun requireCurrentContext(): ChainContext = _currentChainContext.value
    ?: error("No chain context set. Call setContextByChainId() first.")

  /**
   * Get the current chain context or return null.
   */
  fun getCurrentContext(): ChainContext? = _currentChainContext.value

  /**
   * Clear the current context.
   * Typically called when user navigates away from chain-specific screens.
   */
  fun clearContext() {
    _currentChainContext.value = null
  }

  /**
   * Check if a context is currently set.
   */
  fun hasContext(): Boolean = _currentChainContext.value != null
}

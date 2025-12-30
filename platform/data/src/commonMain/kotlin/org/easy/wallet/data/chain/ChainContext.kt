package org.easy.wallet.data.chain

import org.easy.wallet.data.interfaces.IChainAdapter
import org.easy.wallet.model.ChainId
import org.easy.wallet.model.TokenId

/**
 * Represents the current operational context for a specific chain.
 * Contains the chain adapter and metadata about what the user is currently operating on.
 */
data class ChainContext(
    val tokenId: TokenId?,
    val chainId: ChainId,
    val adapter: IChainAdapter
)

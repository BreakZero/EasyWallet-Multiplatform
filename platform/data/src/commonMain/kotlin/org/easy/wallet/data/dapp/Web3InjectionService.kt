package org.easy.wallet.data.dapp

import com.trustwallet.core.CoinType
import org.easy.wallet.data.chain.ChainContextManager
import org.easy.wallet.model.Address
import org.easy.wallet.model.ChainId

/**
 * Service for managing Web3 provider injection into dApps.
 * Handles wallet connection, account management, and transaction signing
 * for web-based decentralized applications.
 *
 * This service adapts the wallet's multi-chain support to provide
 * chain-specific Web3 providers (e.g., window.ethereum for EVM chains).
 */
class Web3InjectionService(
    private val chainContextManager: ChainContextManager
) {
    /**
     * Get the Web3 provider for the current chain context.
     * This provider can be injected into a WebView or dApp browser.
     */
    fun getProviderForCurrentChain(): Web3Provider? {
        val context = chainContextManager.getCurrentContext() ?: return null
        return createProvider(context.chainId)
    }

    /**
     * Get a Web3 provider for a specific chain.
     */
    fun getProviderForChain(chainId: ChainId): Web3Provider {
        return createProvider(chainId)
    }

    /**
     * Connect wallet to a dApp for the current chain.
     */
    suspend fun connect(dappUrl: String, accounts: List<Address>): ConnectionResult {
        val provider = getProviderForCurrentChain()
            ?: return ConnectionResult.Error("No chain context set")

        return try {
            provider.connect(dappUrl, accounts)
            ConnectionResult.Success(accounts)
        } catch (e: Exception) {
            ConnectionResult.Error(e.message ?: "Connection failed")
        }
    }

    /**
     * Disconnect wallet from a dApp.
     */
    suspend fun disconnect(dappUrl: String) {
        val provider = getProviderForCurrentChain() ?: return
        provider.disconnect(dappUrl)
    }

    /**
     * Handle a transaction request from a dApp.
     * This is called when a dApp requests the user to sign a transaction.
     */
    suspend fun handleTransactionRequest(
        request: TransactionRequest,
        coinType: CoinType
    ): TransactionRequestResult {
        val context = chainContextManager.requireCurrentContext()
        val provider = getProviderForCurrentChain()
            ?: return TransactionRequestResult.Error("No provider available")

        return try {
            // The actual signing would happen through the chain adapter
            val signature = provider.signTransaction(request, coinType)
            TransactionRequestResult.Success(signature)
        } catch (e: Exception) {
            TransactionRequestResult.Error(e.message ?: "Signing failed")
        }
    }

    /**
     * Handle a message signing request from a dApp.
     */
    suspend fun handleSignMessageRequest(
        message: String,
        account: Address,
        coinType: CoinType
    ): SignMessageResult {
        val provider = getProviderForCurrentChain()
            ?: return SignMessageResult.Error("No provider available")

        return try {
            val signature = provider.signMessage(message, account, coinType)
            SignMessageResult.Success(signature)
        } catch (e: Exception) {
            SignMessageResult.Error(e.message ?: "Signing failed")
        }
    }

    /**
     * Switch the active chain in the dApp connection.
     */
    suspend fun switchChain(chainId: ChainId): SwitchChainResult {
        return try {
            chainContextManager.setContextByChainId(chainId)
            SwitchChainResult.Success(chainId)
        } catch (e: Exception) {
            SwitchChainResult.Error(e.message ?: "Chain switch failed")
        }
    }

    private fun createProvider(chainId: ChainId): Web3Provider {
        return when {
            chainId.value.startsWith("evm:") -> EvmWeb3Provider(chainId)
            chainId.value.startsWith("btc:") -> BitcoinWeb3Provider(chainId)
            chainId.value.startsWith("solana:") -> SolanaWeb3Provider(chainId)
            else -> throw IllegalArgumentException("Unsupported chain: ${chainId.value}")
        }
    }
}

/**
 * Abstract Web3 provider interface.
 * Chain-specific implementations handle the details of provider injection.
 */
interface Web3Provider {
    val chainId: ChainId

    suspend fun connect(dappUrl: String, accounts: List<Address>)
    suspend fun disconnect(dappUrl: String)
    suspend fun signTransaction(request: TransactionRequest, coinType: CoinType): String
    suspend fun signMessage(message: String, account: Address, coinType: CoinType): String
}

/**
 * EVM-specific Web3 provider (implements window.ethereum interface).
 */
class EvmWeb3Provider(override val chainId: ChainId) : Web3Provider {
    override suspend fun connect(dappUrl: String, accounts: List<Address>) {
        // TODO: Implement EIP-1193 provider injection
        // This would inject window.ethereum into a WebView
    }

    override suspend fun disconnect(dappUrl: String) {
        // TODO: Implement disconnect logic
    }

    override suspend fun signTransaction(request: TransactionRequest, coinType: CoinType): String {
        // TODO: Implement EIP-1559 transaction signing
        throw NotImplementedError("EVM transaction signing not yet implemented")
    }

    override suspend fun signMessage(message: String, account: Address, coinType: CoinType): String {
        // TODO: Implement EIP-191 personal_sign
        throw NotImplementedError("EVM message signing not yet implemented")
    }
}

/**
 * Bitcoin Web3 provider.
 */
class BitcoinWeb3Provider(override val chainId: ChainId) : Web3Provider {
    override suspend fun connect(dappUrl: String, accounts: List<Address>) {
        // TODO: Implement Bitcoin wallet provider (e.g., for ordinals, lightning)
    }

    override suspend fun disconnect(dappUrl: String) {
        // TODO: Implement disconnect logic
    }

    override suspend fun signTransaction(request: TransactionRequest, coinType: CoinType): String {
        // TODO: Implement Bitcoin PSBT signing
        throw NotImplementedError("Bitcoin transaction signing not yet implemented")
    }

    override suspend fun signMessage(message: String, account: Address, coinType: CoinType): String {
        // TODO: Implement Bitcoin message signing
        throw NotImplementedError("Bitcoin message signing not yet implemented")
    }
}

/**
 * Solana Web3 provider (implements window.solana interface).
 */
class SolanaWeb3Provider(override val chainId: ChainId) : Web3Provider {
    override suspend fun connect(dappUrl: String, accounts: List<Address>) {
        // TODO: Implement Solana wallet adapter interface
    }

    override suspend fun disconnect(dappUrl: String) {
        // TODO: Implement disconnect logic
    }

    override suspend fun signTransaction(request: TransactionRequest, coinType: CoinType): String {
        // TODO: Implement Solana transaction signing
        throw NotImplementedError("Solana transaction signing not yet implemented")
    }

    override suspend fun signMessage(message: String, account: Address, coinType: CoinType): String {
        // TODO: Implement Solana message signing (signMessage method)
        throw NotImplementedError("Solana message signing not yet implemented")
    }
}

/**
 * Represents a transaction request from a dApp.
 */
data class TransactionRequest(
    val from: Address,
    val to: Address,
    val value: String?,
    val data: String?,
    val gasLimit: String?,
    val gasPrice: String?,
    val nonce: String?
)

/**
 * Result types for Web3 operations.
 */
sealed class ConnectionResult {
    data class Success(val accounts: List<Address>) : ConnectionResult()
    data class Error(val message: String) : ConnectionResult()
}

sealed class TransactionRequestResult {
    data class Success(val signature: String) : TransactionRequestResult()
    data class Error(val message: String) : TransactionRequestResult()
}

sealed class SignMessageResult {
    data class Success(val signature: String) : SignMessageResult()
    data class Error(val message: String) : SignMessageResult()
}

sealed class SwitchChainResult {
    data class Success(val chainId: ChainId) : SwitchChainResult()
    data class Error(val message: String) : SwitchChainResult()
}

package org.easy.wallet.domain.usecase

import org.easy.wallet.data.chain.ChainContextManager
import org.easy.wallet.data.dapp.ConnectionResult
import org.easy.wallet.data.dapp.Web3InjectionService
import org.easy.wallet.model.Address
import org.easy.wallet.model.ChainId

/**
 * Use case for connecting wallet to a dApp.
 * Handles setting the chain context and establishing the Web3 connection.
 */
class ConnectDAppUseCase(
    private val web3InjectionService: Web3InjectionService,
    private val chainContextManager: ChainContextManager
) {
    /**
     * Connect to a dApp with the specified chain and accounts.
     * @param dappUrl The URL of the dApp
     * @param chainId The blockchain to connect with
     * @param accounts List of accounts to expose to the dApp
     * @return ConnectionResult indicating success or error
     */
    suspend operator fun invoke(
        dappUrl: String,
        chainId: ChainId,
        accounts: List<Address>
    ): ConnectionResult {
        // Set the chain context for the connection
        chainContextManager.setContextByChainId(chainId)

        // Connect to the dApp
        return web3InjectionService.connect(dappUrl, accounts)
    }
}

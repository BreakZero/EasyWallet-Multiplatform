package org.easy.wallet.network

import kotlinx.coroutines.flow.StateFlow

/**
 * Network configuration provider that dynamically returns endpoint configurations
 * based on debug mode (mainnet vs testnet).
 */
interface NetworkConfigProvider {
  val isDebugMode: StateFlow<Boolean>

  fun getChainAssetGatewayBaseUrl(): String

  fun getEtherScanHost(): String

  fun getBlockChairHost(): String

  fun getSolanaRpcUrl(): String

  fun getBitcoinRpcUrl(): String
}

class NetworkConfigProviderImpl(
  override val isDebugMode: StateFlow<Boolean>
) : NetworkConfigProvider {
  override fun getChainAssetGatewayBaseUrl(): String = BuildKonfig.CHAIN_ASSET_GATEWAY_BASE_URL

  override fun getEtherScanHost(): String = if (isDebugMode.value) {
    "api-sepolia.etherscan.io"
  } else {
    "api.etherscan.io"
  }

  override fun getBlockChairHost(): String {
    // BlockChair uses chain parameter to distinguish testnet
    return "api.blockchair.com"
  }

  override fun getSolanaRpcUrl(): String = if (isDebugMode.value) {
    "api.testnet.solana.com"
  } else {
    "api.mainnet-beta.solana.com"
  }

  override fun getBitcoinRpcUrl(): String = if (isDebugMode.value) {
    "testnet.blockchain.info"
  } else {
    "blockchain.info"
  }
}
package org.easy.wallet.domain

import com.trustwallet.core.CoinType
import com.trustwallet.core.HDWallet
import org.easy.wallet.model.Address
import org.easy.wallet.model.ChainId

internal fun HDWallet.address(chainId: ChainId): Address = when (chainId) {
  ChainId.EVM_MAINNET, ChainId.Polygon_MAINNET, ChainId.Arbitrum_MAINNET,
  ChainId.EVM_SEPOLIA, ChainId.Polygon_AMOY, ChainId.Arbitrum_SEPOLIA -> Address(
    getAddressForCoin(CoinType.Ethereum)
  )
  ChainId.BTC_MAINNET, ChainId.BTC_TESTNET -> Address(getAddressForCoin(CoinType.Bitcoin))
  else -> throw IllegalArgumentException("Unsupported chain: ${chainId.value}")
}

fun coinTypeForChain(chainId: ChainId): CoinType = when (chainId) {
  ChainId.EVM_MAINNET, ChainId.Polygon_MAINNET, ChainId.Arbitrum_MAINNET,
  ChainId.EVM_SEPOLIA, ChainId.Polygon_AMOY, ChainId.Arbitrum_SEPOLIA -> CoinType.Ethereum
  ChainId.BTC_MAINNET, ChainId.BTC_TESTNET -> CoinType.Bitcoin
  else -> throw IllegalArgumentException("Unsupported chain: ${chainId.value}")
}
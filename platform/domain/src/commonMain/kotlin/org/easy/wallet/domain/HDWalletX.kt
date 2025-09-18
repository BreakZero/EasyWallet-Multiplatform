package org.easy.wallet.domain

import com.trustwallet.core.CoinType
import com.trustwallet.core.HDWallet
import org.easy.wallet.model.Address
import org.easy.wallet.model.ChainId

internal fun HDWallet.address(chainId: ChainId): Address = when (chainId) {
  ChainId.EVM_MAINNET, ChainId.Polygon_MAINNET, ChainId.Arbitrum_MAINNET -> Address(
    getAddressForCoin(CoinType.Ethereum)
  )
  ChainId.BTC_MAINNET -> Address(getAddressForCoin(CoinType.Bitcoin))
  else -> throw IllegalArgumentException("Unsupported chain")
}
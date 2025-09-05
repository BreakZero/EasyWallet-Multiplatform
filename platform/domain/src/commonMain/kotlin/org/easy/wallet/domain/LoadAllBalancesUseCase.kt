package org.easy.wallet.domain

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.trustwallet.core.HDWallet
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.easy.wallet.data.interfaces.BalanceService
import org.easy.wallet.data.repository.TokenRepository
import org.easy.wallet.model.Address
import org.easy.wallet.model.Balance
import org.easy.wallet.model.ChainId
import org.easy.wallet.model.Token
import org.easy.wallet.model.TokenStandard
import org.easy.wallet.model.WalletAccount

class LoadAllBalancesUseCase internal constructor(
  private val tokenRepository: TokenRepository,
  private val balanceServices: Map<String, BalanceService>
) {
  suspend operator fun invoke(walletAccount: WalletAccount): List<Balance> {
    val hdWallet = HDWallet(walletAccount.mnemonic, "")

    val allToken = tokenRepository.allTokens()
    val balanceJob = coroutineScope {
      allToken.take(6).map { token ->
        val address = hdWallet.address(token)
        val balanceService = balanceServices[token.chainId.value]
        async {
          val balance =
            balanceService?.getBalance(
              account = Address("0xde0b295669a9fd93d5f28d9ec85e40f4cb697bae"),
              contract = token.contract
            ) ?: BigInteger.ZERO
          Balance(
            id = token.tokenId,
            coinName = token.name,
            symbol = token.symbol,
            decimals = token.decimals,
            contractAddress = token.contract,
            logoUrl = token.iconUrl,
            balance = balance
          )
        }
      }
    }
    return balanceJob.awaitAll()
  }
}

private fun HDWallet.address(token: Token): Address = when (token.standard) {
  TokenStandard.NATIVE -> {
    when (token.chainId) {
      ChainId.EVM_MAINNET, ChainId.Polygon_MAINNET, ChainId.Arbitrum_MAINNET -> Address(
        getAddressForCoin(com.trustwallet.core.CoinType.Ethereum)
      )

      ChainId.BTC_MAINNET -> Address(getAddressForCoin(com.trustwallet.core.CoinType.Bitcoin))
      else -> throw IllegalArgumentException("Unsupported chain")
    }
  }

  TokenStandard.ERC20, TokenStandard.ERC721 -> Address(getAddressForCoin(com.trustwallet.core.CoinType.Ethereum))
  else -> throw IllegalArgumentException("Unsupported chain")
}
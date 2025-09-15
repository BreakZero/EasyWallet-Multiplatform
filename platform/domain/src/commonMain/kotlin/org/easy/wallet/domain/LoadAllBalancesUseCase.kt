package org.easy.wallet.domain

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.trustwallet.core.HDWallet
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.easy.wallet.data.interfaces.BalanceService
import org.easy.wallet.data.repository.TokenRepository
import org.easy.wallet.model.Address
import org.easy.wallet.model.Amount
import org.easy.wallet.model.ChainId
import org.easy.wallet.model.FungibleTokenMeta
import org.easy.wallet.model.NativeTokenMeta
import org.easy.wallet.model.Token
import org.easy.wallet.model.TokenHolding
import org.easy.wallet.model.TokenStandard
import org.easy.wallet.model.WalletAccount

class LoadAllBalancesUseCase internal constructor(
  private val tokenRepository: TokenRepository,
  private val balanceServices: Map<String, BalanceService>
) {
  suspend operator fun invoke(walletAccount: WalletAccount): List<TokenHolding> {
    val hdWallet = HDWallet(walletAccount.mnemonic, "")

    val allToken = tokenRepository.allTokens()
    val balanceJob = coroutineScope {
      allToken.take(5).map { token ->
        val address = hdWallet.address(token)
        val balanceService = balanceServices[token.chainId.value]
        async {
          val balance = balanceService?.getBalance(
            account = address,
            contract = token.contract
          ) ?: BigInteger.ZERO

          val metaData = token.contract?.let {
            FungibleTokenMeta(
              id = token.tokenId,
              name = token.name,
              symbol = token.symbol,
              decimals = token.decimals,
              contract = Address(it),
              logoUrl = token.iconUrl
            )
          } ?: NativeTokenMeta(
            id = token.tokenId,
            name = token.name,
            symbol = token.symbol,
            decimals = token.decimals,
            logoUrl = token.iconUrl
          )

          TokenHolding(
            asset = metaData,
            amount = Amount(raw = balance, decimals = metaData.decimals)
          )
        }
      }
    }
    return balanceJob.awaitAll()
  }
}

fun HDWallet.address(token: Token): Address = when (token.standard) {
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
package org.easy.wallet.domain

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.trustwallet.core.HDWallet
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.easy.wallet.data.interfaces.BalanceService
import org.easy.wallet.data.repository.TokenRepository
import org.easy.wallet.model.Address
import org.easy.wallet.model.Amount
import org.easy.wallet.model.FungibleTokenMeta
import org.easy.wallet.model.NativeTokenMeta
import org.easy.wallet.model.TokenHolding
import org.easy.wallet.model.WalletAccount
import org.easy.wallet.model.zero

class LoadAllBalancesUseCase internal constructor(
  private val tokenRepository: TokenRepository,
  private val balanceServices: Map<String, BalanceService>
) {
  operator fun invoke(walletAccount: WalletAccount): Flow<List<TokenHolding>> = flow {
    val hdWallet = HDWallet(walletAccount.mnemonic, "")

    val allToken = tokenRepository.allTokens()

    val assetMetas = allToken.map { token ->
      val meta = token.contract?.let {
        FungibleTokenMeta(
          id = token.tokenId,
          chainId = token.chainId,
          standard = token.standard,
          name = token.name,
          symbol = token.symbol,
          decimals = token.decimals,
          contract = Address(it),
          logoUrl = token.iconUrl
        )
      } ?: NativeTokenMeta(
        id = token.tokenId,
        chainId = token.chainId,
        standard = token.standard,
        name = token.name,
        symbol = token.symbol,
        decimals = token.decimals,
        logoUrl = token.iconUrl
      )
      meta
    }

    emit(value = assetMetas.map { it.zero() })

    val balanceJob = coroutineScope {
      assetMetas.take(5).map { meta ->
        val address = hdWallet.address(meta.chainId)
        val balanceService = balanceServices[meta.chainId.value]
        async {
          val contractAddress = when (meta) {
            is FungibleTokenMeta -> meta.contract.value
            else -> null
          }
          val balance = balanceService?.getBalance(
            account = address,
            contract = contractAddress
          ) ?: BigInteger.ZERO

          TokenHolding(
            asset = meta,
            amount = Amount(raw = balance, decimals = meta.decimals)
          )
        }
      }
    }
    emit(balanceJob.awaitAll())
  }
}
package org.easy.wallet.data.adapter

import androidx.paging.PagingSource
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.trustwallet.core.CoinType
import kotlinx.coroutines.flow.Flow
import org.easy.wallet.data.interfaces.IChainAdapter
import org.easy.wallet.model.Address
import org.easy.wallet.model.FeePolicy
import org.easy.wallet.model.Token
import org.easy.wallet.model.TokenId
import org.easy.wallet.model.TokenStandard
import org.easy.wallet.model.Transfer
import org.easy.wallet.model.UnsignedTx
import org.easy.wallet.network.source.EtherScanController

class EvmAdapter(
  private val provider: EtherScanController
) : IChainAdapter {
  override val supportedStandards: Set<TokenStandard> =
    setOf(TokenStandard.NATIVE, TokenStandard.ERC20)

  override suspend fun getBalance(account: Address, token: Token): BigInteger {
    val balanceResult = provider.balance(account.value, chainId = token.chainId, token.contract)
    return balanceResult.fold(
      onSuccess = {
        runCatching {
          BigInteger.parseString(it)
        }.getOrElse {
          it.printStackTrace()
          BigInteger.ZERO
        }
      },
      onFailure = {
        it.printStackTrace()
        BigInteger.ZERO
      }
    )
  }

  override suspend fun streamBalances(accounts: List<Address>, tokens: List<Token>): Flow<Map<TokenId, BigInteger>> {
    TODO("Not yet implemented")
  }

  override suspend fun estimateTransferFee(
    from: Address,
    to: Address,
    token: Token,
    amount: BigInteger
  ): FeePolicy {
    TODO("Not yet implemented")
  }

  override suspend fun buildTransferTx(
    from: Address,
    to: Address,
    token: Token,
    amount: BigInteger,
    fee: FeePolicy?,
    memo: String?
  ): UnsignedTx {
    TODO("Not yet implemented")
  }

  override suspend fun signAndBroadcast(unsigned: UnsignedTx, coinType: CoinType): String {
    TODO("Not yet implemented")
  }

  override suspend fun getTransfers(
    account: Address,
    cursor: String?,
    pageSize: Int
  ): PagingSource<Int, Transfer> {
    TODO("Not yet implemented")
  }
}
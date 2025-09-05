package org.easy.wallet.data.adapter

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.trustwallet.core.CoinType
import org.easy.wallet.data.interfaces.IChainAdapter
import org.easy.wallet.data.paging.TransactionPagingSource
import org.easy.wallet.model.Address
import org.easy.wallet.model.ChainId
import org.easy.wallet.model.FeePolicy
import org.easy.wallet.model.Token
import org.easy.wallet.model.TokenStandard
import org.easy.wallet.model.Transfer
import org.easy.wallet.model.UnsignedTx
import org.easy.wallet.network.source.EtherScanController

class EvmAdapter(
  override val chainId: ChainId,
  private val provider: EtherScanController
) : IChainAdapter {
  override val supportedStandards: Set<TokenStandard> =
    setOf(TokenStandard.NATIVE, TokenStandard.ERC20)

  override suspend fun getBalance(account: Address, contract: String?): BigInteger {
    val balanceResult = provider.balance(account.value, chainId = chainId, contract)
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

  override fun getTransfers(
    account: Address,
    pageSize: Int
  ): Pager<Int, Transfer> {
    return Pager(
      config = PagingConfig(pageSize, prefetchDistance = 2),
      pagingSourceFactory = {
        TransactionPagingSource(
          ethereumController = provider,
          address = account,
          chainId = chainId
        )
      }
    )
  }
}
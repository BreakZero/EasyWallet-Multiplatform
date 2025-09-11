package org.easy.wallet.data.adapter

import androidx.paging.Pager
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.trustwallet.core.CoinType
import org.easy.wallet.data.interfaces.IChainAdapter
import org.easy.wallet.model.Address
import org.easy.wallet.model.ChainId
import org.easy.wallet.model.FeePolicy
import org.easy.wallet.model.Token
import org.easy.wallet.model.TokenStandard
import org.easy.wallet.model.Transfer
import org.easy.wallet.model.UnsignedTx

class BitcoinAdapter(
  override val chainId: ChainId
) : IChainAdapter {
  override val supportedStandards = setOf(TokenStandard.NATIVE)

  override suspend fun getBalance(account: Address, contract: String?): BigInteger = BigInteger.ONE

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

  override fun getTransfers(account: Address, pageSize: Int): Pager<Int, Transfer> {
    TODO("Not yet implemented")
  }
}
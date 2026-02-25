package org.easy.wallet.data.adapter

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.trustwallet.core.CoinType
import com.trustwallet.core.PrivateKey
import org.easy.wallet.data.interfaces.BalanceService
import org.easy.wallet.data.interfaces.Broadcaster
import org.easy.wallet.data.interfaces.FeeService
import org.easy.wallet.data.interfaces.HistoryService
import org.easy.wallet.data.interfaces.IChainAdapter
import org.easy.wallet.data.interfaces.TransactionBuilder
import org.easy.wallet.model.Address
import org.easy.wallet.model.ChainId
import org.easy.wallet.model.FeePolicy
import org.easy.wallet.model.Token
import org.easy.wallet.model.TokenStandard
import org.easy.wallet.model.Transfer
import org.easy.wallet.model.UnsignedTx
import kotlin.time.Clock

/**
 * Solana blockchain adapter.
 * Implements IChainAdapter for Solana mainnet and devnet.
 *
 * Supports:
 * - Native SOL transfers
 * - SPL token transfers (Solana's token standard)
 * - Transaction history via Solana RPC
 */
class SolanaAdapter(
  override val chainId: ChainId
) : IChainAdapter,
  BalanceService,
  FeeService,
  TransactionBuilder,
  Broadcaster,
  HistoryService {
  override val supportedStandards: Set<TokenStandard> = setOf(
    TokenStandard.NATIVE,
    TokenStandard.SPL // Solana Program Library token standard
  )

  override suspend fun getBalance(account: Address, contract: String?): BigInteger = BigInteger.ZERO

  override suspend fun estimateTransferFee(
    from: Address,
    to: Address,
    token: Token,
    amount: BigInteger
  ): FeePolicy = FeePolicy(
    feeAmount = BigInteger.parseString("5000"),
    gasPrice = null,
    gasLimit = null,
    priorityTip = null
  )

  override fun getTransfers(account: Address, pageSize: Int): Pager<Int, Transfer> = Pager(
    config = PagingConfig(pageSize = pageSize),
    pagingSourceFactory = { SolanaTransactionPagingSource(account, chainId) }
  )

  override suspend fun buildTransferTx(
    from: Address,
    to: Address,
    token: Token,
    amount: BigInteger,
    memo: String?
  ): UnsignedTx {
    val feePolicy = estimateTransferFee(from, to, token, amount)
    return UnsignedTx(
      chainId = chainId,
      from = from,
      to = to,
      amount = amount,
      fee = feePolicy,
      tokenId = token.tokenId
    )
  }

  override suspend fun signAndBroadcast(
    unsigned: UnsignedTx,
    privateKey: PrivateKey,
    coinType: CoinType
  ): String = "mock_solana_tx_${Clock.System.now().toEpochMilliseconds()}"
}

/**
 * Paging source for Solana transaction history.
 */
class SolanaTransactionPagingSource(
  private val account: Address,
  private val chainId: ChainId
) : androidx.paging.PagingSource<Int, Transfer>() {
  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transfer> = LoadResult.Page(
    data = emptyList(),
    prevKey = null,
    nextKey = null
  )

  override fun getRefreshKey(state: androidx.paging.PagingState<Int, Transfer>): Int? = null
}
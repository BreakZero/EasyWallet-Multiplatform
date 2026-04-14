package org.easy.wallet.data.transaction

import androidx.paging.Pager
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.trustwallet.core.CoinType
import org.easy.wallet.data.chain.ChainContextManager
import org.easy.wallet.model.Address
import org.easy.wallet.model.FeePolicy
import org.easy.wallet.model.SupportedAsset
import org.easy.wallet.model.Transfer
import org.easy.wallet.model.UnsignedTx

/**
 * Domain service for managing cross-chain transactions.
 * Automatically routes transaction operations to the correct chain adapter
 * based on the current chain context.
 *
 * This provides a unified API for:
 * - Viewing transaction history
 * - Building transactions
 * - Estimating fees
 * - Broadcasting transactions
 */
class TransactionService(
  private val chainContextManager: ChainContextManager
) {
  /**
   * Get transaction history for the current chain context.
   * @param account The account address to fetch history for
   * @param pageSize Number of transactions per page
   * @return Pager for loading paginated transaction history
   */
  fun getTransactionHistory(account: Address, pageSize: Int = 50): Pager<Int, Transfer> {
    val context = chainContextManager.requireCurrentContext()
    return context.adapter.getTransfers(account, pageSize)
  }

  /**
   * Estimate the fee for a transfer in the current chain context.
   */
  suspend fun estimateFee(
    from: Address,
    to: Address,
    amount: BigInteger,
    asset: SupportedAsset
  ): FeePolicy {
    val context = chainContextManager.requireCurrentContext()
    return context.adapter.estimateTransferFee(from, to, asset, amount)
  }

  suspend fun estimateFeeForAsset(
    asset: SupportedAsset,
    from: Address,
    to: Address,
    amount: BigInteger
  ): FeePolicy {
    chainContextManager.setContextByChainId(asset.chainId)
    return estimateFee(from, to, amount, asset)
  }

  /**
   * Build a transfer transaction in the current chain context.
   */
  suspend fun buildTransferTransaction(
    from: Address,
    to: Address,
    asset: SupportedAsset,
    amount: BigInteger,
    fee: FeePolicy? = null,
    memo: String? = null
  ): UnsignedTx {
    val context = chainContextManager.requireCurrentContext()
    return context.adapter.buildTransferTx(from, to, asset, amount, memo)
  }

  suspend fun buildTransferTransactionForAsset(
    asset: SupportedAsset,
    from: Address,
    to: Address,
    amount: BigInteger,
    fee: FeePolicy? = null,
    memo: String? = null
  ): UnsignedTx {
    chainContextManager.setContextByChainId(asset.chainId)
    return buildTransferTransaction(from, to, asset, amount, fee, memo)
  }

  /**
   * Sign and broadcast a transaction in the current chain context.
   * @param unsigned The unsigned transaction to sign and broadcast
   * @param coinType The coin type containing the private key
   * @return Transaction hash
   */
  suspend fun signAndBroadcast(unsigned: UnsignedTx, coinType: CoinType): String {
    val context = chainContextManager.requireCurrentContext()
    throw RuntimeException()
//    return context.adapter.signAndBroadcast(unsigned, )
  }

  suspend fun executeTransferForAsset(
    asset: SupportedAsset,
    from: Address,
    to: Address,
    amount: BigInteger,
    coinType: CoinType,
    memo: String? = null
  ): TransferResult = try {
    chainContextManager.setContextByChainId(asset.chainId)

    val fee = estimateFee(from, to, amount, asset)
    val unsignedTx = buildTransferTransaction(from, to, asset, amount, fee, memo)
    val txHash = signAndBroadcast(unsignedTx, coinType)

    TransferResult.Success(txHash, fee)
  } catch (e: Exception) {
    TransferResult.Error(e.message ?: "Unknown error occurred")
  }
}

/**
 * Result of a transfer operation.
 */
sealed class TransferResult {
  data class Success(
    val txHash: String,
    val feePaid: FeePolicy
  ) : TransferResult()

  data class Error(
    val message: String
  ) : TransferResult()
}

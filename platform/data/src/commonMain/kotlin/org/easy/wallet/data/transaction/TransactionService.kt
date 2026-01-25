package org.easy.wallet.data.transaction

import androidx.paging.Pager
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.trustwallet.core.CoinType
import org.easy.wallet.data.chain.ChainContextManager
import org.easy.wallet.data.repository.TokenRepository
import org.easy.wallet.model.Address
import org.easy.wallet.model.FeePolicy
import org.easy.wallet.model.Token
import org.easy.wallet.model.TokenId
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
  private val chainContextManager: ChainContextManager,
  private val tokenRepository: TokenRepository
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
   * Get transaction history for a specific token.
   * This sets the chain context automatically based on the token.
   */
  suspend fun getTransactionHistoryForToken(
    tokenId: TokenId,
    account: Address,
    pageSize: Int = 50
  ): Pager<Int, Transfer> {
    chainContextManager.setContextByToken(tokenId)
    return getTransactionHistory(account, pageSize)
  }

  /**
   * Estimate the fee for a transfer in the current chain context.
   */
  suspend fun estimateFee(
    from: Address,
    to: Address,
    amount: BigInteger,
    token: Token
  ): FeePolicy {
    val context = chainContextManager.requireCurrentContext()
    return context.adapter.estimateTransferFee(from, to, token, amount)
  }

  /**
   * Estimate fee for a specific token.
   * This sets the chain context automatically based on the token.
   */
  suspend fun estimateFeeForToken(
    tokenId: TokenId,
    from: Address,
    to: Address,
    amount: BigInteger
  ): FeePolicy {
    chainContextManager.setContextByToken(tokenId)
    val token = tokenRepository.getById(tokenId.value)
      ?: error("Token not found: ${tokenId.value}")
    return estimateFee(from, to, amount, token)
  }

  /**
   * Build a transfer transaction in the current chain context.
   */
  suspend fun buildTransferTransaction(
    from: Address,
    to: Address,
    token: Token,
    amount: BigInteger,
    fee: FeePolicy? = null,
    memo: String? = null
  ): UnsignedTx {
    val context = chainContextManager.requireCurrentContext()
    return context.adapter.buildTransferTx(from, to, token, amount, memo)
  }

  /**
   * Build a transfer transaction for a specific token.
   * This sets the chain context automatically based on the token.
   */
  suspend fun buildTransferTransactionForToken(
    tokenId: TokenId,
    from: Address,
    to: Address,
    amount: BigInteger,
    fee: FeePolicy? = null,
    memo: String? = null
  ): UnsignedTx {
    chainContextManager.setContextByToken(tokenId)
    val token = tokenRepository.getById(tokenId.value)
      ?: error("Token not found: ${tokenId.value}")
    return buildTransferTransaction(from, to, token, amount, fee, memo)
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

  /**
   * Complete transfer flow: estimate fee -> build tx -> sign and broadcast.
   * This is a convenience method for the common send flow.
   */
  suspend fun executeTransfer(
    tokenId: TokenId,
    from: Address,
    to: Address,
    amount: BigInteger,
    coinType: CoinType,
    memo: String? = null
  ): TransferResult = try {
    chainContextManager.setContextByToken(tokenId)
    val token = tokenRepository.getById(tokenId.value)
      ?: error("Token not found: ${tokenId.value}")

    // Step 1: Estimate fee
    val fee = estimateFee(from, to, amount, token)

    // Step 2: Build transaction
    val unsignedTx = buildTransferTransaction(from, to, token, amount, fee, memo)

    // Step 3: Sign and broadcast
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
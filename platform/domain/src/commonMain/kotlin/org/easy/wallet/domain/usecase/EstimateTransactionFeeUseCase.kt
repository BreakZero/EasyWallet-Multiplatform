package org.easy.wallet.domain.usecase

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.easy.wallet.data.transaction.TransactionService
import org.easy.wallet.model.Address
import org.easy.wallet.model.FeePolicy
import org.easy.wallet.model.SupportedAsset

/**
 * Use case for estimating transaction fees.
 * Automatically routes to the correct chain adapter based on the asset's chain.
 */
class EstimateTransactionFeeUseCase(
  private val transactionService: TransactionService
) {
  /**
   * Estimate the fee for a token transfer.
   * @param asset The asset to transfer
   * @param from Sender address
   * @param to Recipient address
   * @param amount Amount to send (in smallest unit)
   * @return FeePolicy with estimated fee details
   */
  suspend operator fun invoke(
    asset: SupportedAsset,
    from: Address,
    to: Address,
    amount: BigInteger
  ): FeePolicy = transactionService.estimateFeeForAsset(
    asset = asset,
    from = from,
    to = to,
    amount = amount
  )
}

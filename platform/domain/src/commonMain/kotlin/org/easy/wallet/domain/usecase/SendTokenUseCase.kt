package org.easy.wallet.domain.usecase

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.trustwallet.core.CoinType
import org.easy.wallet.data.transaction.TransactionService
import org.easy.wallet.data.transaction.TransferResult
import org.easy.wallet.model.Address
import org.easy.wallet.model.TokenId

/**
 * Use case for sending tokens.
 * Handles the complete flow: fee estimation -> transaction building -> signing -> broadcasting.
 * Automatically routes to the correct chain adapter based on the token's chain.
 */
class SendTokenUseCase(
    private val transactionService: TransactionService
) {
    /**
     * Execute a token transfer.
     * @param tokenId The token to send
     * @param from Sender address
     * @param to Recipient address
     * @param amount Amount to send (in smallest unit, e.g., wei for ETH)
     * @param coinType CoinType containing the private key for signing
     * @param memo Optional memo/note for the transaction
     * @return TransferResult indicating success or error
     */
    suspend operator fun invoke(
        tokenId: TokenId,
        from: Address,
        to: Address,
        amount: BigInteger,
        coinType: CoinType,
        memo: String? = null
    ): TransferResult {
        return transactionService.executeTransfer(
            tokenId = tokenId,
            from = from,
            to = to,
            amount = amount,
            coinType = coinType,
            memo = memo
        )
    }
}

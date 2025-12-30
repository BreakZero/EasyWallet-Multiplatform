package org.easy.wallet.domain.usecase

import androidx.paging.Pager
import org.easy.wallet.data.transaction.TransactionService
import org.easy.wallet.model.Address
import org.easy.wallet.model.TokenId
import org.easy.wallet.model.Transfer

/**
 * Use case for fetching transaction history for a specific token.
 * Automatically routes to the correct chain adapter based on the token's chain.
 */
class GetTransactionHistoryUseCase(
    private val transactionService: TransactionService
) {
    suspend operator fun invoke(
        tokenId: TokenId,
        account: Address,
        pageSize: Int = 50
    ): Pager<Int, Transfer> {
        return transactionService.getTransactionHistoryForToken(
            tokenId = tokenId,
            account = account,
            pageSize = pageSize
        )
    }
}

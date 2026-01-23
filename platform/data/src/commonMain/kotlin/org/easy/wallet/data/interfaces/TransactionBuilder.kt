package org.easy.wallet.data.interfaces

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.easy.wallet.model.Address
import org.easy.wallet.model.Token
import org.easy.wallet.model.UnsignedTx

interface TransactionBuilder {
  suspend fun buildTransferTx(
    from: Address,
    to: Address,
    token: Token,
    amount: BigInteger,
    memo: String? = null
  ): UnsignedTx
}
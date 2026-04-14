package org.easy.wallet.data.interfaces

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.easy.wallet.model.Address
import org.easy.wallet.model.SupportedAsset
import org.easy.wallet.model.UnsignedTx

interface TransactionBuilder {
  suspend fun buildTransferTx(
    from: Address,
    to: Address,
    asset: SupportedAsset,
    amount: BigInteger,
    memo: String? = null
  ): UnsignedTx
}

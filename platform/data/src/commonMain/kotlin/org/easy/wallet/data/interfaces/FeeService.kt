package org.easy.wallet.data.interfaces

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.easy.wallet.model.Address
import org.easy.wallet.model.FeePolicy
import org.easy.wallet.model.Token

interface FeeService {
  suspend fun estimateTransferFee(from: Address, to: Address, token: Token, amount: BigInteger): FeePolicy
}
package org.easy.wallet.data.interfaces

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.easy.wallet.model.Address

interface BalanceService {
  suspend fun getBalance(account: Address, contract: String?): BigInteger
}
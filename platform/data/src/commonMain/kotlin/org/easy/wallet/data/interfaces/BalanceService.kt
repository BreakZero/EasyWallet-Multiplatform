package org.easy.wallet.data.interfaces

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.coroutines.flow.Flow
import org.easy.wallet.model.Address
import org.easy.wallet.model.Token
import org.easy.wallet.model.TokenId

interface BalanceService {
  suspend fun getBalance(account: Address, token: Token): BigInteger

  suspend fun streamBalances(accounts: List<Address>, tokens: List<Token>): Flow<Map<TokenId, BigInteger>>
}
package org.easy.wallet.data.repository

interface BalanceRepository {
  suspend fun fetchBalance(address: String, contractAddress: String?): String
}
package org.easy.wallet.data.repository

import kotlinx.coroutines.flow.Flow
import org.easy.wallet.model.Assets
import org.easy.wallet.model.Balance

interface AssetsRepository {
  fun loadAllAssets(): Flow<List<Assets>>

  fun fetchBalance(address: List<Assets>): Flow<List<Balance>>
}
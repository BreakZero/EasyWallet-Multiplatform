package org.easy.wallet.data.repository

import kotlinx.coroutines.flow.Flow
import org.easy.wallet.model.Asset
import org.easy.wallet.model.Balance

interface AssetsRepository {
  fun loadAllAssets(): Flow<List<Asset>>

  fun fetchBalance(address: List<Asset>): Flow<List<Balance>>
}
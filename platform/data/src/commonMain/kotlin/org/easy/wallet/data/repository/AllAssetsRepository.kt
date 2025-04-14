package org.easy.wallet.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.easy.wallet.datastore.assets.AssetDataSource
import org.easy.wallet.model.Asset
import org.easy.wallet.model.Balance

class AllAssetsRepository(
  private val assetDataSource: AssetDataSource,
) : AssetsRepository {
  override fun loadAllAssets(): Flow<List<Asset>> = assetDataSource.loadAssets()

  override fun fetchBalance(address: List<Asset>): Flow<List<Balance>> = flow { emit(emptyList()) }
}
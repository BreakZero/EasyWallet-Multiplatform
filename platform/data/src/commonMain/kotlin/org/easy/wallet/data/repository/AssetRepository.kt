package org.easy.wallet.data.repository

import org.easy.wallet.model.Address
import org.easy.wallet.model.AssetBalance
import org.easy.wallet.model.AssetId
import org.easy.wallet.model.SupportedAsset

interface AssetRepository {
  suspend fun listAssets(): List<SupportedAsset>

  suspend fun getAsset(assetId: AssetId): SupportedAsset?

  suspend fun getBalance(asset: SupportedAsset, address: Address): AssetBalance?
}

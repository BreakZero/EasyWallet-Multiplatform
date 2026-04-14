package org.easy.wallet.data.repository

import org.easy.wallet.model.Address
import org.easy.wallet.model.AssetBalance
import org.easy.wallet.model.AssetId
import org.easy.wallet.model.SupportedAsset
import org.easy.wallet.network.source.ChainAssetGatewayController

class AssetRepositoryImpl internal constructor(
  private val gatewayController: ChainAssetGatewayController
) : AssetRepository {
  override suspend fun listAssets(): List<SupportedAsset> = gatewayController
    .listAssets()
    .getOrDefault(emptyList())

  override suspend fun getAsset(assetId: AssetId): SupportedAsset? = listAssets().firstOrNull { it.id == assetId }

  override suspend fun getBalance(asset: SupportedAsset, address: Address): AssetBalance? = gatewayController
    .balanceDetail(
      address = address.value,
      chainId = asset.chainId,
      contractAddress = asset.contractAddress
    )
    .getOrNull()
}

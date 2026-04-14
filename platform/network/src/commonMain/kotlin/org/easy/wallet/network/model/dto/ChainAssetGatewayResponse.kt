package org.easy.wallet.network.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ChainAssetGatewayResponse<T>(
  @SerialName("success")
  val success: Boolean,
  @SerialName("data")
  val data: T,
  @SerialName("meta")
  val meta: ChainAssetGatewayMeta? = null
)

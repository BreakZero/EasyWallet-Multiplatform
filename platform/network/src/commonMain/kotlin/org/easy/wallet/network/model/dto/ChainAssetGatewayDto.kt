package org.easy.wallet.network.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class ChainAssetGatewayMeta(
  @SerialName("requestId")
  val requestId: String? = null,
  @SerialName("source")
  val source: String? = null,
  @SerialName("updatedAt")
  val updatedAt: String? = null
)

@Serializable
internal data class GatewayAssetsPayload(
  @SerialName("items")
  val items: List<GatewayAssetDto> = emptyList(),
  @SerialName("total")
  val total: Int = 0
)

@Serializable
internal data class GatewayAssetDto(
  @SerialName("chain")
  val chain: String,
  @SerialName("chainId")
  val chainId: Int? = null,
  @SerialName("network")
  val network: String,
  @SerialName("assetType")
  val assetType: String,
  @SerialName("assetId")
  val assetId: String,
  @SerialName("symbol")
  val symbol: String? = null,
  @SerialName("name")
  val name: String? = null,
  @SerialName("contractAddress")
  val contractAddress: String? = null,
  @SerialName("decimals")
  val decimals: Int? = null,
  @SerialName("status")
  val status: String,
  @SerialName("source")
  val source: String,
  @SerialName("updatedAt")
  val updatedAt: String
)

@Serializable
internal data class GatewayAmountDto(
  @SerialName("raw")
  val raw: String,
  @SerialName("formatted")
  val formatted: String,
  @SerialName("decimals")
  val decimals: Int
)

@Serializable
internal data class GatewayBalanceDto(
  @SerialName("chain")
  val chain: String,
  @SerialName("chainId")
  val chainId: Int? = null,
  @SerialName("address")
  val address: String,
  @SerialName("asset")
  val asset: GatewayAssetDto,
  @SerialName("amount")
  val amount: GatewayAmountDto,
  @SerialName("priceUsd")
  val priceUsd: String? = null,
  @SerialName("valueUsd")
  val valueUsd: String? = null,
  @SerialName("source")
  val source: String,
  @SerialName("updatedAt")
  val updatedAt: String
)

@Serializable
internal data class GatewayTransactionDetailDto(
  @SerialName("chain")
  val chain: String,
  @SerialName("chainId")
  val chainId: Int? = null,
  @SerialName("hash")
  val hash: String,
  @SerialName("status")
  val status: String,
  @SerialName("blockNumber")
  val blockNumber: String? = null,
  @SerialName("from")
  val from: String? = null,
  @SerialName("to")
  val to: String? = null,
  @SerialName("valueRaw")
  val valueRaw: String? = null,
  @SerialName("valueFormatted")
  val valueFormatted: String? = null,
  @SerialName("decimals")
  val decimals: Int? = null,
  @SerialName("feeRaw")
  val feeRaw: String? = null,
  @SerialName("feeFormatted")
  val feeFormatted: String? = null,
  @SerialName("symbol")
  val symbol: String? = null,
  @SerialName("updatedAt")
  val updatedAt: String,
  @SerialName("source")
  val source: String
)

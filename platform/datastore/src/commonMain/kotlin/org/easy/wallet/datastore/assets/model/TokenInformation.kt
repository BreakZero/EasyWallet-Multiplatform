package org.easy.wallet.datastore.assets.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.easy.wallet.model.Asset
import org.easy.wallet.model.BasicAsset

@Serializable
internal data class TokenList(
  @SerialName("tokens")
  val tokens: List<TokenInformation>
)

@Serializable
internal data class TokenInformation(
  @SerialName("address")
  val address: String,
  @SerialName("asset")
  val asset: String,
  @SerialName("chainId")
  val chainId: Int,
  @SerialName("decimals")
  val decimals: Int,
  @SerialName("logoURI")
  val logoURI: String,
  @SerialName("name")
  val name: String,
  @SerialName("symbol")
  val symbol: String,
  @SerialName("type")
  val type: String
)

internal fun TokenInformation.toAssets(): Asset = BasicAsset(
  id = this.asset,
  contractAddress = address,
  coinName = name,
  decimals = decimals,
  symbol = symbol,
  logoUrl = logoURI
)
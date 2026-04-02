package org.easy.wallet.model

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.jvm.JvmInline

@JvmInline
value class AssetId(
  val value: String
)

enum class AssetType {
  NATIVE,
  ERC20
}

enum class AssetNetwork {
  MAINNET,
  TESTNET
}

data class SupportedAsset(
  val id: AssetId,
  val chainId: ChainId,
  val network: AssetNetwork,
  val type: AssetType,
  val symbol: String,
  val name: String,
  val contractAddress: String?,
  val decimals: Int,
  val logoUrl: String? = null,
  val source: String,
  val updatedAt: String
)

data class AssetBalance(
  val asset: SupportedAsset,
  val address: Address,
  val amount: Amount,
  val priceUsd: String? = null,
  val valueUsd: String? = null,
  val source: String,
  val updatedAt: String
)

fun SupportedAsset.zeroBalance(address: Address): AssetBalance = AssetBalance(
  asset = this,
  address = address,
  amount = Amount(BigInteger.ZERO, decimals),
  source = source,
  updatedAt = updatedAt
)

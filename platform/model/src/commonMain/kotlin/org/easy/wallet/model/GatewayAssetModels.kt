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
  val iconUrl: String? = null,
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

fun SupportedAsset.toToken(): Token = Token(
  tokenId = TokenId(id.value),
  chainId = chainId,
  standard = when (type) {
    AssetType.NATIVE -> TokenStandard.NATIVE
    AssetType.ERC20 -> TokenStandard.ERC20
  },
  contract = contractAddress,
  symbol = symbol,
  name = name,
  decimals = decimals,
  iconUrl = iconUrl,
  enabled = true,
  sortOrder = 0,
  createdAt = 0L,
  updatedAt = 0L
)

fun SupportedAsset.zeroBalance(address: Address): AssetBalance = AssetBalance(
  asset = this,
  address = address,
  amount = Amount(BigInteger.ZERO, decimals),
  source = source,
  updatedAt = updatedAt
)

package org.easy.wallet.model

interface Assets {
  val id: String
  val coinName: String
  val symbol: String
  val decimals: Int
  val contractAddress: String?
  val logoUrl: String?
}

data class BasicAssets(
  override val id: String,
  override val coinName: String,
  override val symbol: String,
  override val decimals: Int,
  override val contractAddress: String? = null,
  override val logoUrl: String? = null
) : Assets

data class Balance(
  override val id: String,
  override val coinName: String,
  override val symbol: String,
  override val decimals: Int,
  override val contractAddress: String? = null,
  override val logoUrl: String? = null,
  val balance: String = "0.0"
) : Assets

fun Assets.toBalance(balance: String = "0.0"): Balance = Balance(
  id = this.id,
  coinName = this.coinName,
  symbol = this.symbol,
  decimals = this.decimals,
  contractAddress = this.contractAddress,
  logoUrl = this.logoUrl,
  balance = balance
)
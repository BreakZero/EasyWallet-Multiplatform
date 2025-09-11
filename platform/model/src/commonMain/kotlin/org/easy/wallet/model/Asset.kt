package org.easy.wallet.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.integer.BigInteger

interface Asset {
  val id: TokenId
  val coinName: String
  val symbol: String
  val decimals: Int
  val contractAddress: String?
  val logoUrl: String?
  val displayDecimals: Int
}

data class BasicAsset(
  override val id: TokenId,
  override val coinName: String,
  override val symbol: String,
  override val decimals: Int,
  override val contractAddress: String? = null,
  override val logoUrl: String? = null,
  override val displayDecimals: Int = decimals
) : Asset

data class Balance(
  override val id: TokenId,
  override val coinName: String,
  override val symbol: String,
  override val decimals: Int,
  override val contractAddress: String? = null,
  override val logoUrl: String? = null,
  override val displayDecimals: Int = 8,
  val balance: BigInteger = BigInteger.ZERO
) : Asset {
  fun displayBalance(): String {
    val scaleFactor = BigDecimal.TEN.pow(decimals.toLong())

    return BigDecimal
      .fromBigInteger(balance)
      .divide(scaleFactor)
      .roundToDigitPositionAfterDecimalPoint(displayDecimals.toLong(), RoundingMode.ROUND_HALF_FLOOR)
      .toPlainString()
  }
}

fun Asset.toBalance(balance: BigInteger = BigInteger.ZERO): Balance = Balance(
  id = this.id,
  coinName = this.coinName,
  symbol = this.symbol,
  decimals = this.decimals,
  contractAddress = this.contractAddress,
  logoUrl = this.logoUrl,
  balance = balance
)
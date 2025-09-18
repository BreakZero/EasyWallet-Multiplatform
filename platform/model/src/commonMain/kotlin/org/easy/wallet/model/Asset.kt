package org.easy.wallet.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.integer.BigInteger

sealed interface TokenMeta {
  val id: TokenId
  val chainId: ChainId
  val standard: TokenStandard
  val name: String
  val symbol: String
  val decimals: Int
  val logoUrl: String?
}

data class NativeTokenMeta(
  override val id: TokenId,
  override val chainId: ChainId,
  override val standard: TokenStandard,
  override val name: String,
  override val symbol: String,
  override val decimals: Int,
  override val logoUrl: String? = null
) : TokenMeta

data class FungibleTokenMeta(
  override val id: TokenId,
  override val chainId: ChainId,
  override val standard: TokenStandard,
  override val name: String,
  override val symbol: String,
  override val decimals: Int,
  val contract: Address,
  override val logoUrl: String? = null
) : TokenMeta

data class Amount(
  val raw: BigInteger,
  val decimals: Int
) {
  init {
    require(decimals >= 0)
  }

  fun format(displayDecimals: Int = minOf(8, decimals), rounding: RoundingMode = RoundingMode.ROUND_HALF_FLOOR): String =
    AmountFormatter.formatBaseUnits(raw, decimals, displayDecimals, rounding)

  fun isZero() = raw == BigInteger.ZERO
}

data class TokenHolding(
  val asset: TokenMeta,
  val amount: Amount,
  val address: Address? = null
)

fun TokenMeta.zero(address: Address? = null): TokenHolding = TokenHolding(this, Amount(BigInteger.ZERO, decimals), address = address)

object AmountFormatter {
  fun formatBaseUnits(
    raw: BigInteger,
    decimals: Int,
    displayDecimals: Int = minOf(8, decimals),
    rounding: RoundingMode = RoundingMode.ROUND_HALF_FLOOR
  ): String {
    val scale = BigDecimal.TEN.pow(decimals.toLong())
    return BigDecimal
      .fromBigInteger(raw)
      .divide(scale)
      .roundToDigitPositionAfterDecimalPoint(displayDecimals.toLong(), rounding)
      .toPlainString()
  }
}
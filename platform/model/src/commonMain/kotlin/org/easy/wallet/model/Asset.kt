package org.easy.wallet.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.integer.BigInteger

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

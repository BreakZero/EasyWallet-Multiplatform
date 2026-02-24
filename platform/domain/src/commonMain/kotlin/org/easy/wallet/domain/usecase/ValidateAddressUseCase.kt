package org.easy.wallet.domain.usecase

import org.easy.wallet.model.ChainId

class ValidateAddressUseCase {
  operator fun invoke(address: String, chainId: ChainId): Boolean {
    if (address.isBlank()) return false
    return when {
      chainId.value.startsWith("evm") -> isValidEvmAddress(address)
      chainId.value.startsWith("btc") -> isValidBtcAddress(address)
      chainId.value.startsWith("solana") -> isValidSolanaAddress(address)
      else -> false
    }
  }

  private fun isValidEvmAddress(address: String): Boolean = address.startsWith("0x") &&
    address.length == 42 &&
    address.drop(2).all { it.isLetterOrDigit() }

  private fun isValidBtcAddress(address: String): Boolean = address.length in 26..62 &&
    (
      address.startsWith("1") ||
        address.startsWith("3") ||
        address.startsWith("bc1") ||
        address.startsWith("tb1")
    )

  private fun isValidSolanaAddress(address: String): Boolean = address.length in 32..44 && address.all { it.isLetterOrDigit() }
}
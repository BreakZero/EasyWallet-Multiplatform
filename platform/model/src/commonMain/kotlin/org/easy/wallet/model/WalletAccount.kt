package org.easy.wallet.model

data class WalletAccount(
  val id: String,
  val name: String,
  val mnemonic: String
)
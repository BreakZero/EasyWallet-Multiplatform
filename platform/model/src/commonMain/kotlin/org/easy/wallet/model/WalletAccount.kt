package org.easy.wallet.model

data class WalletAccount(
  val id: String,
  val name: String,
  val createdAt: Long,
  val alias: String,
)

package org.easy.wallet.model

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.jvm.JvmInline

@JvmInline
value class ChainId(
  val value: String
) {
  companion object {
    val EVM_MAINNET = ChainId("evm:1")
    val Polygon_MAINNET = ChainId("evm:137")
    val Arbitrum_MAINNET = ChainId("evm:42161")
    val BTC_MAINNET = ChainId("btc:main")
    val SOLANA_MAINNET = ChainId("solana:mainnet")
    val SOLANA_DEVNET = ChainId("solana:devnet")
    val SOLANA_TESTNET = ChainId("solana:testnet")
  }
}

@JvmInline
value class Address(
  val value: String
)

@JvmInline
value class TokenId(
  val value: String
)

enum class TokenStandard { NATIVE, ERC20, ERC721, SPL, TRC20 }

data class Token(
  val tokenId: TokenId,
  val chainId: ChainId,
  val standard: TokenStandard,
  val contract: String?,
  val symbol: String,
  val name: String,
  val decimals: Int,
  val iconUrl: String?,
  val enabled: Boolean,
  val sortOrder: Int,
  val createdAt: Long,
  val updatedAt: Long
)

sealed class TxStatus {
  object Pending : TxStatus()

  object Success : TxStatus()

  data class Failed(
    val reason: String?
  ) : TxStatus()
}

data class Transfer(
  val txHash: String,
  val chainId: ChainId,
  val timestamp: Long,
  val from: Address,
  val to: Address,
  val tokenId: TokenId,
  val amount: BigInteger,
  val feePaid: BigInteger?,
  val status: TxStatus,
  val memo: String? = null
)

data class FeePolicy(
  val feeAmount: BigInteger?,
  val gasPrice: BigInteger? = null,
  val gasLimit: BigInteger? = null,
  val priorityTip: BigInteger? = null
)

data class UnsignedTx(
  val chainId: ChainId,
  val from: Address,
  val to: Address?,
  val tokenId: TokenId,
  val amount: BigInteger?,
  val fee: FeePolicy?,
  val nonce: Long? = null,
  val rawMessage: String? = null,
  val metadata: Map<String, Any?> = emptyMap()
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false

    other as UnsignedTx

    if (nonce != other.nonce) return false
    if (chainId != other.chainId) return false
    if (from != other.from) return false
    if (to != other.to) return false
    if (tokenId != other.tokenId) return false
    if (amount != other.amount) return false
    if (fee != other.fee) return false
    if (!rawMessage.equals(other.rawMessage)) return false
    if (metadata != other.metadata) return false

    return true
  }

  override fun hashCode(): Int {
    var result = nonce?.hashCode() ?: 0
    result = 31 * result + chainId.hashCode()
    result = 31 * result + from.hashCode()
    result = 31 * result + (to?.hashCode() ?: 0)
    result = 31 * result + tokenId.hashCode()
    result = 31 * result + (amount?.hashCode() ?: 0)
    result = 31 * result + (fee?.hashCode() ?: 0)
    result = 31 * result + (rawMessage?.hashCode() ?: 0)
    result = 31 * result + metadata.hashCode()
    return result
  }
}
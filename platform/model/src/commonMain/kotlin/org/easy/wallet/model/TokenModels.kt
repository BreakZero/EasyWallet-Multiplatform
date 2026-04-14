package org.easy.wallet.model

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.jvm.JvmInline

@JvmInline
value class ChainId(
  val value: String
) {
  companion object {
    // Mainnet ChainIds
    val EVM_MAINNET = ChainId("evm:1")
    val Polygon_MAINNET = ChainId("evm:137")
    val Arbitrum_MAINNET = ChainId("evm:42161")
    val BTC_MAINNET = ChainId("btc:main")
    val SOLANA_MAINNET = ChainId("solana:mainnet")
    val SOLANA_DEVNET = ChainId("solana:devnet")
    val SOLANA_TESTNET = ChainId("solana:testnet")

    // Testnet ChainIds
    val EVM_SEPOLIA = ChainId("evm:11155111")
    val Polygon_AMOY = ChainId("evm:80002")
    val Arbitrum_SEPOLIA = ChainId("evm:421614")
    val BTC_TESTNET = ChainId("btc:test")

    /**
     * Get the testnet variant of a mainnet ChainId.
     * Returns null if the chain doesn't have a testnet variant or is already a testnet.
     */
    fun getTestnetVariant(mainnetChainId: ChainId): ChainId? = when (mainnetChainId) {
      EVM_MAINNET -> EVM_SEPOLIA
      Polygon_MAINNET -> Polygon_AMOY
      Arbitrum_MAINNET -> Arbitrum_SEPOLIA
      BTC_MAINNET -> BTC_TESTNET
      SOLANA_MAINNET -> SOLANA_TESTNET
      else -> null
    }

    /**
     * Get the mainnet variant of a ChainId.
     * If the chain is already mainnet, returns it as-is.
     */
    fun getMainnetVariant(chainId: ChainId): ChainId = when (chainId) {
      EVM_SEPOLIA -> EVM_MAINNET
      Polygon_AMOY -> Polygon_MAINNET
      Arbitrum_SEPOLIA -> Arbitrum_MAINNET
      BTC_TESTNET -> BTC_MAINNET
      SOLANA_TESTNET, SOLANA_DEVNET -> SOLANA_MAINNET
      else -> chainId
    }
  }
}

@JvmInline
value class Address(
  val value: String
)

enum class TokenStandard { NATIVE, ERC20, ERC721, SPL, TRC20 }

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
  val assetId: AssetId,
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
  val assetId: AssetId,
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
    if (assetId != other.assetId) return false
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
    result = 31 * result + assetId.hashCode()
    result = 31 * result + (amount?.hashCode() ?: 0)
    result = 31 * result + (fee?.hashCode() ?: 0)
    result = 31 * result + (rawMessage?.hashCode() ?: 0)
    result = 31 * result + metadata.hashCode()
    return result
  }
}

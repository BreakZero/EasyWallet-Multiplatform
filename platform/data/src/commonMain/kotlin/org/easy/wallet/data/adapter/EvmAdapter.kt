package org.easy.wallet.data.adapter

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.trustwallet.core.AnySigner
import com.trustwallet.core.CoinType
import com.trustwallet.core.PrivateKey
import com.trustwallet.core.ethereum.SigningInput
import com.trustwallet.core.ethereum.SigningOutput
import com.trustwallet.core.ethereum.Transaction
import com.trustwallet.core.sign
import org.easy.wallet.data.interfaces.IChainAdapter
import org.easy.wallet.data.paging.TransactionPagingSource
import org.easy.wallet.data.util.clearHexString
import org.easy.wallet.model.Address
import org.easy.wallet.model.ChainId
import org.easy.wallet.model.FeePolicy
import org.easy.wallet.model.Token
import org.easy.wallet.model.TokenStandard
import org.easy.wallet.model.Transfer
import org.easy.wallet.model.UnsignedTx
import org.easy.wallet.network.source.EtherScanController

private const val ZERO_UINT = "0000000000000000000000000000000000000000000000000000000000000000"

class EvmAdapter(
  override val chainId: ChainId,
  private val provider: EtherScanController
) : IChainAdapter {
  override val supportedStandards: Set<TokenStandard> =
    setOf(TokenStandard.NATIVE, TokenStandard.ERC20)

  override suspend fun getBalance(account: Address, contract: String?): BigInteger {
    val balanceResult = provider.balance(account.value, chainId = chainId, contract)
    return balanceResult.fold(
      onSuccess = {
        runCatching {
          BigInteger.parseString(it)
        }.getOrElse {
          it.printStackTrace()
          BigInteger.ZERO
        }
      },
      onFailure = {
        it.printStackTrace()
        BigInteger.ZERO
      }
    )
  }

  override suspend fun estimateTransferFee(
    from: Address,
    to: Address,
    token: Token,
    amount: BigInteger
  ): FeePolicy = FeePolicy(
    gasLimit = BigInteger.ZERO,
    gasPrice = BigInteger.ZERO,
    feeAmount = BigInteger.ZERO,
    priorityTip = BigInteger.ZERO
  )

  override suspend fun buildTransferTx(
    from: Address,
    to: Address,
    token: Token,
    amount: BigInteger,
    memo: String?
  ): UnsignedTx {
    val feePolicy = estimateTransferFee(
      from = from,
      to = to,
      token = token,
      amount = amount
    )
    return UnsignedTx(
      chainId = chainId,
      from = from,
      to = to,
      amount = amount,
      fee = feePolicy,
      nonce = 0L,
      tokenId = token.tokenId
    )
  }

  override suspend fun signAndBroadcast(
    unsigned: UnsignedTx,
    privateKey: PrivateKey,
    coinType: CoinType
  ): String {
    val signingInput = SigningInput(
      private_key = privateKey.data.decodeToString().clearHexString(),
      chain_id = unsigned.chainId.value.clearHexString(),
      nonce = checkNotNull(unsigned.nonce).toString(16).clearHexString(),
      gas_price = checkNotNull(unsigned.fee?.gasPrice).toString(16).clearHexString(),
      gas_limit = checkNotNull(unsigned.fee?.gasLimit).toString(16).clearHexString(),
      to_address = checkNotNull(unsigned.to).value,
      transaction = Transaction(
        transfer = Transaction.Transfer(
          amount = checkNotNull(unsigned.amount).toString(16).clearHexString()
        )
      )
    )
    val output = AnySigner.sign(signingInput, coinType, SigningOutput.ADAPTER)
    return "0x${output.encoded.hex()}"
  }

  override fun getTransfers(account: Address, pageSize: Int): Pager<Int, Transfer> = Pager(
    config = PagingConfig(pageSize, prefetchDistance = 2),
    pagingSourceFactory = {
      TransactionPagingSource(
        ethereumController = provider,
        address = account,
        chainId = chainId
      )
    }
  )
}
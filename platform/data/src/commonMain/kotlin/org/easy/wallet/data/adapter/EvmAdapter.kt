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
import org.easy.wallet.data.util.clearHexString
import org.easy.wallet.model.Address
import org.easy.wallet.model.AssetType
import org.easy.wallet.model.ChainId
import org.easy.wallet.model.FeePolicy
import org.easy.wallet.model.SupportedAsset
import org.easy.wallet.model.Transfer
import org.easy.wallet.model.UnsignedTx
import org.easy.wallet.network.mapper.toGatewayEvmChainIdOrNull
import org.easy.wallet.network.source.ChainAssetGatewayController

private const val ZERO_UINT = "0000000000000000000000000000000000000000000000000000000000000000"

class EvmAdapter(
  override val chainId: ChainId,
  private val gatewayController: ChainAssetGatewayController
) : IChainAdapter {
  override val supportedAssetTypes: Set<AssetType> =
    setOf(AssetType.NATIVE, AssetType.ERC20)

  override suspend fun getBalance(account: Address, contract: String?): BigInteger {
    val gatewayChainId = chainId.toGatewayEvmChainIdOrNull() ?: return BigInteger.ZERO
    val balanceResult = gatewayController.balance(
      address = account.value,
      chainId = ChainId("evm:$gatewayChainId"),
      contractAddress = contract
    )
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
    asset: SupportedAsset,
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
    asset: SupportedAsset,
    amount: BigInteger,
    memo: String?
  ): UnsignedTx {
    val feePolicy = estimateTransferFee(
      from = from,
      to = to,
      asset = asset,
      amount = amount
    )
    return UnsignedTx(
      chainId = chainId,
      from = from,
      to = to,
      amount = amount,
      fee = feePolicy,
      nonce = 0L,
      assetId = asset.id
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
    pagingSourceFactory = { EmptyEvmHistoryPagingSource() }
  )
}

private class EmptyEvmHistoryPagingSource : androidx.paging.PagingSource<Int, Transfer>() {
  override fun getRefreshKey(state: androidx.paging.PagingState<Int, Transfer>): Int? = null

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transfer> = LoadResult.Page(
    data = emptyList(),
    prevKey = null,
    nextKey = null
  )
}

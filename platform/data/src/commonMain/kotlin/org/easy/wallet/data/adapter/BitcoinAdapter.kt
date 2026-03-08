package org.easy.wallet.data.adapter

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import co.touchlab.kermit.Logger
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.trustwallet.core.AnySigner
import com.trustwallet.core.BitcoinSigHashType
import com.trustwallet.core.CoinType
import com.trustwallet.core.PrivateKey
import com.trustwallet.core.bitcoin.OutPoint
import com.trustwallet.core.bitcoin.SigningInput
import com.trustwallet.core.bitcoin.SigningOutput
import com.trustwallet.core.bitcoin.UnspentTransaction
import com.trustwallet.core.sign
import okio.ByteString.Companion.decodeHex
import org.easy.wallet.data.interfaces.IChainAdapter
import org.easy.wallet.model.Address
import org.easy.wallet.model.ChainId
import org.easy.wallet.model.FeePolicy
import org.easy.wallet.model.Token
import org.easy.wallet.model.TokenStandard
import org.easy.wallet.model.Transfer
import org.easy.wallet.model.UnsignedTx

class BitcoinAdapter(
  override val chainId: ChainId
) : IChainAdapter {
  override val supportedStandards = setOf(TokenStandard.NATIVE)

  override suspend fun getBalance(account: Address, contract: String?): BigInteger = BigInteger.ONE

  override suspend fun estimateTransferFee(
    from: Address,
    to: Address,
    token: Token,
    amount: BigInteger
  ): FeePolicy = FeePolicy(
    feeAmount = BigInteger.parseString("10000"),
    gasPrice = null,
    gasLimit = null,
    priorityTip = null
  )

  override suspend fun buildTransferTx(
    from: Address,
    to: Address,
    token: Token,
    amount: BigInteger,
    memo: String?
  ): UnsignedTx {
    val feePolicy = estimateTransferFee(from, to, token, amount)
    return UnsignedTx(
      chainId = chainId,
      from = from,
      to = to,
      amount = amount,
      fee = feePolicy,
      tokenId = token.tokenId
    )
  }

  override suspend fun signAndBroadcast(
    unsigned: UnsignedTx,
    privateKey: PrivateKey,
    coinType: CoinType
  ): String {
    val input = buildTransactionInput()
    val output = AnySigner.sign(input, CoinType.Bitcoin, SigningOutput.ADAPTER)

    val encodedTransaction = output.encoded.toByteArray().toHexString()

    return encodedTransaction.also {
      Logger.d("===== Signed Transaction (Hex): $encodedTransaction")
    }
  }

  private fun buildTransactionInput(): SigningInput {
    val toAddress = "1Bp9U1ogV3A14FMvKbRJms7ctyso4Z4Tcx"
    val changeAddress = "1FQc5LdgGHMHEN9nwkjmz6tWkxhPpxBvBU"
    val amountToSend: Long = 335_790_000
    val byteFee: Long = 1

    val privateKeys = listOf(
      "bbc27228ddcb9209d7fd6f36b02f7dfa6252af40bb2f1cbc7a557da8027ff866".decodeHex(),
      "619c335025c7f4012e556c2a58b2506e30b8511b53ade95ea316fd8c3286feb9".decodeHex()
    )

    val utxos = listOf(
      UnspentTransaction(
        amount = 625_000_000,
        script = "2103c9f4836b9a4f77fc0d81f7bcb01b7f1b35916864b9476c241ce9fc198bd25432ac".decodeHex(),
        out_point = OutPoint(
          hash = "fff7f7881a8099afa6940d42d1e7f6362bec38171ea3edf433541db4e4ad969f".decodeHex(),
          index = 0,
          sequence = UInt.MAX_VALUE.toInt() // 使用 UInt.MAX_VALUE 更符合意图
        )
      ),
      UnspentTransaction(
        amount = 600_000_000,
        script = "00141d0f172a0ecb48aee1be1f2687d2963ae33f71a1".decodeHex(),
        out_point = OutPoint(
          hash = "ef51e1b804cc89d182d279655c3aa89e815b1b309fe287d9b2b55d57b90ec68a".decodeHex(),
          index = 1,
          sequence = UInt.MAX_VALUE.toInt()
        )
      )
    )

    return SigningInput(
      amount = amountToSend,
      hash_type = BitcoinSigHashType.All.value.toInt(),
      to_address = toAddress,
      change_address = changeAddress,
      byte_fee = byteFee,
      private_key = privateKeys,
      utxo = utxos
    )
  }

  override fun getTransfers(account: Address, pageSize: Int): Pager<Int, Transfer> = Pager(
    config = PagingConfig(pageSize = pageSize, prefetchDistance = 2),
    pagingSourceFactory = { BitcoinTransactionPagingSource() }
  )
}

private class BitcoinTransactionPagingSource : PagingSource<Int, Transfer>() {
  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transfer> = LoadResult.Page(
    data = emptyList(),
    prevKey = null,
    nextKey = null
  )

  override fun getRefreshKey(state: PagingState<Int, Transfer>): Int? = null
}
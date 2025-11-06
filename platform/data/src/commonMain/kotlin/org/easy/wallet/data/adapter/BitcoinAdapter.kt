package org.easy.wallet.data.adapter

import androidx.paging.Pager
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.trustwallet.core.AnySigner
import com.trustwallet.core.BitcoinScript
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
  ): FeePolicy {
    TODO("Not yet implemented")
  }

  override suspend fun buildTransferTx(
    from: Address,
    to: Address,
    token: Token,
    amount: BigInteger,
    memo: String?
  ): UnsignedTx {
    TODO("Not yet implemented")
  }

  override suspend fun signAndBroadcast(
    unsigned: UnsignedTx,
    privateKey: PrivateKey,
    coinType: CoinType
  ): String {
    val input = SigningInput(
      amount = 1L,
      hash_type = BitcoinScript.hashTypeForCoin(CoinType.Bitcoin).toInt(),
      to_address = "1Bp9U1ogV3A14FMvKbRJms7ctyso4Z4Tcx",
      change_address = "1FQc5LdgGHMHEN9nwkjmz6tWkxhPpxBvBU",
      byte_fee = 1
    )

    val utxoKey0 = "bbc27228ddcb9209d7fd6f36b02f7dfa6252af40bb2f1cbc7a557da8027ff866".decodeHex()
    val utxoKey1 = "619c335025c7f4012e556c2a58b2506e30b8511b53ade95ea316fd8c3286feb9".decodeHex()
    input.copy(private_key = listOf(utxoKey0, utxoKey1))

    val output0 = OutPoint(
      hash = "fff7f7881a8099afa6940d42d1e7f6362bec38171ea3edf433541db4e4ad969f".decodeHex(),
      index = 0,
      sequence = Long.MAX_VALUE.toInt()
    )

    val utxo0 = UnspentTransaction(
      amount = 625_000_000,
      out_point = output0,
      script = "2103c9f4836b9a4f77fc0d81f7bcb01b7f1b35916864b9476c241ce9fc198bd25432ac".decodeHex()
    )

    val output1 = OutPoint(
      hash = "ef51e1b804cc89d182d279655c3aa89e815b1b309fe287d9b2b55d57b90ec68a".decodeHex(),
      index = 1,
      sequence = Long.MAX_VALUE.toInt()
    )

    val utxo1 = UnspentTransaction(
      amount = 600_000_000,
      out_point = output1,
      script = "00141d0f172a0ecb48aee1be1f2687d2963ae33f71a1".decodeHex()
    )

    input.copy(utxo = listOf(utxo0, utxo1))

    val output = AnySigner.sign(input, coinType, SigningOutput.ADAPTER)

    val signedTransaction = output.transaction
    val encoded = output.encoded

    return encoded.hex().also { println("===== $it") }
  }

  override fun getTransfers(account: Address, pageSize: Int): Pager<Int, Transfer> {
    TODO("Not yet implemented")
  }
}
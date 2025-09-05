package org.easy.wallet.network.source

import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import org.easy.wallet.model.ChainId
import org.easy.wallet.model.Transfer
import org.easy.wallet.network.model.dto.EtherScanBaseResponse
import org.easy.wallet.network.model.dto.EvmTransactionDTO
import org.easy.wallet.network.safeGet

class EtherScanController internal constructor(
  private val httpClient: HttpClient
) {
  suspend fun balance(
    address: String,
    chainId: ChainId,
    contractAddress: String? = null
  ): Result<String> {
    val action = if (contractAddress.isNullOrBlank()) "balance" else "tokenbalance"
    val chainid = chainId.value.split(":").last()

    val result = httpClient
      .safeGet<EtherScanBaseResponse<String>>("") {
        parameter("module", "account")
        parameter("action", action)
        parameter("chainid", chainid)
        parameter("address", address)
        if (!contractAddress.isNullOrBlank()) {
          parameter("contractaddress", contractAddress)
        }
        parameter("tag", "latest")
      }
    return result.map {
      if (it.status == "1") {
        it.result
      } else {
        "0.00"
      }
    }
  }

  suspend fun listNormalTransfer(
    address: String,
    chainId: ChainId,
    page: Int,
    offset: Int,
    sort: String = "asc"
  ): List<Transfer> {
    val chainid = chainId.value.split(":").last()

    val result = httpClient
      .safeGet<EtherScanBaseResponse<List<EvmTransactionDTO>>>("") {
        parameter("module", "account")
        parameter("action", "txlist")
        parameter("chainid", chainid)
        parameter("address", address)
        parameter("page", page)
        parameter("offset", offset)
        parameter("sort", sort)
      }
    println("===== $result")
    return emptyList()
  }

  suspend fun tokenTransfer(
    address: String,
    chainId: ChainId,
    contract: String,
    page: Int,
    offset: Int,
    sort: String = "asc"
  ): List<Transfer> {
    val chainid = chainId.value.split(":").last()
    val result = httpClient
      .safeGet<EtherScanBaseResponse<List<EvmTransactionDTO>>>("") {
        parameter("module", "account")
        parameter("action", "tokentx")
        parameter("chainid", chainid)
        parameter("address", address)
        parameter("contractaddress", contract)
        parameter("page", page)
        parameter("offset", offset)
        parameter("sort", sort)
      }
    println("===== $result")
    return emptyList()
  }
}
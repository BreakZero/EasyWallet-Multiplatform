package org.easy.wallet.network.source

import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import org.easy.wallet.network.model.dto.EtherScanBaseResponse
import org.easy.wallet.network.safeGet

class EtherScanController internal constructor(
  private val httpClient: HttpClient
) {
  suspend fun balance(address: String, contractAddress: String? = null): String {
    val action = if (contractAddress.isNullOrBlank()) {
      "balance"
    } else {
      "tokenbalance"
    }
    val response = httpClient
      .safeGet<EtherScanBaseResponse<String>>("") {
        parameter("module", "account")
        parameter("action", action)
        parameter("address", address)
        if (!contractAddress.isNullOrBlank()) {
          parameter("contractaddress", contractAddress)
        }
        parameter("tag", "latest")
      }.getOrNull()
    return if (response != null && response.status == "1") {
      response.result
    } else {
      "0.00"
    }
  }
}
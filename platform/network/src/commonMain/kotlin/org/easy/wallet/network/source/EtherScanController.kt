package org.easy.wallet.network.source

import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import org.easy.wallet.network.model.dto.EtherScanBaseResponse
import org.easy.wallet.network.safeGet

class EtherScanController internal constructor(
  private val httpClient: HttpClient
) {
  suspend fun balance(address: String, contractAddress: String? = null): Result<String> {
    val action = if (contractAddress.isNullOrBlank()) "balance" else "tokenbalance"

    val result = httpClient
      .safeGet<EtherScanBaseResponse<String>>("") {
        parameter("module", "account")
        parameter("action", action)
        parameter("address", address)
        if (!contractAddress.isNullOrBlank()) {
          parameter("contractaddress", contractAddress)
        }
        parameter("tag", "latest")
      }
    return result.map {
      if (it.status == "1") {
        "0.00"
      } else {
        it.result
      }
    }
  }
}
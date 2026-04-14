package org.easy.wallet.network.source

import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import org.easy.wallet.model.AssetBalance
import org.easy.wallet.model.ChainId
import org.easy.wallet.model.SupportedAsset
import org.easy.wallet.network.NetworkConfigProvider
import org.easy.wallet.network.mapper.toAssetBalanceOrNull
import org.easy.wallet.network.mapper.toAssetOrNull
import org.easy.wallet.network.mapper.toGatewayEvmChainIdOrNull
import org.easy.wallet.network.model.dto.ChainAssetGatewayResponse
import org.easy.wallet.network.model.dto.GatewayAssetsPayload
import org.easy.wallet.network.model.dto.GatewayBalanceDto
import org.easy.wallet.network.model.dto.GatewayTransactionDetailDto
import org.easy.wallet.network.safeGet

class ChainAssetGatewayController internal constructor(
  private val httpClient: HttpClient,
  private val configProvider: NetworkConfigProvider
) {
  suspend fun listAssets(): Result<List<SupportedAsset>> {
    val url = "${configProvider.getChainAssetGatewayBaseUrl().trimEnd('/')}/v1/assets"
    return httpClient
      .safeGet<ChainAssetGatewayResponse<GatewayAssetsPayload>>(url)
      .map { response -> response.data.items.mapNotNull { it.toAssetOrNull() } }
  }

  suspend fun balance(
    address: String,
    chainId: ChainId,
    contractAddress: String? = null
  ): Result<String> = balanceDetail(
    address = address,
    chainId = chainId,
    contractAddress = contractAddress
  ).map { it.amount.raw.toString() }

  suspend fun balanceDetail(
    address: String,
    chainId: ChainId,
    contractAddress: String? = null
  ): Result<AssetBalance> {
    val path = when {
      chainId == ChainId.BTC_MAINNET -> {
        if (contractAddress != null) {
          return Result.failure(
            IllegalArgumentException("Bitcoin native balance does not support contractAddress")
          )
        }
        "/v1/balances/bitcoin/$address/native"
      }

      else -> {
        val evmChainId = chainId.toGatewayEvmChainIdOrNull()
          ?: return Result.failure(IllegalArgumentException("Unsupported gateway chain: ${chainId.value}"))
        if (contractAddress != null) {
          "/v1/balances/ethereum/$address/erc20/${contractAddress.lowercase()}"
        } else {
          "/v1/balances/ethereum/$address/native"
        }
      }
    }
    val url = "${configProvider.getChainAssetGatewayBaseUrl().trimEnd('/')}$path"

    val response = httpClient.safeGet<ChainAssetGatewayResponse<GatewayBalanceDto>>(url) {
      parameter("includePrice", true)
      chainId.toGatewayEvmChainIdOrNull()?.let { evmChainId ->
        parameter("chainId", evmChainId)
      }
    }
    return response.mapCatching { result ->
      result.data.toAssetBalanceOrNull()
        ?: error("Unable to map gateway balance response for $address on ${chainId.value}")
    }
  }

  internal suspend fun getEthereumTransactionDetail(
    txHash: String,
    chainId: ChainId
  ): Result<GatewayTransactionDetailDto> {
    val evmChainId = chainId.toGatewayEvmChainIdOrNull()
      ?: return Result.failure(IllegalArgumentException("Unsupported gateway chain: ${chainId.value}"))
    val url = "${configProvider.getChainAssetGatewayBaseUrl().trimEnd('/')}/v1/transactions/ethereum/$txHash"
    return httpClient.safeGet<ChainAssetGatewayResponse<GatewayTransactionDetailDto>>(url) {
      parameter("chainId", evmChainId)
    }.map { it.data }
  }
}

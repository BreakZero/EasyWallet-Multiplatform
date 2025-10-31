package org.easy.wallet.network.di

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.easy.wallet.network.BuildKonfig
import org.easy.wallet.network.httpClient
import org.easy.wallet.network.source.BlockChairController
import org.easy.wallet.network.source.EtherScanController
import org.easy.wallet.network.source.GeckoController
import org.koin.core.qualifier.named
import org.koin.dsl.module

private enum class SourceQualifier {
  BLOCK_CHAIR,
  ETHER_SCAN,
  GECKO
}

private fun httpClientWithDefault(serializersModule: SerializersModule? = null, config: HttpClientConfig<*>.() -> Unit = {}): HttpClient =
  httpClient {
    install(HttpCookies)
    install(ContentNegotiation) {
      json(
        Json {
          prettyPrint = true
          isLenient = true
          ignoreUnknownKeys = true
          allowStructuredMapKeys = true
          serializersModule?.let {
            this.serializersModule = it
          }
        }
      )
    }
    install(Logging) {
      level = LogLevel.BODY
      logger = object : io.ktor.client.plugins.logging.Logger {
        override fun log(message: String) {
          Logger.d { message }
        }
      }
    }
    config()
  }

val networkModule = module {
  single(qualifier = named(SourceQualifier.BLOCK_CHAIR)) {
    httpClientWithDefault {
      defaultRequest {
        url {
          protocol = URLProtocol.HTTPS
          host = "api.blockchair.com"
          path("/")
        }
        header("Content-Type", "application/json")
      }
    }
  }
  single(qualifier = named(SourceQualifier.ETHER_SCAN)) {
    httpClientWithDefault {
      defaultRequest {
        url {
          protocol = URLProtocol.HTTPS
          host = "api.etherscan.io"
//        host = "api-sepolia.etherscan.io"
          path("v2/api/")
          parameters.append("apikey", BuildKonfig.ETHERSCAN_KEY)
        }
        header("Content-Type", "application/json")
      }
    }
  }

  single(qualifier = named(SourceQualifier.GECKO)) {
    // https://api.coingecko.com/api/v3/
    httpClientWithDefault {
      defaultRequest {
        url {
          protocol = URLProtocol.HTTPS
          host = "api.coingecko.com"
//        host = "api-sepolia.etherscan.io"
          path("api/v3/")
          parameters.append("x_cg_demo_api_key", BuildKonfig.COINGECKO_KEY)
        }
        header("Content-Type", "application/json")
      }
    }
  }

  factory { BlockChairController(get(qualifier = named(SourceQualifier.BLOCK_CHAIR))) }
  factory { EtherScanController(get(qualifier = named(SourceQualifier.ETHER_SCAN))) }
  factory { GeckoController(get(qualifier = named(SourceQualifier.GECKO))) }
}
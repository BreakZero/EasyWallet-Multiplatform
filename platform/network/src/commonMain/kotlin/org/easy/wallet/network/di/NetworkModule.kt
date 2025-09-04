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
import org.easy.wallet.network.httpClient
import org.easy.wallet.network.source.BlockChairController
import org.easy.wallet.network.source.EtherScanController
import org.koin.core.qualifier.named
import org.koin.dsl.module

private enum class SourceQualifier {
  BLOCK_CHAIR,
  ETHER_SCAN
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
          // TODO add api key
          parameters.append("apikey", "")
        }
        header("Content-Type", "application/json")
      }
    }
  }

  factory { BlockChairController(get(qualifier = named(SourceQualifier.BLOCK_CHAIR))) }
  factory { EtherScanController(get(qualifier = named(SourceQualifier.ETHER_SCAN))) }
}
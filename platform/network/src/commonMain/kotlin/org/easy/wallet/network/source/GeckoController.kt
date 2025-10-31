package org.easy.wallet.network.source

import io.ktor.client.HttpClient

class GeckoController internal constructor(
  private val httpClient: HttpClient
) {
  suspend fun market() {}
}
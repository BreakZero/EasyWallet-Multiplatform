package org.easy.wallet.network.source

import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import org.easy.wallet.model.News
import org.easy.wallet.network.mapper.toNews
import org.easy.wallet.network.model.dto.BlockChairBaseResponse
import org.easy.wallet.network.model.dto.BlockChairNewsDTO
import org.easy.wallet.network.safeGet

class BlockChairController internal constructor(
  private val httpClient: HttpClient
) {
  suspend fun loadNews(limit: Int, offset: Int): Result<List<News>> {
    val result = httpClient
      .safeGet<BlockChairBaseResponse<List<BlockChairNewsDTO>>>(urlString = "news?q=language(en)") {
        parameter("limit", limit)
        parameter("offset", offset)
      }
    return result.map { it.data.map(BlockChairNewsDTO::toNews) }
  }
}
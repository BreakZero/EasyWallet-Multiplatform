package org.easy.wallet.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.easy.wallet.model.News
import org.easy.wallet.network.source.BlockChairController

internal class NewsPagingSource(
  private val blockChairController: BlockChairController
) : PagingSource<Int, News>() {
  companion object {
    internal const val PAGE_SIZE = 20
  }

  override fun getRefreshKey(state: PagingState<Int, News>): Int? = null

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, News> {
    val offset = params.key ?: 0
    val loadSize = params.loadSize

    val result = blockChairController.loadNews(limit = loadSize, offset = offset)

    return result.fold(
      onFailure = {
        LoadResult.Error(throwable = it)
      },
      onSuccess = { data ->
        LoadResult.Page(
          data = data,
          prevKey = if (offset <= 0) null else offset - loadSize,
          nextKey = if (data.size < loadSize) null else offset + loadSize
        )
      }
    )
  }
}
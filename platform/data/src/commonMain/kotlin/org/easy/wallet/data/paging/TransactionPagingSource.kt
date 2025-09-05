package org.easy.wallet.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.easy.wallet.model.Transfer
import org.easy.wallet.network.source.EtherScanController

class TransactionPagingSource(
  private val ethereumController: EtherScanController
): PagingSource<Int, Transfer>() {
  companion object {
    private const val PAGE_SIZE = 20
  }

  override fun getRefreshKey(state: PagingState<Int, Transfer>): Int? = null

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transfer> {
    val offset = params.key ?: 0
    TODO()
  }
}
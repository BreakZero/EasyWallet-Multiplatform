package org.easy.wallet.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.easy.wallet.model.Address
import org.easy.wallet.model.ChainId
import org.easy.wallet.model.Transfer
import org.easy.wallet.network.source.EtherScanController

class TransactionPagingSource(
  private val ethereumController: EtherScanController,
  private val address: Address,
  private val chainId: ChainId
) : PagingSource<Int, Transfer>() {
  override fun getRefreshKey(state: PagingState<Int, Transfer>): Int? = null

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transfer> {
    val page = params.key ?: 1
    val loadSize = params.loadSize

    val result = ethereumController.listNormalTransfer(
      address = address.value,
      chainId = chainId,
      page = page,
      offset = loadSize
    )
    return result.fold(
      onFailure = { LoadResult.Error(it) },
      onSuccess = { data ->
        LoadResult.Page(
          data = data,
          prevKey = if (page <= 1) null else page - 1,
          nextKey = if (data.isEmpty()) null else page + 1
        )
      }
    )
  }
}
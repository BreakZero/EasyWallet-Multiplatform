package org.easy.wallet.data.interfaces

import androidx.paging.PagingSource
import org.easy.wallet.model.Address
import org.easy.wallet.model.Transfer

interface HistoryService {
  suspend fun getTransfers(
    account: Address,
    cursor: String? = null,
    pageSize: Int = 50
  ): PagingSource<Int, Transfer>
}
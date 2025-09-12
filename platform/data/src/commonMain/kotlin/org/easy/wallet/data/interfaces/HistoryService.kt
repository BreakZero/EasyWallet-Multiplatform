package org.easy.wallet.data.interfaces

import androidx.paging.Pager
import org.easy.wallet.model.Address
import org.easy.wallet.model.Transfer

interface HistoryService {
  fun getTransfers(account: Address, pageSize: Int = 50): Pager<Int, Transfer>
}
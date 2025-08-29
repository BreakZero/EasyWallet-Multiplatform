package org.easy.wallet.feature.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import org.easy.wallet.data.repository.NewsRepository

class NewsViewModel constructor(
  newsRepository: NewsRepository
) : ViewModel() {
  val newsPagingData = newsRepository
    .getNews()
    .distinctUntilChanged()
    .cachedIn(viewModelScope)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(3_000), PagingData.empty())
}
package org.easy.wallet.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Any> PullToRefreshPagingColumn(
  pagingItems: LazyPagingItems<T>,
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(0.dp),
  lazyListState: LazyListState = rememberLazyListState(),
  headerContainer: LazyListScope.() -> Unit = {},
  footerContainer: LazyListScope.() -> Unit = {},
  itemKey: ((index: Int) -> Any)? = {
    pagingItems[it].hashCode()
  },
  verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
  itemContainer: @Composable LazyItemScope.(T) -> Unit
) {
  val pullToRefreshState = rememberPullToRefreshState()
  val isRefreshing by remember {
    derivedStateOf { pagingItems.loadState.refresh is LoadState.Loading }
  }

  PullToRefreshBox(
    modifier = modifier,
    onRefresh = { pagingItems.refresh() },
    state = pullToRefreshState,
    isRefreshing = isRefreshing
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      state = lazyListState,
      contentPadding = contentPadding,
      verticalArrangement = verticalArrangement
    ) {
      headerContainer()
      items(count = pagingItems.itemCount, key = itemKey) {
        itemContainer(pagingItems[it]!!)
      }
      with(pagingItems) {
        when {
          loadState.refresh is LoadState.Error -> {
            item {
              Box(
                modifier = Modifier.fillParentMaxSize(),
                contentAlignment = Alignment.Center
              ) {
                Button(onClick = { refresh() }) {
                  Text(text = "tap to refresh...")
                }
              }
            }
          }

          loadState.append is LoadState.Loading -> {
            item {
              Box(
                modifier = Modifier.fillParentMaxWidth(),
                contentAlignment = Alignment.Center
              ) {
                CircularProgressIndicator()
              }
            }
          }

          loadState.append is LoadState.Error -> {
            item {
              Box(
                modifier = Modifier.fillParentMaxWidth(),
                contentAlignment = Alignment.Center
              ) {
                Button(onClick = ::retry) {
                  Text(text = "load more failed...")
                }
              }
            }
          }
        }
      }
      footerContainer()
    }
  }
}
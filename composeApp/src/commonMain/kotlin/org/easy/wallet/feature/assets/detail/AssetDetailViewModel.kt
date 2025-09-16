package org.easy.wallet.feature.assets.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.easy.wallet.domain.FetchTokenInformationUseCase
import org.easy.wallet.model.TokenId

class AssetDetailViewModel(
  fetchTokenInformationUseCase: FetchTokenInformationUseCase,
  tokenId: TokenId
) : ViewModel() {
  val state = fetchTokenInformationUseCase(tokenId)
    .map {
      AssetDetailUiState(it)
    }.stateIn(viewModelScope, SharingStarted.Lazily, AssetDetailUiState())
}
package org.easy.wallet.feature.assets.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import org.easy.wallet.domain.FetchTokenInformationUseCase
import org.easy.wallet.model.TokenId

class AssetDetailViewModel(
  private val fetchTokenInformationUseCase: FetchTokenInformationUseCase,
  private val tokenId: TokenId
) : ViewModel() {
  val state = flow {
    val holding = fetchTokenInformationUseCase(tokenId)
    emit(AssetDetailUiState(holding))
  }.stateIn(viewModelScope, SharingStarted.Lazily, AssetDetailUiState())
}
package org.easy.wallet.feature.assets.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.easy.wallet.domain.FetchAssetBalanceUseCase
import org.easy.wallet.model.AssetId

class AssetDetailViewModel(
  fetchAssetBalanceUseCase: FetchAssetBalanceUseCase,
  assetId: AssetId
) : ViewModel() {
  val state = fetchAssetBalanceUseCase(assetId)
    .map {
      AssetDetailUiState(assetBalance = it)
    }.stateIn(viewModelScope, SharingStarted.Lazily, AssetDetailUiState())
}

package org.easy.wallet.feature.assets.detail

import androidx.compose.runtime.Stable
import org.easy.wallet.model.AssetBalance

@Stable
data class AssetDetailUiState(
  val assetBalance: AssetBalance? = null
)

package org.easy.wallet.feature.assets.detail

import androidx.compose.runtime.Stable
import org.easy.wallet.model.TokenHolding

@Stable
data class AssetDetailUiState(
  val tokenHolding: TokenHolding? = null
)
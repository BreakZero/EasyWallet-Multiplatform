package org.easy.wallet.feature.wallet.create

import androidx.compose.runtime.Immutable

@Immutable
data class SeedState(
  val mnemonic: List<String> = emptyList()
)
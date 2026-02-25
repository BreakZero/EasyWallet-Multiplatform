package org.easy.wallet.common

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.NavEntry
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal inline fun <reified T : ViewModel> NavEntry<*>.sharedViewModel(
  key: String? = null,
  noinline parameters: (() -> org.koin.core.parameter.ParametersHolder)? = null
): T {
  val viewModelStoreOwner = LocalViewModelStoreOwner.current
    ?: error("No ViewModelStoreOwner found in composition")
  return koinViewModel(viewModelStoreOwner = viewModelStoreOwner, key = key, parameters = parameters)
}

@Composable
internal inline fun <reified T : ViewModel> sharedViewModel(
  key: String? = null,
  noinline parameters: (() -> org.koin.core.parameter.ParametersHolder)? = null
): T {
  val viewModelStoreOwner = LocalViewModelStoreOwner.current
    ?: error("No ViewModelStoreOwner found in composition")
  return koinViewModel(viewModelStoreOwner = viewModelStoreOwner, key = key, parameters = parameters)
}
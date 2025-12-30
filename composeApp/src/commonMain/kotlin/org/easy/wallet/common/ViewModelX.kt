package org.easy.wallet.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
  navController: NavController,
  noinline parameters: (() -> org.koin.core.parameter.ParametersHolder)? = null
): T {
  val navGraphRoute = destination.parent?.route ?: return koinViewModel(parameters = parameters)
  val parentEntry = remember { navController.getBackStackEntry(navGraphRoute) }
  return koinViewModel(viewModelStoreOwner = parentEntry, parameters = parameters)
}
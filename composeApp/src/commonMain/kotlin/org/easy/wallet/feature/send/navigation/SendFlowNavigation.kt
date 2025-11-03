package org.easy.wallet.feature.send.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.easy.wallet.common.ObserveAsEvents
import org.easy.wallet.feature.send.SendFlowEvent
import org.easy.wallet.feature.send.SendFlowViewModel
import org.easy.wallet.feature.send.amount.EnterAmountScreen
import org.easy.wallet.feature.send.recipient.RecipientTypingScreen
import org.easy.wallet.model.TokenId
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data object SendFlowEntryPoint

@Serializable
private data class RecipientAddressRoute(
  val tokenId: String
)

@Serializable
data class EnterAmountRoute(
  val tokenId: String
)

fun NavController.navigateToSendFlow(tokenId: TokenId, navOptions: NavOptions? = null) =
  navigate(route = RecipientAddressRoute(tokenId.value), navOptions)

fun NavController.navigateToEnterAmount(tokenId: TokenId, navOptions: NavOptions? = null) =
  navigate(route = EnterAmountRoute(tokenId.value), navOptions)

fun NavGraphBuilder.sendFlowSection(navController: NavController) {
  navigation<SendFlowEntryPoint>(startDestination = RecipientAddressRoute::class) {
    composable<RecipientAddressRoute> { backStackEntry ->
      val route = backStackEntry.toRoute<RecipientAddressRoute>()
      val tokenId = TokenId(route.tokenId)
      val viewModel: SendFlowViewModel = koinViewModel { parametersOf(tokenId) }
      val state by viewModel.uiState.collectAsStateWithLifecycle()

      ObserveAsEvents(viewModel.event) { event ->
        when (event) {
          is SendFlowEvent.NavigateTo -> {
            when (event.route) {
              "enter_amount" -> navController.navigateToEnterAmount(tokenId)
              else -> Unit
            }
          }
          is SendFlowEvent.OnError -> {}
          SendFlowEvent.Popup -> navController.popBackStack()
        }
      }

      RecipientTypingScreen(
        state = state,
        onAction = viewModel::handleAction
      )
    }

    composable<EnterAmountRoute> { backStackEntry ->
      val route = backStackEntry.toRoute<EnterAmountRoute>()
      val tokenId = TokenId(route.tokenId)
      val viewModel: SendFlowViewModel = koinViewModel { parametersOf(tokenId) }

      ObserveAsEvents(viewModel.event) { event ->
        when (event) {
          is SendFlowEvent.NavigateTo -> navController.navigate(event.route)
          is SendFlowEvent.OnError -> {}
          SendFlowEvent.Popup -> navController.popBackStack()
        }
      }

      val state by viewModel.uiState.collectAsStateWithLifecycle()

      EnterAmountScreen(
        state = state,
        onAction = viewModel::handleAction
      )
    }
  }
}
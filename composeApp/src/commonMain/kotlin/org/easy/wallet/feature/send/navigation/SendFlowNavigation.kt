package org.easy.wallet.feature.send.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.easy.wallet.common.ObserveAsEvents
import org.easy.wallet.common.sharedViewModel
import org.easy.wallet.feature.send.SendFlowEvent
import org.easy.wallet.feature.send.SendFlowViewModel
import org.easy.wallet.feature.send.amount.EnterAmountScreen
import org.easy.wallet.feature.send.recipient.RecipientTypingScreen
import org.easy.wallet.model.TokenId
import org.easy.wallet.navhost.Navigator
import org.koin.core.parameter.parametersOf

@Serializable
internal data class RecipientAddressRoute(
  val tokenId: String
) : NavKey

@Serializable
data class EnterAmountRoute(
  val tokenId: String
) : NavKey

fun Navigator.navigateToSendFlow(tokenId: TokenId) =
  navigate(RecipientAddressRoute(tokenId.value))

fun Navigator.navigateToEnterAmount(tokenId: TokenId) =
  navigate(EnterAmountRoute(tokenId.value))

fun EntryProviderScope<NavKey>.sendFlowSection(navigator: Navigator) {
  entry<RecipientAddressRoute> { key ->
    val tokenId = TokenId(key.tokenId)
    val viewModel: SendFlowViewModel =
      sharedViewModel { parametersOf(tokenId) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
      when (event) {
        is SendFlowEvent.NavigateTo -> {
          when (event.route) {
            "enter_amount" -> navigator.navigateToEnterAmount(tokenId)
            else -> Unit
          }
        }

        is SendFlowEvent.OnError -> {}
        SendFlowEvent.Popup -> navigator.goBack()
      }
    }

    RecipientTypingScreen(
      state = state,
      onAction = viewModel::handleAction
    )
  }

  entry<EnterAmountRoute> { key ->
    val tokenId = TokenId(key.tokenId)
    val viewModel: SendFlowViewModel =
      sharedViewModel { parametersOf(tokenId) }

    ObserveAsEvents(viewModel.event) { event ->
      when (event) {
        is SendFlowEvent.NavigateTo -> {
          // Handle string-based navigation if needed
        }
        is SendFlowEvent.OnError -> {}
        SendFlowEvent.Popup -> navigator.goBack()
      }
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    EnterAmountScreen(
      state = state,
      onAction = viewModel::handleAction
    )
  }
}
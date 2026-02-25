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
import org.easy.wallet.feature.send.result.TransactionResultScreen
import org.easy.wallet.feature.send.review.ReviewTransactionScreen
import org.easy.wallet.model.TokenId
import org.easy.wallet.navhost.Navigator
import org.koin.core.parameter.parametersOf

@Serializable
internal data class RecipientAddressRoute(
  val tokenId: String
) : NavKey

@Serializable
internal data class EnterAmountRoute(
  val tokenId: String
) : NavKey

@Serializable
internal data class ReviewTransactionRoute(
  val tokenId: String
) : NavKey

@Serializable
internal data class TransactionResultRoute(
  val tokenId: String
) : NavKey

fun Navigator.navigateToSendFlow(tokenId: TokenId) = navigate(RecipientAddressRoute(tokenId.value))

fun EntryProviderScope<NavKey>.sendFlowSection(navigator: Navigator) {
  entry<RecipientAddressRoute> { key ->
    val tokenId = TokenId(key.tokenId)
    val viewModel: SendFlowViewModel =
      sharedViewModel(key = key.tokenId) { parametersOf(tokenId) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
      when (event) {
        SendFlowEvent.GoBack -> navigator.goBack()
        SendFlowEvent.NavigateToEnterAmount ->
          navigator.navigate(EnterAmountRoute(key.tokenId))
        else -> Unit
      }
    }

    RecipientTypingScreen(state = state, onAction = viewModel::handleAction)
  }

  entry<EnterAmountRoute> { key ->
    val tokenId = TokenId(key.tokenId)
    val viewModel: SendFlowViewModel =
      sharedViewModel(key = key.tokenId) { parametersOf(tokenId) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
      when (event) {
        SendFlowEvent.GoBack -> navigator.goBack()
        SendFlowEvent.NavigateToReview ->
          navigator.navigate(ReviewTransactionRoute(key.tokenId))
        else -> Unit
      }
    }

    EnterAmountScreen(state = state, onAction = viewModel::handleAction)
  }

  entry<ReviewTransactionRoute> { key ->
    val tokenId = TokenId(key.tokenId)
    val viewModel: SendFlowViewModel =
      sharedViewModel(key = key.tokenId) { parametersOf(tokenId) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
      when (event) {
        SendFlowEvent.GoBack -> navigator.goBack()
        SendFlowEvent.NavigateToResult ->
          navigator.navigate(TransactionResultRoute(key.tokenId))
        else -> Unit
      }
    }

    ReviewTransactionScreen(state = state, onAction = viewModel::handleAction)
  }

  entry<TransactionResultRoute> { key ->
    val tokenId = TokenId(key.tokenId)
    val viewModel: SendFlowViewModel =
      sharedViewModel(key = key.tokenId) { parametersOf(tokenId) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
      when (event) {
        SendFlowEvent.GoBack -> navigator.goBack()
        SendFlowEvent.NavigateToHome ->
          navigator.popBackTo(
            predicate = { it is RecipientAddressRoute },
            inclusive = true
          )
        else -> Unit
      }
    }

    TransactionResultScreen(state = state, onAction = viewModel::handleAction)
  }
}
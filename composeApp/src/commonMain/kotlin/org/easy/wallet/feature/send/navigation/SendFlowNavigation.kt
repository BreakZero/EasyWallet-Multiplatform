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
import org.easy.wallet.model.AssetId
import org.easy.wallet.navhost.Navigator
import org.koin.core.parameter.parametersOf

@Serializable
internal data class RecipientAddressRoute(
  val assetId: String
) : NavKey

@Serializable
internal data class EnterAmountRoute(
  val assetId: String
) : NavKey

@Serializable
internal data class ReviewTransactionRoute(
  val assetId: String
) : NavKey

@Serializable
internal data class TransactionResultRoute(
  val assetId: String
) : NavKey

fun Navigator.navigateToSendFlow(assetId: AssetId) = navigate(RecipientAddressRoute(assetId.value))

fun EntryProviderScope<NavKey>.sendFlowSection(navigator: Navigator) {
  entry<RecipientAddressRoute> { key ->
    val assetId = AssetId(key.assetId)
    val viewModel: SendFlowViewModel =
      sharedViewModel(key = key.assetId) { parametersOf(assetId) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
      when (event) {
        SendFlowEvent.GoBack -> navigator.goBack()
        SendFlowEvent.NavigateToEnterAmount ->
          navigator.navigate(EnterAmountRoute(key.assetId))
        else -> Unit
      }
    }

    RecipientTypingScreen(state = state, onAction = viewModel::handleAction)
  }

  entry<EnterAmountRoute> { key ->
    val assetId = AssetId(key.assetId)
    val viewModel: SendFlowViewModel =
      sharedViewModel(key = key.assetId) { parametersOf(assetId) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
      when (event) {
        SendFlowEvent.GoBack -> navigator.goBack()
        SendFlowEvent.NavigateToReview ->
          navigator.navigate(ReviewTransactionRoute(key.assetId))
        else -> Unit
      }
    }

    EnterAmountScreen(state = state, onAction = viewModel::handleAction)
  }

  entry<ReviewTransactionRoute> { key ->
    val assetId = AssetId(key.assetId)
    val viewModel: SendFlowViewModel =
      sharedViewModel(key = key.assetId) { parametersOf(assetId) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
      when (event) {
        SendFlowEvent.GoBack -> navigator.goBack()
        SendFlowEvent.NavigateToResult ->
          navigator.navigate(TransactionResultRoute(key.assetId))
        else -> Unit
      }
    }

    ReviewTransactionScreen(state = state, onAction = viewModel::handleAction)
  }

  entry<TransactionResultRoute> { key ->
    val assetId = AssetId(key.assetId)
    val viewModel: SendFlowViewModel =
      sharedViewModel(key = key.assetId) { parametersOf(assetId) }
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

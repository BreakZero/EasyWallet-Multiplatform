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
import org.easy.wallet.feature.send.SendFlowViewModel
import org.easy.wallet.feature.send.amount.EnterAmountScreen
import org.easy.wallet.feature.send.recipient.RecipientTypingScreen
import org.easy.wallet.model.TokenId
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Entry point route for the send flow navigation graph.
 * Contains the token ID to identify which token is being sent.
 */
@Serializable
data object SendFlowEntryPoint

/**
 * Route for the recipient address entry screen.
 * First step in the send flow where users enter or scan recipient address.
 */
@Serializable
private data class RecipientAddressRoute(
  val tokenId: String
)

/**
 * Route for the amount entry screen.
 * Second step in the send flow where users specify the amount to send
 * and review transaction details.
 */
@Serializable
data class EnterAmountRoute(
  val tokenId: String
)

// Navigation functions

/**
 * Navigates to the send flow for a specific token.
 * This starts the send flow from the recipient address entry screen.
 */
fun NavController.navigateToSendFlow(tokenId: TokenId, navOptions: NavOptions? = null) =
  navigate(route = RecipientAddressRoute(tokenId.value), navOptions)

/**
 * Internal navigation function to move from recipient entry to amount entry.
 * Called when user confirms a valid recipient address.
 */
fun NavController.navigateToEnterAmount(tokenId: TokenId, navOptions: NavOptions? = null) =
  navigate(route = EnterAmountRoute(tokenId.value), navOptions)

/**
 * Defines the send flow navigation graph with two main screens:
 * 1. RecipientAddressRoute - for entering recipient wallet address
 * 2. EnterAmountRoute - for specifying amount and confirming transaction
 *
 * @param navController The navigation controller for handling screen transitions
 */
fun NavGraphBuilder.sendFlowSection(navController: NavController) {
  navigation<SendFlowEntryPoint>(startDestination = RecipientAddressRoute::class) {
    composable<RecipientAddressRoute> { backStackEntry ->
      val route = backStackEntry.toRoute<RecipientAddressRoute>()
      val tokenId = TokenId(route.tokenId)
      val viewModel: SendFlowViewModel = koinViewModel { parametersOf(tokenId) }
      val state by viewModel.uiState.collectAsStateWithLifecycle()

      RecipientTypingScreen(
        state = state,
        onAction = viewModel::handleAction
      )
    }

    // Step 2: Amount Entry and Transaction Review
    composable<EnterAmountRoute> { backStackEntry ->
      val route = backStackEntry.toRoute<EnterAmountRoute>()
      val tokenId = TokenId(route.tokenId)
      val viewModel: SendFlowViewModel = koinViewModel { parametersOf(tokenId) }

      val state by viewModel.uiState.collectAsStateWithLifecycle()

      EnterAmountScreen(
        state = state,
        onAction = viewModel::handleAction
      )
    }
  }
}
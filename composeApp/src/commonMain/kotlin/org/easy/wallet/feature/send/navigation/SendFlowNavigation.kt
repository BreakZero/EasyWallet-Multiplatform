package org.easy.wallet.feature.send.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
data object SendFlowEntryPoint

@Serializable
private data object RecipientAddressRoute

@Serializable
private data object EnterAmountRoute

fun NavGraphBuilder.sendFlowSection() {
  navigation<SendFlowEntryPoint>(startDestination = RecipientAddressRoute) {
    composable<RecipientAddressRoute> {

    }
    composable<EnterAmountRoute> {  }
  }
}
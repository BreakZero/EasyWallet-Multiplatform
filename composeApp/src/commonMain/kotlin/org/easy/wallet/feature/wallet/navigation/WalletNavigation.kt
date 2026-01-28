package org.easy.wallet.feature.wallet.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.easy.wallet.feature.wallet.WalletOptionScreen
import org.easy.wallet.feature.wallet.create.GenerateSeedScreen
import org.easy.wallet.feature.wallet.passcode.CreatePassCodeScreen
import org.easy.wallet.feature.wallet.restore.WalletRestoreScreen
import org.easy.wallet.navhost.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object WalletOptionRoute : NavKey

@Serializable
data class SetPassCodeRoute(
  val isRestore: Boolean
) : NavKey

@Serializable
data class GenerateSeedRoute(
  val passcode: String
) : NavKey

@Serializable
data class WalletRestoreRoute(
  val passcode: String
) : NavKey

fun EntryProviderScope<NavKey>.attachWalletGraph(navigator: Navigator) {
  entry<WalletOptionRoute> {
    WalletOptionScreen(
      onCreateWallet = { navigator.navigate(SetPassCodeRoute(isRestore = false)) },
      onRestoreWallet = { navigator.navigate(SetPassCodeRoute(isRestore = true)) },
      popBackStack = navigator::goBack
    )
  }

  entry<SetPassCodeRoute> { key ->
    CreatePassCodeScreen(
      popBackStack = navigator::goBack,
      toNext = { passcode ->
        // Remove current route and navigate to next
        navigator.goBack()
        navigator.navigate(
          if (key.isRestore) WalletRestoreRoute(passcode) else GenerateSeedRoute(passcode)
        )
      }
    )
  }

  entry<GenerateSeedRoute> { key ->
    GenerateSeedScreen(
      passcode = key.passcode,
      popBackStack = navigator::goBack,
      onComplete = { navigator.goBack() }
    )
  }

  entry<WalletRestoreRoute> { key ->
    WalletRestoreScreen(
      passcode = key.passcode,
      popBackStack = navigator::goBack,
      onComplete = { navigator.goBack() }
    )
  }
}
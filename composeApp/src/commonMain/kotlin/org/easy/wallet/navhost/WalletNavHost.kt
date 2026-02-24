package org.easy.wallet.navhost

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import org.easy.wallet.feature.account.navigation.accountSection
import org.easy.wallet.feature.apps.navigation.appsSection
import org.easy.wallet.feature.assets.navigation.assetsSection
import org.easy.wallet.feature.news.navigation.newsSection
import org.easy.wallet.feature.send.navigation.sendFlowSection
import org.easy.wallet.feature.wallet.navigation.WalletOptionRoute
import org.easy.wallet.feature.wallet.navigation.attachWalletGraph

@Composable
fun WalletNavHost(
  modifier: Modifier = Modifier,
  navigationState: NavigationState,
  navigator: Navigator,
) {
  val entryProvider = entryProvider<NavKey> {
    assetsSection(navigator)
    newsSection()
    appsSection { }
    accountSection(
      onEvent = {
        navigator.navigate(WalletOptionRoute)
      },
      accountNestedGraph = { }
    )
    sendFlowSection(navigator)
    attachWalletGraph(navigator)
  }

  NavDisplay(
    modifier = modifier,
    entries = navigationState.toEntries(entryProvider),
    onBack = { navigator.goBack() }
  )
}
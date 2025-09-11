package org.easy.wallet.navhost

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import org.easy.wallet.feature.account.navigation.accountSection
import org.easy.wallet.feature.apps.navigation.appsSection
import org.easy.wallet.feature.assets.navigation.assetsSection
import org.easy.wallet.feature.news.navigation.newsSection
import org.easy.wallet.feature.wallet.navigation.WalletOptionRoute
import org.easy.wallet.feature.wallet.navigation.attachWalletGraph

@Composable
fun WalletNavHost(
  modifier: Modifier = Modifier,
  navController: NavHostController,
  startDestination: Any,
) {
  NavHost(
    modifier = modifier,
    navController = navController,
    startDestination = startDestination
  ) {
    assetsSection(navController) { }
    newsSection()
    appsSection { }
    accountSection(
      onEvent = {
        navController.navigate(WalletOptionRoute)
      },
      accountNestedGraph = {
        attachWalletGraph(
          navController = navController
        )
      }
    )
  }
}
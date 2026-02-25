package org.easy.wallet.navhost

import androidx.navigation3.runtime.NavKey
import easywallet.composeapp.generated.resources.Res
import easywallet.composeapp.generated.resources.ic_tab_account
import easywallet.composeapp.generated.resources.ic_tab_home
import easywallet.composeapp.generated.resources.ic_tab_news
import easywallet.composeapp.generated.resources.tab_account
import easywallet.composeapp.generated.resources.tab_assets
import easywallet.composeapp.generated.resources.tab_news
import org.easy.wallet.feature.account.navigation.AccountRoute
import org.easy.wallet.feature.assets.navigation.AssetsRoute
import org.easy.wallet.feature.news.navigation.NewsRoute
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class TopLevelDestination(
  val selectedIcon: DrawableResource,
  val unselectedIcon: DrawableResource,
  val titleTextId: StringResource,
  val route: NavKey,
) {
  Assets(
    selectedIcon = Res.drawable.ic_tab_home,
    unselectedIcon = Res.drawable.ic_tab_home,
    titleTextId = Res.string.tab_assets,
    route = AssetsRoute
  ),
  News(
    selectedIcon = Res.drawable.ic_tab_news,
    unselectedIcon = Res.drawable.ic_tab_news,
    titleTextId = Res.string.tab_news,
    route = NewsRoute
  ),

//  DApps(
//    selectedIcon = Res.drawable.compose_multiplatform,
//    unselectedIcon = Res.drawable.compose_multiplatform,
//    titleTextId = Res.string.tab_dapp,
//    route = DAppsRoute
//  ),
  Account(
    selectedIcon = Res.drawable.ic_tab_account,
    unselectedIcon = Res.drawable.ic_tab_account,
    titleTextId = Res.string.tab_account,
    route = AccountRoute
  ),
}
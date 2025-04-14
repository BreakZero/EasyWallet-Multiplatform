package org.easy.wallet.navhost

import easywallet.composeapp.generated.resources.Res
import easywallet.composeapp.generated.resources.compose_multiplatform
import easywallet.composeapp.generated.resources.tab_account
import easywallet.composeapp.generated.resources.tab_assets
import easywallet.composeapp.generated.resources.tab_news
import org.easy.wallet.feature.account.navigation.AccountRoute
import org.easy.wallet.feature.assets.navigation.AssetsBaseRoute
import org.easy.wallet.feature.assets.navigation.AssetsRoute
import org.easy.wallet.feature.news.navigation.NewsRoute
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import kotlin.reflect.KClass

enum class TopLevelDestination(
  val selectedIcon: DrawableResource,
  val unselectedIcon: DrawableResource,
  val titleTextId: StringResource,
  val route: KClass<*>,
  val baseRoute: KClass<*> = route,
) {
  Assets(
    selectedIcon = Res.drawable.compose_multiplatform,
    unselectedIcon = Res.drawable.compose_multiplatform,
    titleTextId = Res.string.tab_assets,
    route = AssetsRoute::class,
    baseRoute = AssetsBaseRoute::class
  ),
  News(
    selectedIcon = Res.drawable.compose_multiplatform,
    unselectedIcon = Res.drawable.compose_multiplatform,
    titleTextId = Res.string.tab_news,
    route = NewsRoute::class
  ),

//  DApps(
//    selectedIcon = Res.drawable.compose_multiplatform,
//    unselectedIcon = Res.drawable.compose_multiplatform,
//    titleTextId = Res.string.tab_dapp,
//    route = DAppsRoute::class
//  ),
  Account(
    selectedIcon = Res.drawable.compose_multiplatform,
    unselectedIcon = Res.drawable.compose_multiplatform,
    titleTextId = Res.string.tab_account,
    route = AccountRoute::class
  ),
}
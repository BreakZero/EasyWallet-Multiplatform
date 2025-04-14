package org.easy.wallet.feature.news.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import org.easy.wallet.feature.news.NewsScreen

@Serializable
data object NewsBaseRoute

@Serializable
data object NewsRoute

fun NavController.navigateToNews(navOptions: NavOptions) = navigate(route = NewsRoute, navOptions)

fun NavGraphBuilder.newsSection(newsNestedGraph: NavGraphBuilder.() -> Unit,) {
  navigation<NewsBaseRoute>(startDestination = NewsRoute) {
    composable<NewsRoute> {
      NewsScreen()
    }
    newsNestedGraph()
  }
}
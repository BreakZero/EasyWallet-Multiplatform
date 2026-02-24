package org.easy.wallet.feature.news.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.easy.wallet.feature.news.NewsScreen
import org.easy.wallet.navhost.Navigator

@Serializable
data object NewsRoute : NavKey

fun Navigator.navigateToNews() = navigate(NewsRoute)

fun EntryProviderScope<NavKey>.newsSection() {
  entry<NewsRoute> {
    NewsScreen()
  }
}
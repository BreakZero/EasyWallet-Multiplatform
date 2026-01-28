package org.easy.wallet.feature.account.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.easy.wallet.feature.account.AccountScreen
import org.easy.wallet.navhost.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object AccountRoute : NavKey

fun Navigator.navigateToAccount() = navigate(AccountRoute)

fun EntryProviderScope<NavKey>.accountSection(
  accountNestedGraph: EntryProviderScope<NavKey>.() -> Unit,
  onEvent: () -> Unit
) {
  entry<AccountRoute> {
    AccountScreen(navigateToWallet = onEvent)
  }
  accountNestedGraph()
}
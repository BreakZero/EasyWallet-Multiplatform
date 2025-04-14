package org.easy.wallet.di

import org.easy.wallet.feature.account.AccountViewModel
import org.easy.wallet.feature.assets.AssetsViewModel
import org.easy.wallet.feature.news.NewsViewModel
import org.easy.wallet.feature.wallet.create.GenerateSeedViewModel
import org.easy.wallet.feature.wallet.create.SetPasswordViewModel
import org.easy.wallet.feature.wallet.password.CreatePasswordViewModel
import org.easy.wallet.feature.wallet.restore.WalletRestoreViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
  viewModelOf(::AccountViewModel)
  viewModelOf(::AssetsViewModel)
  viewModelOf(::NewsViewModel)
  viewModelOf(::SetPasswordViewModel)
  viewModelOf(::GenerateSeedViewModel)
  viewModelOf(::WalletRestoreViewModel)
  viewModelOf(::CreatePasswordViewModel)
}
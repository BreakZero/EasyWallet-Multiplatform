package org.easy.wallet

import android.app.Application
import org.easy.wallet.common.ClipboardManager
import org.easy.wallet.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class WalletApplication : Application() {
  init {
    System.loadLibrary("TrustWalletCore")
  }

  override fun onCreate() {
    super.onCreate()
    initKoin {
      androidLogger()
      androidContext(this@WalletApplication)
    }
    ClipboardManager.init(this.applicationContext)
  }
}
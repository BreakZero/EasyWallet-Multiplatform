package org.easy.wallet.common

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual object WebManager {
  actual fun openInBrowser(url: String, completionHandler: (Boolean) -> Unit) {
    NSURL.URLWithString(url)?.let {
      UIApplication.sharedApplication.openURL(
        url = it,
        options = emptyMap<Any?, Any>(),
        completionHandler = completionHandler
      )
    }
  }
}
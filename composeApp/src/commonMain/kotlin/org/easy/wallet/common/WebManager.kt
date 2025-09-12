package org.easy.wallet.common

expect object WebManager {
  fun openInBrowser(
    url: String,
    completionHandler: (Boolean) -> Unit = {}
  )
}
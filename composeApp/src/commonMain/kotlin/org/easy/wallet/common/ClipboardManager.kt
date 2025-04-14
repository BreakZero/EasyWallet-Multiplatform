package org.easy.wallet.common

expect object ClipboardManager {
  fun copyToClipboard(text: String)

  fun getClipboardText(): String?
}
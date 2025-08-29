package org.easy.wallet.common

import platform.UIKit.UIPasteboard

actual object ClipboardManager {
  actual fun copyToClipboard(text: String) {
    UIPasteboard.generalPasteboard.string = text
  }

  actual fun getClipboardText(): String? = UIPasteboard.generalPasteboard.string
}
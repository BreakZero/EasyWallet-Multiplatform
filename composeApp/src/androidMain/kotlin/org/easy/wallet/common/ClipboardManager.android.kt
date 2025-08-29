package org.easy.wallet.common

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE

actual object ClipboardManager {
  private lateinit var appContext: Context

  fun init(context: Context) {
    appContext = context.applicationContext
  }

  actual fun copyToClipboard(text: String) {
    val clipboard = appContext.getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard?.setPrimaryClip(clip)
  }

  actual fun getClipboardText(): String? {
    val clipboard = appContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = clipboard.primaryClip
    return if (clipData != null && clipData.itemCount > 0) {
      clipData.getItemAt(0).coerceToText(appContext).toString()
    } else {
      null
    }
  }
}
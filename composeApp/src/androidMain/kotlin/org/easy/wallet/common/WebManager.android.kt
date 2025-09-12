package org.easy.wallet.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

actual object WebManager {
  private lateinit var appContext: Context

  fun init(context: Context) {
    appContext = context.applicationContext
  }

  actual fun openInBrowser(url: String, completionHandler: (Boolean) -> Unit) {
    val result = try {
      appContext.startActivity(
        Intent(
          Intent.ACTION_VIEW, url.toUri()
        ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
      true
    } catch (_: ActivityNotFoundException) {
      false
    }
    completionHandler(result)
  }
}
package org.easy.wallet.database

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory(private val context: Context) {
  actual fun createDriver(): app.cash.sqldelight.db.SqlDriver {
    return AndroidSqliteDriver(EasyWalletDatabase.Schema, context, "easywallet.db")
  }
}
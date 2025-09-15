package org.easy.wallet.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory(
  private val context: Context
) {
  actual fun createDriver(): app.cash.sqldelight.db.SqlDriver = AndroidSqliteDriver(
    EasyWalletDatabase.Schema,
    context,
    "easywallet.db",
    callback = object : AndroidSqliteDriver.Callback(EasyWalletDatabase.Schema) {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        val seedDriver = AndroidSqliteDriver(db)
        val database = EasyWalletDatabase(seedDriver)
        initLocalDatabase(database)
      }
    }
  )
}
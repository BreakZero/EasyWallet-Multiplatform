package org.easy.wallet.database

import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
  actual fun createDriver(): app.cash.sqldelight.db.SqlDriver {
    return NativeSqliteDriver(EasyWalletDatabase.Schema, "easywallet.db")
  }
}
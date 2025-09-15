package org.easy.wallet.database

import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
  actual fun createDriver(): app.cash.sqldelight.db.SqlDriver {
    val driver = NativeSqliteDriver(EasyWalletDatabase.Schema, "easywallet.db")
    val database = EasyWalletDatabase(driver)

    initLocalDatabase(database)

    return driver
  }
}
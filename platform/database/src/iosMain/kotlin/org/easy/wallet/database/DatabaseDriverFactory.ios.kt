package org.easy.wallet.database

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration

actual class DatabaseDriverFactory {
  actual fun createDriver(): app.cash.sqldelight.db.SqlDriver {
    val configuration = DatabaseConfiguration(
      name = "easywallet.db",
      version = EasyWalletDatabase.Schema.version.toInt(),
      create = { conn ->
        wrapConnection(conn) { driver ->
          EasyWalletDatabase.Schema.create(driver)
          val db = EasyWalletDatabase(driver)
          init(db)
        }
      },
      upgrade = { conn, oldVersion, newVersion ->
        wrapConnection(conn) { driver ->
          EasyWalletDatabase.Schema.migrate(
            driver,
            oldVersion.toLong(),
            newVersion.toLong()
          )
        }
      }
    )

    return NativeSqliteDriver(configuration = configuration)
  }
}
package org.easy.wallet.database

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
  fun createDriver(): SqlDriver
}
package org.easy.wallet.database.di

import org.easy.wallet.database.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val databaseModule: Module = module {
  single { DatabaseDriverFactory() }
}
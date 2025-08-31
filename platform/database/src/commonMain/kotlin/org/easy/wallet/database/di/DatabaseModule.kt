package org.easy.wallet.database.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val databaseModule: Module

val databaseModules: Module
  get() = module { includes(databaseModule) }
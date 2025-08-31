@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package org.easy.wallet.data.repository

import org.easy.wallet.database.DatabaseDriverFactory
import org.easy.wallet.database.EasyWalletDatabase
import org.easy.wallet.datastore.KeyStorePort
import org.easy.wallet.model.WalletAccount
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AccountRepositoryImpl internal constructor(
  driverFactory: DatabaseDriverFactory,
  private val keyStorePort: KeyStorePort
) {
  private val database = EasyWalletDatabase(driverFactory.createDriver())
  private val accountsQueries = database.accountsQueries

  suspend fun listAccounts(): List<WalletAccount> =
    accountsQueries.selectAllAccounts().executeAsList().map {
      WalletAccount(
        id = it.id,
        name = it.name,
        createdAt = it.createdAt,
        alias = it.alias
      )
    }

  suspend fun create(name: String, mnemonic: String) {
    val id = Uuid.random().toString()
    val alias = "acc_$id"
    keyStorePort.store(alias, mnemonic.encodeToByteArray())

    accountsQueries.insertAccount(
      id = id,
      name = name,
      createdAt = Clock.System.now().toEpochMilliseconds(),
      alias = alias
    )
  }

  suspend fun deleteAccount(id: String) = accountsQueries.deleteAccountById(id)
}
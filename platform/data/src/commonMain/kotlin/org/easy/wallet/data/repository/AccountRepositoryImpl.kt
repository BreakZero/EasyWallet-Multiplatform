@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package org.easy.wallet.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

  suspend fun listAccounts(): List<WalletAccount> = accountsQueries.selectAllAccounts().executeAsList().map {
    val alias = it.alias
    val mnemonic = keyStorePort.load(alias).decodeToString()
    WalletAccount(
      id = it.id,
      name = it.name,
      mnemonic = mnemonic
    )
  }

  fun getCurrentAccount(): Flow<WalletAccount?> = accountsQueries
    .firstAccount()
    .asFlow()
    .mapToOneOrNull(Dispatchers.IO)
    .map { account ->
      account?.let {
        val alias = it.alias
        val mnemonic = keyStorePort.load(alias).decodeToString()
        WalletAccount(
          id = it.id,
          name = it.name,
          mnemonic = mnemonic
        )
      }
    }

  suspend fun create(name: String, mnemonic: String): WalletAccount {
    val id = Uuid.random().toString()
    val alias = "acc_$id"
    keyStorePort.store(alias, mnemonic.encodeToByteArray())
    val createAt = Clock.System.now().toEpochMilliseconds()
    accountsQueries.insertAccount(
      id = id,
      name = name,
      createdAt = createAt,
      alias = alias
    )
    return WalletAccount(
      id = id,
      name = name,
      mnemonic = mnemonic
    )
  }

  suspend fun deleteAccount(id: String) = accountsQueries.deleteAccountById(id)
}
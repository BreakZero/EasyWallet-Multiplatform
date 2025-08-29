package org.easy.wallet.datastore

actual class PasswordStorage {
  actual fun save(
    service: String,
    account: String,
    password: String
  ): Boolean {
    TODO("Not yet implemented")
  }

  actual fun retrieve(service: String, account: String): String? {
    TODO("Not yet implemented")
  }

  actual fun delete(service: String, account: String): Boolean {
    TODO("Not yet implemented")
  }
}
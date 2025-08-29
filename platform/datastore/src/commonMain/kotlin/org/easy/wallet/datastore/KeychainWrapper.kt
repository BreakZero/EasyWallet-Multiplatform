package org.easy.wallet.datastore

expect class PasswordStorage {
  fun save(service: String, account: String, password: String): Boolean
  fun retrieve(service: String, account: String): String?
  fun delete(service: String, account: String): Boolean
}
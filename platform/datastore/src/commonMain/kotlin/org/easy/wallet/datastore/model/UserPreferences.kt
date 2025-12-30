package org.easy.wallet.datastore.model

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
  val language: String = "system",
  val passcode: String = "",
  val debugMode: Boolean = false,
)

val DefaultPreferences = UserPreferences()
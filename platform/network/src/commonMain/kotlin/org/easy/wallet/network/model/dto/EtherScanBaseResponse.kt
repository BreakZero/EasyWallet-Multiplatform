package org.easy.wallet.network.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class EtherScanBaseResponse<T>(
  @SerialName("message")
  val message: String,
  @SerialName("result")
  val result: T,
  @SerialName("status")
  val status: String
)
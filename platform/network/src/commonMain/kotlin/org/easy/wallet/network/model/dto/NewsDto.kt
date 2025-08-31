package org.easy.wallet.network.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BlockChairNewsDto(
  @SerialName("description")
  val description: String,
  @SerialName("file")
  val fileName: String,
  @SerialName("hash")
  val hash: String,
  @SerialName("language")
  val language: String,
  @SerialName("link")
  val link: String,
  @SerialName("permalink")
  val permalink: String,
  @SerialName("source")
  val source: String,
  @SerialName("tags")
  val tags: String,
  @SerialName("time")
  val time: String,
  @SerialName("title")
  val title: String
)
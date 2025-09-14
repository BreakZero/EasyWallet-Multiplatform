@file:OptIn(ExperimentalForeignApi::class)

package org.easy.wallet.datastore.common

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.data
import platform.Foundation.dataWithBytes
import platform.posix.memcpy

internal fun ByteArray.toNSData(): NSData = if (isEmpty()) {
  NSData.data()
} else {
  usePinned { NSData.dataWithBytes(it.addressOf(0), size.convert()) }
}

internal fun NSData.toByteArray(): ByteArray {
  val len = length.toInt()
  if (len == 0) return ByteArray(0)
  val out = ByteArray(len)
  out.usePinned { memcpy(it.addressOf(0), bytes, length) }
  return out
}
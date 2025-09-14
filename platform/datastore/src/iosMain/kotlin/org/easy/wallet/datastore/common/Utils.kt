@file:OptIn(ExperimentalForeignApi::class)

package org.easy.wallet.datastore.common

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.Security.SecRandomCopyBytes
import platform.Security.errSecSuccess
import platform.Security.kSecRandomDefault
import platform.darwin.OSStatus

internal fun secureRandomBytes(n: Int): ByteArray {
  val bytes = ByteArray(n)
  bytes.usePinned { pinned ->
    val rc = SecRandomCopyBytes(kSecRandomDefault, n.convert(), pinned.addressOf(0))
    if (rc != errSecSuccess) error("Secure random failed: $rc")
  }
  return bytes
}

internal fun checkStatus(status: OSStatus, msg: String) {
  if (status != errSecSuccess) error("$msg: $status")
}
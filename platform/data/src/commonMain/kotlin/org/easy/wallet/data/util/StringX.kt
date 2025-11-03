package org.easy.wallet.data.util

import okio.ByteString
import okio.ByteString.Companion.decodeHex

fun String.asHex(): ByteString = this.removePrefix("0x").decodeHex()
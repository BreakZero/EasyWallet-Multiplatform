package org.easy.wallet.data.util

import okio.ByteString
import okio.ByteString.Companion.decodeHex

fun String.clearHexString(): ByteString = this.removePrefix("0x").decodeHex()
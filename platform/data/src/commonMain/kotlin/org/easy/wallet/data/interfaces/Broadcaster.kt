package org.easy.wallet.data.interfaces

import com.trustwallet.core.CoinType
import org.easy.wallet.model.UnsignedTx

interface Broadcaster {
  suspend fun signAndBroadcast(unsigned: UnsignedTx, coinType: CoinType): String /* txHash */
}
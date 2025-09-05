package org.easy.wallet.network.mapper

import org.easy.wallet.model.Address
import org.easy.wallet.model.ChainId
import org.easy.wallet.model.TokenId
import org.easy.wallet.model.Transfer
import org.easy.wallet.network.model.dto.EvmTransactionDTO

//internal fun EvmTransactionDTO.toTransfer(tokenId: TokenId, chainId: ChainId): Transfer = Transfer(
//  txHash = this.hash,
//  chainId = chainId,
//  timestamp = this.timeStamp,
//  from = Address(this.from),
//  to = Address(this.to),
//  tokenId = tokenId,
//  amount = this.value,
//  feePaid = this.gas,
//  status = this.confirmations
//)
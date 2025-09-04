package org.easy.wallet.data.interfaces

import org.easy.wallet.model.ChainId
import org.easy.wallet.model.TokenStandard

interface IChainAdapter :
  BalanceService,
  FeeService,
  TransactionBuilder,
  Broadcaster,
  HistoryService {
  val chainId: ChainId
  val supportedStandards: Set<TokenStandard>
}
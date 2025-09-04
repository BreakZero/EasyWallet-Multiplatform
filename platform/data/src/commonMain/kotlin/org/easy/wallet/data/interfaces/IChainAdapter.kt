package org.easy.wallet.data.interfaces

import org.easy.wallet.model.TokenStandard

interface IChainAdapter :
  BalanceService,
  FeeService,
  TransactionBuilder,
  Broadcaster,
  HistoryService {
  val supportedStandards: Set<TokenStandard>
}
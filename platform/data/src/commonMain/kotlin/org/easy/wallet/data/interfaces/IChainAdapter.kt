package org.easy.wallet.data.interfaces

import org.easy.wallet.model.AssetType
import org.easy.wallet.model.ChainId

interface IChainAdapter :
  BalanceService,
  FeeService,
  TransactionBuilder,
  Broadcaster,
  HistoryService {
  val chainId: ChainId
  val supportedAssetTypes: Set<AssetType>
}

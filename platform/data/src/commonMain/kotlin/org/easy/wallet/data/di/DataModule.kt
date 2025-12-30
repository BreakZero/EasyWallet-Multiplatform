package org.easy.wallet.data.di

import org.easy.wallet.data.adapter.BitcoinAdapter
import org.easy.wallet.data.adapter.EvmAdapter
import org.easy.wallet.data.adapter.SolanaAdapter
import org.easy.wallet.data.chain.AdapterRegistry
import org.easy.wallet.data.chain.ChainContextManager
import org.easy.wallet.data.dapp.Web3InjectionService
import org.easy.wallet.data.interfaces.BalanceService
import org.easy.wallet.data.interfaces.Broadcaster
import org.easy.wallet.data.interfaces.FeeService
import org.easy.wallet.data.interfaces.HistoryService
import org.easy.wallet.data.interfaces.TransactionBuilder
import org.easy.wallet.data.paging.NewsPagingSource
import org.easy.wallet.data.repository.AccountRepositoryImpl
import org.easy.wallet.data.repository.NewsRepository
import org.easy.wallet.data.repository.NewsRepositoryImpl
import org.easy.wallet.data.repository.TokenRepository
import org.easy.wallet.data.repository.TokenRepositoryImpl
import org.easy.wallet.data.transaction.TransactionService
import org.easy.wallet.database.di.databaseModules
import org.easy.wallet.datastore.di.storeModules
import org.easy.wallet.model.ChainId
import org.easy.wallet.network.di.networkModule
import org.koin.core.parameter.parametersOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

val dataModule = module {

  includes(networkModule, storeModules, databaseModules)

  single { NewsRepositoryImpl(get()) } bind NewsRepository::class

  single { AccountRepositoryImpl(driverFactory = get(), keyStorePort = get()) }

  single { TokenRepositoryImpl(driverFactory = get()) } bind TokenRepository::class

  factory { NewsPagingSource(get()) }

  single { (chainId: ChainId) -> BitcoinAdapter(chainId) } binds arrayOf(
    BalanceService::class,
    Broadcaster::class,
    FeeService::class,
    HistoryService::class,
    TransactionBuilder::class
  )

  single { (chainId: ChainId) -> EvmAdapter(chainId = chainId, provider = get()) } binds arrayOf(
    BalanceService::class,
    Broadcaster::class,
    FeeService::class,
    HistoryService::class,
    TransactionBuilder::class
  )

  single { (chainId: ChainId) -> SolanaAdapter(chainId) } binds arrayOf(
    BalanceService::class,
    Broadcaster::class,
    FeeService::class,
    HistoryService::class,
    TransactionBuilder::class
  )

  // Legacy adapter map - kept for backward compatibility
  single {
    mapOf(
      ChainId.EVM_MAINNET.value to get<EvmAdapter> { parametersOf(ChainId.EVM_MAINNET) },
      ChainId.Polygon_MAINNET.value to get<EvmAdapter> { parametersOf(ChainId.Polygon_MAINNET) },
      ChainId.Arbitrum_MAINNET.value to get<EvmAdapter> { parametersOf(ChainId.Arbitrum_MAINNET) },
      ChainId.BTC_MAINNET.value to get<BitcoinAdapter> { parametersOf(ChainId.BTC_MAINNET) },
      ChainId.SOLANA_MAINNET.value to get<SolanaAdapter> { parametersOf(ChainId.SOLANA_MAINNET) },
      ChainId.SOLANA_DEVNET.value to get<SolanaAdapter> { parametersOf(ChainId.SOLANA_DEVNET) }
    )
  }

  // Chain management services
  single { AdapterRegistry(adapters = get(), tokenRepository = get()) }
  single { ChainContextManager(adapterRegistry = get()) }

  // Transaction and dApp services
  single { TransactionService(chainContextManager = get(), tokenRepository = get()) }
  single { Web3InjectionService(chainContextManager = get()) }
}
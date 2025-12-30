# Domain Layer Architecture for Multi-Chain Support

## Overview

This document describes the domain layer architecture designed to support multiple blockchain networks (EVM, Bitcoin, Solana, etc.) with automatic chain detection and unified operations across transaction history, sending coins, and dApp injection.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                      UI Layer (Compose)                      │
│  AssetDetailScreen → SendFlowScreen → DAppsScreen            │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│                   Use Cases (Domain)                         │
│  • GetTransactionHistoryUseCase                              │
│  • SendTokenUseCase                                          │
│  • EstimateTransactionFeeUseCase                             │
│  • ConnectDAppUseCase                                        │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│              Domain Services (Core Logic)                    │
│  ┌──────────────────────────────────────────────┐            │
│  │      ChainContextManager (Auto-Detection)    │            │
│  │  • setContextByToken(tokenId)                │            │
│  │  • setContextByChainId(chainId)              │            │
│  │  • requireCurrentContext() → ChainContext    │            │
│  └──────────────────┬───────────────────────────┘            │
│                     │                                        │
│  ┌──────────────────▼───────────────────────────┐            │
│  │         TransactionService                   │            │
│  │  • getTransactionHistory()                   │            │
│  │  • buildTransferTransaction()                │            │
│  │  • estimateFee()                             │            │
│  │  • signAndBroadcast()                        │            │
│  │  • executeTransfer()                         │            │
│  └──────────────────────────────────────────────┘            │
│                                                              │
│  ┌──────────────────────────────────────────────┐            │
│  │         Web3InjectionService                 │            │
│  │  • getProviderForCurrentChain()              │            │
│  │  • connect(dappUrl, accounts)                │            │
│  │  • handleTransactionRequest()                │            │
│  │  • handleSignMessageRequest()                │            │
│  └──────────────────────────────────────────────┘            │
│                                                              │
│  ┌──────────────────────────────────────────────┐            │
│  │           AdapterRegistry                    │            │
│  │  • getAdapter(chainId) → IChainAdapter       │            │
│  │  • getAdapterForToken(tokenId)               │            │
│  │  • getAllAdapters()                          │            │
│  └──────────────────┬───────────────────────────┘            │
└────────────────────┬┴───────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│              Chain Adapters (Data Layer)                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │  EvmAdapter  │  │BitcoinAdapter│  │SolanaAdapter │       │
│  │              │  │              │  │              │       │
│  │ • ERC20      │  │ • Native BTC │  │ • Native SOL │       │
│  │ • Native ETH │  │              │  │ • SPL tokens │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
│                                                              │
│  All implement IChainAdapter:                                │
│  • BalanceService                                            │
│  • FeeService                                                │
│  • TransactionBuilder                                        │
│  • Broadcaster                                               │
│  • HistoryService                                            │
└──────────────────────────────────────────────────────────────┘
```

## Key Components

### 1. ChainContextManager

**Purpose**: Automatically detects which blockchain the user is operating on and maintains the current chain context.

**Location**: `platform/domain/src/commonMain/kotlin/org/easy/wallet/domain/chain/ChainContextManager.kt`

**Key Methods**:
- `setContextByToken(tokenId)` - Set context based on a token (most common usage)
- `setContextByChainId(chainId)` - Set context directly by chain
- `requireCurrentContext()` - Get current context (throws if none set)
- `getCurrentContext()` - Get current context or null

**Usage Example**:
```kotlin
// In a ViewModel or Use Case
chainContextManager.setContextByToken(tokenId)
val context = chainContextManager.requireCurrentContext()
// Now context.adapter is the correct chain adapter (EVM/Bitcoin/Solana)
```

### 2. AdapterRegistry

**Purpose**: Central registry for all chain adapters. Provides lookup by chain ID or token ID.

**Location**: `platform/domain/src/commonMain/kotlin/org/easy/wallet/domain/chain/AdapterRegistry.kt`

**Key Methods**:
- `getAdapter(chainId)` - Get adapter by chain ID
- `getAdapterForToken(tokenId)` - Get adapter by token ID (looks up chain via TokenRepository)
- `getAllAdapters()` - Get all registered adapters
- `isChainSupported(chainId)` - Check if chain is supported

**Registration** (in DI):
```kotlin
// In DataModule.kt
single {
  mapOf(
    ChainId.EVM_MAINNET.value to get<EvmAdapter> { parametersOf(ChainId.EVM_MAINNET) },
    ChainId.BTC_MAINNET.value to get<BitcoinAdapter> { parametersOf(ChainId.BTC_MAINNET) },
    ChainId.SOLANA_MAINNET.value to get<SolanaAdapter> { parametersOf(ChainId.SOLANA_MAINNET) }
  )
}
single { AdapterRegistry(adapters = get(), tokenRepository = get()) }
```

### 3. TransactionService

**Purpose**: Unified transaction operations across all chains. Routes to correct adapter automatically.

**Location**: `platform/domain/src/commonMain/kotlin/org/easy/wallet/domain/transaction/TransactionService.kt`

**Key Methods**:
- `getTransactionHistory(account, pageSize)` - Get tx history for current chain
- `getTransactionHistoryForToken(tokenId, account)` - Auto-sets chain context
- `estimateFee(from, to, amount, token)` - Estimate transaction fee
- `buildTransferTransaction(...)` - Build unsigned transaction
- `signAndBroadcast(unsigned, coinType)` - Sign and broadcast transaction
- `executeTransfer(tokenId, from, to, amount, coinType)` - Complete flow: estimate → build → sign → broadcast

**Usage Example**:
```kotlin
// In SendFlowViewModel
val result = transactionService.executeTransfer(
    tokenId = tokenId,
    from = senderAddress,
    to = recipientAddress,
    amount = amountInSmallestUnit,
    coinType = coinType,
    memo = memo
)

when (result) {
    is TransferResult.Success -> {
        // Show success: result.txHash
    }
    is TransferResult.Error -> {
        // Show error: result.message
    }
}
```

### 4. Web3InjectionService

**Purpose**: Manages Web3 provider injection for dApps. Provides chain-specific providers (window.ethereum for EVM, window.solana for Solana, etc.)

**Location**: `platform/domain/src/commonMain/kotlin/org/easy/wallet/domain/dapp/Web3InjectionService.kt`

**Key Methods**:
- `getProviderForCurrentChain()` - Get provider for current chain context
- `connect(dappUrl, accounts)` - Connect wallet to dApp
- `disconnect(dappUrl)` - Disconnect from dApp
- `handleTransactionRequest(request, coinType)` - Handle tx signing request from dApp
- `handleSignMessageRequest(message, account, coinType)` - Handle message signing
- `switchChain(chainId)` - Switch active chain

**Provider Types**:
- `EvmWeb3Provider` - Implements EIP-1193 (window.ethereum)
- `BitcoinWeb3Provider` - Bitcoin wallet provider
- `SolanaWeb3Provider` - Implements window.solana wallet adapter

**Usage Example**:
```kotlin
// In DAppsScreen ViewModel
val result = web3InjectionService.connect(
    dappUrl = "https://uniswap.org",
    chainId = ChainId.EVM_MAINNET,
    accounts = listOf(userAddress)
)

when (result) {
    is ConnectionResult.Success -> {
        // Inject provider into WebView
        val provider = web3InjectionService.getProviderForCurrentChain()
    }
    is ConnectionResult.Error -> {
        // Show error
    }
}
```

## Use Cases

All use cases are located in `platform/domain/src/commonMain/kotlin/org/easy/wallet/domain/usecase/`

### 1. GetTransactionHistoryUseCase

Fetch transaction history for a specific token.

```kotlin
val pager = getTransactionHistoryUseCase(
    tokenId = tokenId,
    account = userAddress,
    pageSize = 50
)

// Use with Paging 3
val transactions = pager.flow.collectAsLazyPagingItems()
```

### 2. SendTokenUseCase

Send tokens with automatic fee estimation, transaction building, and broadcasting.

```kotlin
val result = sendTokenUseCase(
    tokenId = tokenId,
    from = senderAddress,
    to = recipientAddress,
    amount = amountInSmallestUnit,
    coinType = coinType,
    memo = "Payment for services"
)
```

### 3. EstimateTransactionFeeUseCase

Estimate transaction fee before sending.

```kotlin
val feePolicy = estimateTransactionFeeUseCase(
    tokenId = tokenId,
    from = senderAddress,
    to = recipientAddress,
    amount = amountInSmallestUnit
)

// Display fee to user
val feeAmount = feePolicy.feeAmount
```

### 4. ConnectDAppUseCase

Connect wallet to a dApp with specific chain and accounts.

```kotlin
val result = connectDAppUseCase(
    dappUrl = "https://app.uniswap.org",
    chainId = ChainId.EVM_MAINNET,
    accounts = listOf(userAddress)
)
```

## How It Works: Automatic Chain Detection

### Scenario 1: User Sends ETH

1. User navigates to ETH asset detail screen
2. Taps "Send" button
3. Navigation passes `tokenId` to SendFlow
4. `SendFlowViewModel` calls `sendTokenUseCase(tokenId, ...)`
5. **Automatic Detection**:
   - `SendTokenUseCase` calls `TransactionService.executeTransfer(tokenId, ...)`
   - `TransactionService` calls `chainContextManager.setContextByToken(tokenId)`
   - `ChainContextManager` looks up token in repository → finds `chainId = "evm:1"`
   - `ChainContextManager` gets `EvmAdapter` from `AdapterRegistry`
   - `ChainContext` is now set with `EvmAdapter`
6. Transaction operations are routed to `EvmAdapter`:
   - Fee estimation → `EvmAdapter.estimateTransferFee()`
   - Transaction building → `EvmAdapter.buildTransferTx()`
   - Broadcasting → `EvmAdapter.signAndBroadcast()`

### Scenario 2: User Views Bitcoin Transaction History

1. User navigates to BTC asset detail screen with `tokenId`
2. `AssetDetailViewModel` calls `getTransactionHistoryUseCase(tokenId, ...)`
3. **Automatic Detection**:
   - Use case calls `TransactionService.getTransactionHistoryForToken(tokenId, ...)`
   - `TransactionService` calls `chainContextManager.setContextByToken(tokenId)`
   - `ChainContextManager` finds `chainId = "btc:main"`
   - `ChainContextManager` gets `BitcoinAdapter`
4. History fetching is routed to `BitcoinAdapter.getTransfers()`

### Scenario 3: User Connects to Uniswap dApp

1. User opens dApp browser and navigates to Uniswap
2. dApp requests wallet connection
3. User selects Ethereum network
4. `DAppsViewModel` calls `connectDAppUseCase(dappUrl, ChainId.EVM_MAINNET, accounts)`
5. **Automatic Detection**:
   - Use case calls `chainContextManager.setContextByChainId(ChainId.EVM_MAINNET)`
   - `ChainContextManager` gets `EvmAdapter`
   - `Web3InjectionService.connect()` is called
   - `EvmWeb3Provider` is created and injected into WebView
6. dApp can now call `window.ethereum` methods which route through `EvmAdapter`

## Adding a New Chain

To add support for a new blockchain (e.g., Tron):

### 1. Create the Adapter

```kotlin
// platform/data/src/commonMain/kotlin/org/easy/wallet/data/adapter/TronAdapter.kt

class TronAdapter(
    override val chainId: ChainId
) : IChainAdapter, BalanceService, FeeService, TransactionBuilder, Broadcaster, HistoryService {

    override val supportedStandards = setOf(TokenStandard.NATIVE, TokenStandard.TRC20)

    override suspend fun getBalance(account: Address, contract: String?): BigInteger {
        // Implement Tron balance fetching
    }

    override suspend fun estimateTransferFee(...): FeePolicy {
        // Implement Tron fee estimation
    }

    override suspend fun buildTransferTx(...): UnsignedTx {
        // Implement Tron transaction building
    }

    override suspend fun signAndBroadcast(...): String {
        // Implement Tron transaction broadcasting
    }

    override fun getTransfers(...): Pager<Int, Transfer> {
        // Implement Tron transaction history
    }
}
```

### 2. Add ChainId

```kotlin
// platform/model/src/commonMain/kotlin/org/easy/wallet/model/TokenModels.kt

@JvmInline
value class ChainId(val value: String) {
    companion object {
        // ... existing chains
        val TRON_MAINNET = ChainId("tron:mainnet")
    }
}
```

### 3. Register in DI

```kotlin
// platform/data/src/commonMain/kotlin/org/easy/wallet/data/di/DataModule.kt

val dataModule = module {
    // ... existing adapters

    single { (chainId: ChainId) -> TronAdapter(chainId) } binds arrayOf(
        BalanceService::class,
        Broadcaster::class,
        FeeService::class,
        HistoryService::class,
        TransactionBuilder::class
    )

    single {
        mapOf(
            // ... existing chains
            ChainId.TRON_MAINNET.value to get<TronAdapter> { parametersOf(ChainId.TRON_MAINNET) }
        )
    }
}
```

### 4. (Optional) Add Web3 Provider for dApp Support

```kotlin
// platform/domain/src/commonMain/kotlin/org/easy/wallet/domain/dapp/Web3InjectionService.kt

class TronWeb3Provider(override val chainId: ChainId) : Web3Provider {
    override suspend fun connect(dappUrl: String, accounts: List<Address>) {
        // Implement TronLink-compatible provider
    }
    // ... implement other methods
}

// Update createProvider()
private fun createProvider(chainId: ChainId): Web3Provider {
    return when {
        chainId.value.startsWith("evm:") -> EvmWeb3Provider(chainId)
        chainId.value.startsWith("btc:") -> BitcoinWeb3Provider(chainId)
        chainId.value.startsWith("solana:") -> SolanaWeb3Provider(chainId)
        chainId.value.startsWith("tron:") -> TronWeb3Provider(chainId) // Add this
        else -> throw IllegalArgumentException("Unsupported chain: ${chainId.value}")
    }
}
```

That's it! The entire domain layer automatically works with the new chain.

## Integration with Existing Code

### SendFlowViewModel

Update to use the new use cases:

```kotlin
class SendFlowViewModel(
    private val sendTokenUseCase: SendTokenUseCase,
    private val estimateFeeUseCase: EstimateTransactionFeeUseCase
) : ViewModel() {

    suspend fun estimateFee(amount: BigInteger) {
        val fee = estimateFeeUseCase(
            tokenId = currentTokenId,
            from = senderAddress,
            to = recipientAddress,
            amount = amount
        )
        _state.value = _state.value.copy(estimatedFee = fee)
    }

    suspend fun sendTransaction() {
        val result = sendTokenUseCase(
            tokenId = currentTokenId,
            from = senderAddress,
            to = recipientAddress,
            amount = amount,
            coinType = coinType,
            memo = memo
        )

        when (result) {
            is TransferResult.Success -> {
                // Navigate to success screen
                _txHash.value = result.txHash
            }
            is TransferResult.Error -> {
                // Show error
                _error.value = result.message
            }
        }
    }
}
```

### AssetDetailViewModel

Update to use transaction history use case:

```kotlin
class AssetDetailViewModel(
    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase
) : ViewModel() {

    val transactionHistory: Flow<PagingData<Transfer>> =
        getTransactionHistoryUseCase(
            tokenId = tokenId,
            account = userAddress,
            pageSize = 50
        ).flow.cachedIn(viewModelScope)
}
```

### DAppsViewModel

New ViewModel for dApp browser:

```kotlin
class DAppsViewModel(
    private val connectDAppUseCase: ConnectDAppUseCase,
    private val web3InjectionService: Web3InjectionService
) : ViewModel() {

    suspend fun connectToPlat(dappUrl: String, chainId: ChainId) {
        val result = connectDAppUseCase(
            dappUrl = dappUrl,
            chainId = chainId,
            accounts = listOf(userAddress)
        )

        when (result) {
            is ConnectionResult.Success -> {
                // Get provider and inject into WebView
                val provider = web3InjectionService.getProviderForCurrentChain()
                // Inject provider.getJavaScriptInterface() into WebView
            }
            is ConnectionResult.Error -> {
                _error.value = result.message
            }
        }
    }
}
```

## Benefits of This Architecture

1. **Automatic Chain Detection**: No manual chain selection needed - context is automatically set based on the token/operation
2. **Type Safety**: All chain IDs and token IDs are type-safe value classes
3. **Extensibility**: Adding new chains requires only implementing `IChainAdapter` and registering in DI
4. **Testability**: Each component can be tested independently with mock adapters
5. **Single Responsibility**: Each service has a clear, focused purpose
6. **DRY**: Common transaction flows (estimate → build → sign → broadcast) are unified in `TransactionService`
7. **Clean Architecture**: Clear separation between UI → Use Cases → Services → Adapters → Network
8. **Dependency Injection**: All dependencies are injected via Koin, making the code flexible and testable

## Current Implementation Status

### ✅ Completed
- Domain layer architecture design
- ChainContextManager for automatic chain detection
- AdapterRegistry for managing adapters
- TransactionService for unified transaction operations
- Web3InjectionService for dApp integration (architecture)
- Use cases for common operations
- Solana adapter skeleton
- DI configuration

### ⚠️ Partial
- EVM adapter (balance ✅, history ✅, tx building ❌, signing ❌)
- Bitcoin adapter (stubs only)

### ❌ To Implement
- EVM: estimateTransferFee(), buildTransferTx(), signAndBroadcast()
- Bitcoin: all methods
- Solana: all methods
- Web3 providers: EVM, Bitcoin, Solana injection logic
- DApp browser UI with WebView
- Update existing ViewModels to use new use cases

## Next Steps

1. **Implement Transaction Building** for EVM chains
2. **Implement Bitcoin Adapter** fully
3. **Implement Solana Adapter** fully
4. **Create DApp Browser UI** with WebView
5. **Implement Web3 Provider Injection** JavaScript interfaces
6. **Update SendFlowViewModel** to use `SendTokenUseCase`
7. **Update AssetDetailViewModel** to use `GetTransactionHistoryUseCase`
8. **Add Unit Tests** for all domain components
9. **Add Integration Tests** for transaction flows

## File Structure

```
platform/
├── domain/src/commonMain/kotlin/org/easy/wallet/domain/
│   ├── chain/
│   │   ├── ChainContextManager.kt
│   │   ├── ChainContext.kt
│   │   └── AdapterRegistry.kt
│   ├── transaction/
│   │   └── TransactionService.kt
│   ├── dapp/
│   │   └── Web3InjectionService.kt
│   └── usecase/
│       ├── GetTransactionHistoryUseCase.kt
│       ├── SendTokenUseCase.kt
│       ├── EstimateTransactionFeeUseCase.kt
│       └── ConnectDAppUseCase.kt
├── data/src/commonMain/kotlin/org/easy/wallet/data/
│   ├── adapter/
│   │   ├── EvmAdapter.kt
│   │   ├── BitcoinAdapter.kt
│   │   └── SolanaAdapter.kt
│   └── di/
│       └── DataModule.kt (updated)
└── model/src/commonMain/kotlin/org/easy/wallet/model/
    └── TokenModels.kt (updated with Solana ChainIds)
```

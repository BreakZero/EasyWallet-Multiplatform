# Multi-Chain Domain Layer - Quick Reference

## What Was Built

A comprehensive domain layer that **automatically detects which blockchain the user is operating on** and routes all operations (transaction history, sending, dApp interaction) to the correct chain adapter.

## Key Features

✅ **Automatic Chain Detection** - Based on the token the user selects
✅ **Unified API** - Same code works for EVM, Bitcoin, Solana, and future chains
✅ **Transaction Management** - Complete flow: estimate fee → build → sign → broadcast
✅ **dApp Integration** - Web3 provider injection for each chain type
✅ **Type-Safe** - All IDs use value classes (ChainId, TokenId, Address)
✅ **Extensible** - Add new chains by implementing one interface
✅ **Testable** - Clean dependency injection via Koin

## Architecture at a Glance

```
User selects token → ChainContextManager detects chain → Routes to correct adapter
                                    ↓
        ┌───────────────────────────┴──────────────────────────┐
        │                                                      │
    EvmAdapter                BitcoinAdapter             SolanaAdapter
        │                         │                            │
     Ethereum                   Bitcoin                     Solana
     Polygon                                                (SPL tokens)
     Arbitrum
```

## Core Components

| Component | Purpose | Location |
|-----------|---------|----------|
| **ChainContextManager** | Auto-detects which chain to use | `domain/chain/ChainContextManager.kt` |
| **AdapterRegistry** | Manages all chain adapters | `domain/chain/AdapterRegistry.kt` |
| **TransactionService** | Unified transaction operations | `domain/transaction/TransactionService.kt` |
| **Web3InjectionService** | dApp provider injection | `domain/dapp/Web3InjectionService.kt` |
| **Use Cases** | High-level operations for UI | `domain/usecase/*.kt` |

## Quick Start Examples

### 1. Send Any Token (Auto-Detects Chain)

```kotlin
class SendViewModel(
    private val sendTokenUseCase: SendTokenUseCase
) : ViewModel() {

    suspend fun send(tokenId: TokenId, to: Address, amount: BigInteger) {
        val result = sendTokenUseCase(
            tokenId = tokenId,  // ETH, BTC, SOL - doesn't matter!
            from = myAddress,
            to = to,
            amount = amount,
            coinType = coinType
        )

        when (result) {
            is TransferResult.Success -> showSuccess(result.txHash)
            is TransferResult.Error -> showError(result.message)
        }
    }
}
```

### 2. View Transaction History

```kotlin
class HistoryViewModel(
    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase
) : ViewModel() {

    val history: Flow<PagingData<Transfer>> =
        getTransactionHistoryUseCase(
            tokenId = tokenId,  // Auto-detects chain
            account = myAddress
        ).flow
}
```

### 3. Connect to dApp

```kotlin
class DAppViewModel(
    private val connectDAppUseCase: ConnectDAppUseCase
) : ViewModel() {

    suspend fun connect(url: String, chain: ChainId) {
        val result = connectDAppUseCase(
            dappUrl = url,
            chainId = chain,  // EVM, Solana, etc.
            accounts = listOf(myAddress)
        )
    }
}
```

## How Chain Detection Works

```kotlin
// User taps on ETH in asset list
navigation.navigate("send/${ethTokenId}")

// SendViewModel receives ethTokenId
sendTokenUseCase(tokenId = ethTokenId, ...)
    ↓
// TransactionService automatically:
chainContextManager.setContextByToken(ethTokenId)
    ↓
// Looks up: ethTokenId → chainId = "evm:1"
    ↓
// Gets: EvmAdapter from AdapterRegistry
    ↓
// All operations now route to EvmAdapter!
```

## Supported Chains

| Chain | ChainId | Adapter | Status |
|-------|---------|---------|--------|
| Ethereum | `ChainId.EVM_MAINNET` | `EvmAdapter` | ⚠️ Partial |
| Polygon | `ChainId.Polygon_MAINNET` | `EvmAdapter` | ⚠️ Partial |
| Arbitrum | `ChainId.Arbitrum_MAINNET` | `EvmAdapter` | ⚠️ Partial |
| Bitcoin | `ChainId.BTC_MAINNET` | `BitcoinAdapter` | ⚠️ Skeleton |
| Solana | `ChainId.SOLANA_MAINNET` | `SolanaAdapter` | ⚠️ Skeleton |

**Partial** = Some methods implemented (balance, history)
**Skeleton** = Structure ready, needs implementation

## Adding a New Chain (3 Steps)

### 1. Create Adapter

```kotlin
class MyChainAdapter(
    override val chainId: ChainId
) : IChainAdapter, BalanceService, FeeService, TransactionBuilder, Broadcaster, HistoryService {

    override val supportedStandards = setOf(TokenStandard.NATIVE)

    override suspend fun getBalance(...) { /* implement */ }
    override suspend fun estimateTransferFee(...) { /* implement */ }
    override suspend fun buildTransferTx(...) { /* implement */ }
    override suspend fun signAndBroadcast(...) { /* implement */ }
    override fun getTransfers(...) { /* implement */ }
}
```

### 2. Add ChainId

```kotlin
// In TokenModels.kt
companion object {
    val MY_CHAIN_MAINNET = ChainId("mychain:mainnet")
}
```

### 3. Register in DI

```kotlin
// In DataModule.kt
single { (chainId: ChainId) -> MyChainAdapter(chainId) }

single {
    mapOf(
        // ... existing chains
        ChainId.MY_CHAIN_MAINNET.value to get<MyChainAdapter> { parametersOf(ChainId.MY_CHAIN_MAINNET) }
    )
}
```

**Done!** The entire app now supports your chain.

## Files Created

### Domain Layer
```
platform/domain/src/commonMain/kotlin/org/easy/wallet/domain/
├── chain/
│   ├── ChainContextManager.kt       ✅ Created
│   ├── ChainContext.kt              ✅ Created
│   └── AdapterRegistry.kt           ✅ Created
├── transaction/
│   └── TransactionService.kt        ✅ Created
├── dapp/
│   └── Web3InjectionService.kt      ✅ Created
└── usecase/
    ├── GetTransactionHistoryUseCase.kt  ✅ Created
    ├── SendTokenUseCase.kt              ✅ Created
    ├── EstimateTransactionFeeUseCase.kt ✅ Created
    └── ConnectDAppUseCase.kt            ✅ Created
```

### Data Layer
```
platform/data/src/commonMain/kotlin/org/easy/wallet/data/
├── adapter/
│   └── SolanaAdapter.kt             ✅ Created
└── di/
    └── DataModule.kt                ✅ Updated (DI config)
```

### Model Layer
```
platform/model/src/commonMain/kotlin/org/easy/wallet/model/
└── TokenModels.kt                   ✅ Updated (added Solana ChainIds)
```

### Documentation
```
docs/
├── DOMAIN_LAYER_ARCHITECTURE.md     ✅ Created (full architecture)
├── USAGE_EXAMPLES.md                ✅ Created (code examples)
└── DOMAIN_LAYER_SUMMARY.md          ✅ Created (this file)

DOMAIN_LAYER_ARCHITECTURE.md         ✅ Created (root level)
```

## What's Next?

### Immediate TODOs

1. **Complete EVM Adapter**
   - `estimateTransferFee()` - Use eth_estimateGas
   - `buildTransferTx()` - Build EIP-1559 transactions
   - `signAndBroadcast()` - Sign with TrustWallet Core and broadcast

2. **Complete Bitcoin Adapter**
   - Implement all methods
   - UTXO management
   - Fee estimation

3. **Complete Solana Adapter**
   - Implement all methods
   - SPL token support
   - Fee estimation (compute units)

4. **Update ViewModels**
   - Replace direct adapter usage with use cases
   - Update `SendFlowViewModel`
   - Update `AssetDetailViewModel`

5. **Implement Web3 Providers**
   - `EvmWeb3Provider` - window.ethereum (EIP-1193)
   - `SolanaWeb3Provider` - window.solana
   - JavaScript injection into WebView

6. **Build dApp Browser UI**
   - WebView with provider injection
   - Chain selector
   - Transaction approval flow

## Integration Points

### Where to Use in Existing Code

| Screen/ViewModel | Replace This | With This |
|------------------|--------------|-----------|
| `SendFlowViewModel` | Direct adapter calls | `SendTokenUseCase` |
| `AssetDetailViewModel` | Direct `getTransfers()` | `GetTransactionHistoryUseCase` |
| `DAppsScreen` | (Not implemented) | `ConnectDAppUseCase` + `Web3InjectionService` |

### Example Migration

**Before**:
```kotlin
// In SendFlowViewModel
val adapter = chainAdapters[token.chainId.value]
val fee = adapter.estimateTransferFee(...)
val unsigned = adapter.buildTransferTx(...)
val txHash = adapter.signAndBroadcast(...)
```

**After**:
```kotlin
// In SendFlowViewModel
val result = sendTokenUseCase(tokenId, from, to, amount, coinType)
// That's it! All the complexity is handled.
```

## Benefits Summary

| Benefit | Description |
|---------|-------------|
| **Less Code** | ViewModels don't need chain-specific logic |
| **Less Bugs** | Centralized chain routing = fewer mistakes |
| **Easier Testing** | Mock one use case instead of many adapters |
| **Faster Development** | Add features once, works on all chains |
| **Better UX** | Users don't think about chains, just tokens |
| **Future-Proof** | Easy to add new chains (Cosmos, Near, etc.) |

## Key Takeaways

1. **User operates on TOKENS, not chains** - The domain layer figures out the chain
2. **Use Cases are your API** - Always use them in ViewModels
3. **ChainContextManager is automatic** - You rarely need to interact with it directly
4. **Adding chains is easy** - Just implement `IChainAdapter` and register
5. **Same code, all chains** - Write once, works everywhere

## Questions?

- **"How do I know which chain I'm on?"** - You don't need to! Pass `tokenId` and it's automatic.
- **"What if I need manual control?"** - Use `ChainContextManager.setContextByChainId()`
- **"How do I add a new coin?"** - Implement `IChainAdapter` (see "Adding a New Chain" above)
- **"Is this tested?"** - Architecture is ready, unit tests still needed
- **"When will it be complete?"** - After implementing TODOs in each adapter

## Resources

- **Full Architecture**: See `DOMAIN_LAYER_ARCHITECTURE.md`
- **Code Examples**: See `docs/USAGE_EXAMPLES.md`
- **Adapter Interface**: See `platform/data/src/commonMain/kotlin/org/easy/wallet/data/interfaces/IChainAdapter.kt`

---

**Status**: ✅ Architecture complete, ready for implementation

**Created**: 2025-12-30

**Author**: Domain layer design for EasyWallet-Multiplatform

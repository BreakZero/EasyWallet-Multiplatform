# Architecture Diagrams

## 1. Overall System Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           UI Layer (Compose)                            │
│                                                                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌─────────────┐  │
│  │ AssetsScreen │  │AssetDetail   │  │SendFlowScreen│  │DAppsScreen  │  │
│  │              │  │Screen        │  │              │  │             │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬──────┘  │
└─────────┼─────────────────┼─────────────────┼─────────────────┼─────────┘
          │                 │                 │                 │
          ▼                 ▼                 ▼                 ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         ViewModel Layer                                 │
│                                                                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌─────────────┐  │
│  │ AssetsVM     │  │AssetDetailVM │  │SendFlowVM    │  │DAppsVM      │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬──────┘  │
└─────────┼─────────────────┼─────────────────┼─────────────────┼─────────┘
          │                 │                 │                 │
          ▼                 ▼                 ▼                 ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      Use Case Layer (Domain)                            │
│                                                                         │
│  ┌──────────────────────┐  ┌──────────────────┐  ┌──────────────────┐   │
│  │ LoadAllBalances      │  │GetTransactionHis │  │SendToken         │   │
│  │ UseCase              │  │toryUseCase       │  │UseCase           │   │
│  └──────────────────────┘  └──────────────────┘  └──────────────────┘   │
│                                                                         │
│  ┌──────────────────────┐  ┌──────────────────┐                         │
│  │ EstimateTransaction  │  │ConnectDApp       │                         │
│  │ FeeUseCase           │  │UseCase           │                         │
│  └──────────────────────┘  └──────────────────┘                         │
└─────────────────┬───────────────────────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    Domain Services Layer                                │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │              ChainContextManager (Auto-Detection)                │   │
│  │  ┌────────────────────────────────────────────────────────────┐  │   │
│  │  │  • setContextByToken(tokenId) → detects chain              │  │   │
│  │  │  • setContextByChainId(chainId)                            │  │   │
│  │  │  • requireCurrentContext() → ChainContext                  │  │   │
│  │  │  • currentChainContext: StateFlow<ChainContext?>           │  │   │
│  │  └────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────┬───────────────────────────────────────┘   │
│                             │                                           │
│  ┌──────────────────────────▼───────────────────────────────────────┐   │
│  │                  AdapterRegistry                                 │   │
│  │  ┌────────────────────────────────────────────────────────────┐  │   │
│  │  │  Map<String, IChainAdapter>:                               │  │   │
│  │  │    "evm:1"     → EvmAdapter                                │  │   │
│  │  │    "evm:137"   → EvmAdapter (Polygon)                      │  │   │
│  │  │    "btc:main"  → BitcoinAdapter                            │  │   │
│  │  │    "solana:mainnet" → SolanaAdapter                        │  │   │
│  │  └────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────┬───────────────────────────────────────┘   │
│                             │                                           │
│  ┌──────────────────────────▼───────────────────────────────────────┐   │
│  │              TransactionService                                  │   │
│  │  • getTransactionHistory(account)                                │   │
│  │  • estimateFee(from, to, amount, token)                          │   │
│  │  • buildTransferTransaction(...)                                 │   │
│  │  • signAndBroadcast(unsigned, coinType)                          │   │
│  │  • executeTransfer(...) → complete flow                          │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │              Web3InjectionService                                │   │
│  │  • getProviderForCurrentChain() → Web3Provider                   │   │
│  │  • connect(dappUrl, accounts)                                    │   │
│  │  • handleTransactionRequest(request)                             │   │
│  │  • switchChain(chainId)                                          │   │
│  └──────────────────────────────────────────────────────────────────┘   │
└─────────────────┬───────────────────────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      Chain Adapter Layer (Data)                         │
│                                                                         │
│  ┌────────────────────┐  ┌────────────────────┐  ┌────────────────────┐ │
│  │   EvmAdapter       │  │  BitcoinAdapter    │  │  SolanaAdapter     │ │
│  │                    │  │                    │  │                    │ │
│  │ Implements:        │  │ Implements:        │  │ Implements:        │ │
│  │ • BalanceService   │  │ • BalanceService   │  │ • BalanceService   │ │
│  │ • FeeService       │  │ • FeeService       │  │ • FeeService       │ │
│  │ • TxBuilder        │  │ • TxBuilder        │  │ • TxBuilder        │ │
│  │ • Broadcaster      │  │ • Broadcaster      │  │ • Broadcaster      │ │
│  │ • HistoryService   │  │ • HistoryService   │  │ • HistoryService   │ │
│  │                    │  │                    │  │                    │ │
│  │ Supports:          │  │ Supports:          │  │ Supports:          │ │
│  │ • NATIVE (ETH)     │  │ • NATIVE (BTC)     │  │ • NATIVE (SOL)     │ │
│  │ • ERC20            │  │                    │  │ • SPL              │ │
│  └─────────┬──────────┘  └─────────┬──────────┘  └─────────┬──────────┘ │
└────────────┼───────────────────────┼───────────────────────┼────────────┘
             │                       │                       │
             ▼                       ▼                       ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                       Network/Blockchain Layer                          │
│                                                                         │
│  ┌────────────────────┐  ┌────────────────────┐  ┌────────────────────┐ │
│  │ EtherScanController│  │ Bitcoin RPC        │  │ Solana RPC         │ │
│  │ • getBalance       │  │ (To be impl.)      │  │ (To be impl.)      │ │
│  │ • listTransfers    │  │                    │  │                    │ │
│  │ • estimateGas      │  │                    │  │                    │ │
│  └────────────────────┘  └────────────────────┘  └────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
```

## 2. Chain Detection Flow

```
User Interaction: Tap on "ETH" token in asset list
                           │
                           ▼
Navigation: navigate("asset-detail/${ethTokenId}")
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ AssetDetailScreen receives tokenId = "eth-mainnet-native"                │
└──────────────────────────┬───────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ AssetDetailViewModel calls:                                              │
│   getTransactionHistoryUseCase(tokenId = "eth-mainnet-native", ...)      │
└──────────────────────────┬───────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ GetTransactionHistoryUseCase calls:                                      │
│   transactionService.getTransactionHistoryForToken(tokenId, ...)         │
└──────────────────────────┬───────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ TransactionService:                                                      │
│   1. chainContextManager.setContextByToken(tokenId)                      │
│   2. getTransactionHistory(account, pageSize)                            │
└──────────────────────────┬───────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ ChainContextManager.setContextByToken(tokenId):                          │
│   1. chainId = adapterRegistry.getChainIdForToken(tokenId)               │
│      └─> TokenRepository.getById("eth-mainnet-native")                   │
│          └─> Returns Token with chainId = ChainId("evm:1")               │
│                                                                          │
│   2. adapter = adapterRegistry.getAdapter(chainId)                       │
│      └─> Looks up adapters["evm:1"]                                      │
│          └─> Returns EvmAdapter                                          │
│                                                                          │
│   3. Sets _currentChainContext = ChainContext(                           │
│         tokenId = "eth-mainnet-native",                                  │
│         chainId = ChainId("evm:1"),                                      │
│         adapter = EvmAdapter                                             │
│      )                                                                   │
└──────────────────────────┬───────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ TransactionService.getTransactionHistory():                              │
│   context = chainContextManager.requireCurrentContext()                  │
│   return context.adapter.getTransfers(account, pageSize)                 │
│          └─> Calls EvmAdapter.getTransfers(...)                          │
└──────────────────────────┬───────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ EvmAdapter.getTransfers():                                               │
│   Returns Pager with TransactionPagingSource                             │
│     └─> Fetches from EtherScanController                                 │
└──────────────────────────┬───────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ UI displays transaction history for Ethereum                             │
└──────────────────────────────────────────────────────────────────────────┘

KEY INSIGHT: If user had selected "BTC" instead, the EXACT SAME CODE would
             have routed to BitcoinAdapter instead of EvmAdapter!
```

## 3. Send Transaction Flow

```
User Interaction: Enter recipient & amount, tap "Send"
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ SendFlowViewModel.sendTransaction(recipientAddress, amount, ...)         │
└──────────────────────────┬───────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ SendTokenUseCase(                                                        │
│   tokenId = currentTokenId,  // e.g., "eth-mainnet-native"               │
│   from = senderAddress,                                                  │
│   to = recipientAddress,                                                 │
│   amount = amountInWei,                                                  │
│   coinType = CoinType.ETHEREUM                                           │
│ )                                                                        │
└──────────────────────────┬───────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ TransactionService.executeTransfer():                                    │
│                                                                          │
│   Step 1: Set chain context                                              │
│   ├─> chainContextManager.setContextByToken(tokenId)                     │
│   │   └─> Detects chainId = "evm:1", sets EvmAdapter                     │
│   │                                                                      │
│   Step 2: Get token metadata                                             │
│   ├─> token = tokenRepository.getById(tokenId)                           │
│   │                                                                      │
│   Step 3: Estimate fee                                                   │
│   ├─> fee = estimateFee(from, to, amount, token)                         │
│   │   └─> context.adapter.estimateTransferFee(...)                       │
│   │       └─> EvmAdapter.estimateTransferFee(...)                        │
│   │           └─> EtherScanController.estimateGas() [TODO]               │
│   │                                                                      │
│   Step 4: Build transaction                                              │
│   ├─> unsignedTx = buildTransferTransaction(from, to, token, amount, fee)│
│   │   └─> context.adapter.buildTransferTx(...)                           │
│   │       └─> EvmAdapter.buildTransferTx(...)                            │
│   │           └─> Creates UnsignedTx with raw tx data [TODO]             │
│   │                                                                      │
│   Step 5: Sign and broadcast                                             │
│   └─> txHash = signAndBroadcast(unsignedTx, coinType)                    │
│       └─> context.adapter.signAndBroadcast(...)                          │
│           └─> EvmAdapter.signAndBroadcast(...)                           │
│               ├─> Sign with TrustWallet Core [TODO]                      │
│               └─> Broadcast via EtherScanController [TODO]               │
│                                                                          │
│   Returns: TransferResult.Success(txHash, feePaid)                       │
└──────────────────────────┬───────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ SendFlowViewModel receives result:                                       │
│   when (result) {                                                        │
│     is Success → Navigate to success screen, show txHash                 │
│     is Error → Show error message to user                                │
│   }                                                                      │
└──────────────────────────────────────────────────────────────────────────┘

AUTOMATIC ROUTING: If sending BTC instead of ETH, Steps 3-5 would call
                   BitcoinAdapter methods instead. Same high-level flow!
```

## 4. dApp Connection Flow

```
User Interaction: Navigate to "https://app.uniswap.org" in dApp browser
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ DAppsViewModel.connectToPlat(                                            │
│   dappUrl = "https://app.uniswap.org",                                   │
│   chainId = ChainId.EVM_MAINNET                                          │
│ )                                                                        │
└──────────────────────────┬───────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ ConnectDAppUseCase:                                                      │
│   1. chainContextManager.setContextByChainId(chainId)                    │
│   2. web3InjectionService.connect(dappUrl, accounts)                     │
└──────────────────────────┬───────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ Web3InjectionService.connect():                                          │
│   1. provider = getProviderForCurrentChain()                             │
│      └─> Creates provider based on chainId:                              │
│          ├─> "evm:*" → EvmWeb3Provider                                   │
│          ├─> "btc:*" → BitcoinWeb3Provider                               │
│          └─> "solana:*" → SolanaWeb3Provider                             │
│                                                                          │
│   2. provider.connect(dappUrl, accounts)                                 │
│      └─> EvmWeb3Provider injects window.ethereum [TODO]                  │
└──────────────────────────┬───────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ WebView with injected JavaScript:                                        │
│                                                                          │
│   window.ethereum = {                                                    │
│     request: function({ method, params }) {                              │
│       // Routes to EvmWeb3Provider in Kotlin                             │
│     },                                                                   │
│     on: function(event, handler) { ... },                                │
│     selectedAddress: "0x...",                                            │
│     chainId: "0x1"                                                       │
│   }                                                                      │
└──────────────────────────┬───────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ Uniswap calls: window.ethereum.request({ method: 'eth_sendTransaction' })│
└──────────────────────────┬───────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ EvmWeb3Provider.signTransaction(request, coinType):                      │
│   1. Show transaction approval dialog to user                            │
│   2. If approved, build transaction via EvmAdapter                       │
│   3. Sign and broadcast                                                  │
│   4. Return transaction hash to dApp                                     │
└──────────────────────────┬───────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ Uniswap receives transaction hash, shows confirmation UI                 │
└──────────────────────────────────────────────────────────────────────────┘
```

## 5. Multi-Chain Comparison

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        Same Operation, Different Chains                 │
└─────────────────────────────────────────────────────────────────────────┘

ETHEREUM (EVM)                 BITCOIN                      SOLANA
─────────────────             ─────────────────            ─────────────────

User selects ETH              User selects BTC             User selects SOL
      │                             │                            │
      ▼                             ▼                            ▼
SendTokenUseCase(              SendTokenUseCase(            SendTokenUseCase(
  tokenId = "eth-..."            tokenId = "btc-..."          tokenId = "sol-..."
)                              )                            )
      │                             │                            │
      ▼                             ▼                            ▼
ChainContextManager            ChainContextManager          ChainContextManager
detects chainId:               detects chainId:             detects chainId:
"evm:1"                        "btc:main"                   "solana:mainnet"
      │                             │                            │
      ▼                             ▼                            ▼
Gets EvmAdapter                Gets BitcoinAdapter          Gets SolanaAdapter
      │                             │                            │
      ▼                             ▼                            ▼
EvmAdapter.                    BitcoinAdapter.              SolanaAdapter.
estimateTransferFee()          estimateTransferFee()        estimateTransferFee()
└─> Gas: 21000 * gasPrice      └─> Fee: sat/vbyte          └─> Fee: lamports/sig
      │                             │                            │
      ▼                             ▼                            ▼
EvmAdapter.                    BitcoinAdapter.              SolanaAdapter.
buildTransferTx()              buildTransferTx()            buildTransferTx()
└─> EIP-1559 tx                └─> UTXO-based tx            └─> Solana tx
      │                             │                            │
      ▼                             ▼                            ▼
EvmAdapter.                    BitcoinAdapter.              SolanaAdapter.
signAndBroadcast()             signAndBroadcast()           signAndBroadcast()
└─> ECDSA signature            └─> ECDSA signature          └─> Ed25519 signature
└─> Broadcast to Ethereum      └─> Broadcast to Bitcoin     └─> Broadcast to Solana
      │                             │                            │
      ▼                             ▼                            ▼
Returns txHash                 Returns txHash               Returns signature
```

## 6. Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                            DATA FLOW                                    │
└─────────────────────────────────────────────────────────────────────────┘

User Action                   Domain Layer                Network/Blockchain
───────────                  ──────────────              ──────────────────

  [Tap "Send"]
       │
       ▼
[Enter Amount] ───────────▶ EstimateFee
       │                   UseCase
       │                      │
       │                      ▼
       │              ChainContextManager
       │              (detects chain)
       │                      │
       │                      ▼
       │              TransactionService
       │                      │
       │                      ▼
       │              EvmAdapter ─────────▶ EtherScan API
       │              .estimateFee()          (eth_estimateGas)
       │                      │                     │
       │                      ◀─────────────────────┘
       │                  Fee = 0.001 ETH
       │                      │
       ◀──────────────────────┘
  [Show Fee]


  [Tap Confirm]
       │
       ▼
[Confirm Send] ──────────▶ SendToken
       │                   UseCase
       │                      │
       │                      ▼
       │              ChainContextManager
       │                      │
       │                      ▼
       │              TransactionService
       │              .executeTransfer()
       │                      │
       │                      ├─▶ estimateFee() ──▶ EtherScan
       │                      │                        │
       │                      │   ◀────────────────────┘
       │                      │
       │                      ├─▶ buildTransferTx()
       │                      │   (creates unsigned tx)
       │                      │
       │                      ├─▶ signAndBroadcast()
       │                      │      │
       │                      │      ├─▶ TrustWallet Core
       │                      │      │   (sign tx)
       │                      │      │
       │                      │      └─▶ EtherScan API
       │                      │          (broadcast tx)
       │                      │                │
       │                      ◀────────────────┘
       │                  txHash = "0x..."
       │                      │
       ◀──────────────────────┘
  [Show Success]
```

## 7. Class Dependency Graph

```
┌────────────────────────────────────────────────────────────────────────┐
│                         CLASS DEPENDENCIES                             │
└────────────────────────────────────────────────────────────────────────┘

┌─────────────────┐
│  UI Components  │
│  (Composables)  │
└────────┬────────┘
         │ observes
         ▼
┌─────────────────┐
│   ViewModels    │
└────────┬────────┘
         │ calls
         ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                           Use Cases                                     │
│ ┌───────────────┐  ┌───────────────┐  ┌───────────────┐                 │
│ │ SendTokenUC   │  │ GetHistoryUC  │  │EstimateFeeUC  │                 │
│ └───────┬───────┘  └───────┬───────┘  └───────┬───────┘                 │
└─────────┼──────────────────┼──────────────────┼─────────────────────────┘
          │ depends on       │                  │
          └──────────────────┼──────────────────┘
                             ▼
          ┌──────────────────────────────────────────┐
          │       TransactionService                 │
          │  • getTransactionHistory()               │
          │  • executeTransfer()                     │
          │  • estimateFee()                         │
          └────────┬─────────────────┬───────────────┘
                   │                 │
      ┌────────────┘                 └────────────┐
      │ depends on                     depends on │
      ▼                                           ▼
┌──────────────────────┐                 ┌─────────────────┐
│ ChainContextManager  │                 │ TokenRepository │
│  • currentContext    │                 └─────────────────┘
│  • setContextByToken │
└──────┬───────────────┘
       │ depends on
       ▼
┌──────────────────────────────────────────────────────────────┐
│                     AdapterRegistry                          │
│  Map<String, IChainAdapter>                                  │
└──────┬───────────────────────────────────────────────────────┘
       │ provides
       ▼
┌──────────────────────────────────────────────────────────────┐
│                    IChainAdapter                             │
│  ┌────────────┐  ┌────────────┐  ┌─────────────┐             │
│  │ EvmAdapter │  │BtcAdapter  │  │SolanaAdapter│             │
│  └─────┬──────┘  └─────┬──────┘  └─────┬───────┘             │
└────────┼───────────────┼───────────────┼─────────────────────┘
         │               │               │
         └───────────────┼───────────────┘
                         ▼
         ┌───────────────────────────────┐
         │    Network Controllers        │
         │  • EtherScanController        │
         │  • Bitcoin RPC                │
         │  • Solana RPC                 │
         └───────────────────────────────┘
```

## 8. State Management Flow

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      STATE MANAGEMENT                                   │
└─────────────────────────────────────────────────────────────────────────┘

ChainContextManager maintains current chain state:

┌────────────────────────────────────────────────────────────────────────┐
│ ChainContextManager                                                    │
│                                                                        │
│  private val _currentChainContext = MutableStateFlow<ChainContext?>(   │
│    null                                                                │
│  )                                                                     │
│                                                                        │
│  val currentChainContext: StateFlow<ChainContext?> =                   │
│    _currentChainContext.asStateFlow()                                  │
└────────────────────────────────────────────────────────────────────────┘
                                   │
                    ┌──────────────┼──────────────┐
                    │              │              │
                    ▼              ▼              ▼
         ┌───────────────┐  ┌────────────┐  ┌───────────┐
         │setContextBy   │  │setContextBy│  │clear      │
         │Token()        │  │ChainId()   │  │Context()  │
         └───────┬───────┘  └─────┬──────┘  └─────┬─────┘
                 │                │               │
                 │                │               │
                 ▼                ▼               ▼
         ┌─────────────────────────────────────────────┐
         │    _currentChainContext.value = ...         │
         └─────────────────┬───────────────────────────┘
                           │
                           │ emits
                           ▼
         ┌─────────────────────────────────────────────┐
         │ All subscribers receive new ChainContext:   │
         │  • TransactionService                       │
         │  • Web3InjectionService                     │
         │  • ViewModels (if observing)                │
         └─────────────────────────────────────────────┘


Example Timeline:

Time  │ Action                    │ State
──────┼───────────────────────────┼────────────────────────────────
  0   │ App starts                │ currentChainContext = null
  1   │ User taps ETH             │ currentChainContext = null
  2   │ Navigate to detail        │ currentChainContext = null
  3   │ ViewModel calls usecase   │ currentChainContext = null
  4   │ setContextByToken(eth)    │ currentChainContext = ChainContext(
      │                           │   tokenId = "eth-...",
      │                           │   chainId = "evm:1",
      │                           │   adapter = EvmAdapter
      │                           │ )
  5   │ Load transaction history  │ (uses EvmAdapter from context)
  6   │ User navigates away       │ currentChainContext = ChainContext(...)
  7   │ User taps BTC             │ currentChainContext = ChainContext(...)
  8   │ setContextByToken(btc)    │ currentChainContext = ChainContext(
      │                           │   tokenId = "btc-...",
      │                           │   chainId = "btc:main",
      │                           │   adapter = BitcoinAdapter
      │                           │ )
```

## Summary

These diagrams illustrate:

1. **Layered Architecture**: Clear separation between UI, Domain, Data, and Network layers
2. **Automatic Routing**: ChainContextManager detects chain and routes to correct adapter
3. **Unified Interface**: Same high-level API works for all chains
4. **State Management**: Reactive state flow for chain context
5. **Dependency Flow**: Clean dependency graph with proper separation of concerns

The key insight: **User operates on tokens, domain layer figures out the chain automatically**.
# Usage Examples for Multi-Chain Domain Layer

This document provides practical examples of how to use the multi-chain domain layer in your application.

## Table of Contents

1. [Basic Transaction Flow](#basic-transaction-flow)
2. [Viewing Transaction History](#viewing-transaction-history)
3. [Estimating Transaction Fees](#estimating-transaction-fees)
4. [Connecting to dApps](#connecting-to-dapps)
5. [Working with Different Chains](#working-with-different-chains)
6. [Manual Chain Context Management](#manual-chain-context-management)

## Basic Transaction Flow

### Example 1: Send ETH from SendFlowViewModel

```kotlin
class SendFlowViewModel(
    private val sendTokenUseCase: SendTokenUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tokenId: TokenId = savedStateHandle.get<String>("tokenId")
        ?.let { TokenId(it) } ?: error("No tokenId provided")

    private val _transferState = MutableStateFlow<TransferState>(TransferState.Idle)
    val transferState: StateFlow<TransferState> = _transferState.asStateFlow()

    fun sendTransaction(
        recipientAddress: String,
        amount: String,
        coinType: CoinType,
        memo: String? = null
    ) {
        viewModelScope.launch {
            _transferState.value = TransferState.Loading

            // Convert amount from user input to smallest unit (wei for ETH)
            val amountInWei = amount.toBigDecimal()
                .multiply(BigDecimal.TEN.pow(18))
                .toBigInteger()

            val result = sendTokenUseCase(
                tokenId = tokenId,
                from = Address(userWalletAddress),
                to = Address(recipientAddress),
                amount = amountInWei,
                coinType = coinType,
                memo = memo
            )

            _transferState.value = when (result) {
                is TransferResult.Success -> {
                    TransferState.Success(
                        txHash = result.txHash,
                        feePaid = result.feePaid
                    )
                }
                is TransferResult.Error -> {
                    TransferState.Error(result.message)
                }
            }
        }
    }
}

sealed class TransferState {
    object Idle : TransferState()
    object Loading : TransferState()
    data class Success(val txHash: String, val feePaid: FeePolicy) : TransferState()
    data class Error(val message: String) : TransferState()
}
```

### Example 2: Send Bitcoin

The exact same code works for Bitcoin! Just pass a Bitcoin token ID:

```kotlin
// The use case automatically detects it's Bitcoin and routes to BitcoinAdapter
val result = sendTokenUseCase(
    tokenId = TokenId("bitcoin-mainnet-native"), // Bitcoin token
    from = Address(bitcoinAddress),
    to = Address(recipientBitcoinAddress),
    amount = satoshiAmount,
    coinType = CoinType.BITCOIN,
    memo = null // Bitcoin doesn't support memos in standard transfers
)
```

### Example 3: Send Solana SPL Token

Same code, different token:

```kotlin
val result = sendTokenUseCase(
    tokenId = TokenId("solana-usdc"), // USDC on Solana
    from = Address(solanaAddress),
    to = Address(recipientSolanaAddress),
    amount = usdcAmount,
    coinType = CoinType.SOLANA,
    memo = "Payment for services"
)
```

## Viewing Transaction History

### Example 4: Display Transaction History in AssetDetailScreen

```kotlin
class AssetDetailViewModel(
    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tokenId: TokenId = savedStateHandle.get<String>("tokenId")
        ?.let { TokenId(it) } ?: error("No tokenId provided")

    // Automatically fetches history for the correct chain
    val transactionHistory: Flow<PagingData<Transfer>> =
        flow {
            emit(
                getTransactionHistoryUseCase(
                    tokenId = tokenId,
                    account = Address(userWalletAddress),
                    pageSize = 50
                )
            )
        }
        .flatMapLatest { it.flow }
        .cachedIn(viewModelScope)
}
```

Usage in Composable:

```kotlin
@Composable
fun AssetDetailScreen(
    viewModel: AssetDetailViewModel = koinViewModel()
) {
    val transactions = viewModel.transactionHistory.collectAsLazyPagingItems()

    LazyColumn {
        items(transactions.itemCount) { index ->
            transactions[index]?.let { transfer ->
                TransactionItem(transfer = transfer)
            }
        }
    }
}

@Composable
fun TransactionItem(transfer: Transfer) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            Text(text = "To: ${transfer.to.value}")
            Text(text = "Amount: ${transfer.amount}")
            Text(text = "Fee: ${transfer.feePaid}")
            Text(text = "Status: ${transfer.status}")
            Text(text = "Hash: ${transfer.txHash}")
        }
    }
}
```

## Estimating Transaction Fees

### Example 5: Show Fee Estimate Before Sending

```kotlin
class SendFlowViewModel(
    private val estimateFeeUseCase: EstimateTransactionFeeUseCase,
    private val sendTokenUseCase: SendTokenUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tokenId: TokenId = savedStateHandle.get<String>("tokenId")
        ?.let { TokenId(it) } ?: error("No tokenId provided")

    private val _feeEstimate = MutableStateFlow<FeeEstimate>(FeeEstimate.None)
    val feeEstimate: StateFlow<FeeEstimate> = _feeEstimate.asStateFlow()

    fun estimateFee(
        recipientAddress: String,
        amount: String
    ) {
        viewModelScope.launch {
            _feeEstimate.value = FeeEstimate.Loading

            val amountInWei = amount.toBigDecimal()
                .multiply(BigDecimal.TEN.pow(18))
                .toBigInteger()

            try {
                val feePolicy = estimateFeeUseCase(
                    tokenId = tokenId,
                    from = Address(userWalletAddress),
                    to = Address(recipientAddress),
                    amount = amountInWei
                )

                _feeEstimate.value = FeeEstimate.Success(feePolicy)
            } catch (e: Exception) {
                _feeEstimate.value = FeeEstimate.Error(e.message ?: "Unknown error")
            }
        }
    }

    // Call this when user changes amount or recipient
    fun onAmountChanged(newAmount: String, recipientAddress: String) {
        if (newAmount.isNotBlank() && recipientAddress.isNotBlank()) {
            estimateFee(recipientAddress, newAmount)
        }
    }
}

sealed class FeeEstimate {
    object None : FeeEstimate()
    object Loading : FeeEstimate()
    data class Success(val feePolicy: FeePolicy) : FeeEstimate()
    data class Error(val message: String) : FeeEstimate()
}
```

Usage in Composable:

```kotlin
@Composable
fun EnterAmountScreen(
    viewModel: SendFlowViewModel = koinViewModel()
) {
    val feeEstimate by viewModel.feeEstimate.collectAsState()

    Column {
        // Amount input field
        OutlinedTextField(
            value = amount,
            onValueChange = { newAmount ->
                amount = newAmount
                viewModel.onAmountChanged(newAmount, recipientAddress)
            },
            label = { Text("Amount") }
        )

        // Fee display
        when (val estimate = feeEstimate) {
            is FeeEstimate.Loading -> {
                CircularProgressIndicator()
            }
            is FeeEstimate.Success -> {
                Text("Estimated Fee: ${estimate.feePolicy.feeAmount} wei")
                estimate.feePolicy.gasPrice?.let { gasPrice ->
                    Text("Gas Price: $gasPrice")
                }
                estimate.feePolicy.gasLimit?.let { gasLimit ->
                    Text("Gas Limit: $gasLimit")
                }
            }
            is FeeEstimate.Error -> {
                Text(
                    text = "Fee estimation failed: ${estimate.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
            FeeEstimate.None -> {
                // No estimate yet
            }
        }
    }
}
```

## Connecting to dApps

### Example 6: Connect to Uniswap (EVM dApp)

```kotlin
class DAppsViewModel(
    private val connectDAppUseCase: ConnectDAppUseCase,
    private val web3InjectionService: Web3InjectionService
) : ViewModel() {

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    fun connectToPlat(dappUrl: String, chainId: ChainId = ChainId.EVM_MAINNET) {
        viewModelScope.launch {
            _connectionState.value = ConnectionState.Connecting

            val result = connectDAppUseCase(
                dappUrl = dappUrl,
                chainId = chainId,
                accounts = listOf(Address(userWalletAddress))
            )

            _connectionState.value = when (result) {
                is ConnectionResult.Success -> {
                    ConnectionState.Connected(
                        dappUrl = dappUrl,
                        chainId = chainId,
                        accounts = result.accounts
                    )
                }
                is ConnectionResult.Error -> {
                    ConnectionState.Error(result.message)
                }
            }
        }
    }

    fun disconnect(dappUrl: String) {
        viewModelScope.launch {
            web3InjectionService.disconnect(dappUrl)
            _connectionState.value = ConnectionState.Disconnected
        }
    }

    fun switchChain(newChainId: ChainId) {
        viewModelScope.launch {
            when (val result = web3InjectionService.switchChain(newChainId)) {
                is SwitchChainResult.Success -> {
                    // Update UI to show new chain
                }
                is SwitchChainResult.Error -> {
                    // Show error
                }
            }
        }
    }
}

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    data class Connected(
        val dappUrl: String,
        val chainId: ChainId,
        val accounts: List<Address>
    ) : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}
```

Usage in Composable with WebView:

```kotlin
@Composable
fun DAppBrowserScreen(
    viewModel: DAppsViewModel = koinViewModel()
) {
    val connectionState by viewModel.connectionState.collectAsState()

    Column {
        // Chain selector
        ChainSelector(
            selectedChain = (connectionState as? ConnectionState.Connected)?.chainId,
            onChainSelected = { chainId ->
                viewModel.switchChain(chainId)
            }
        )

        // WebView
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true

                    // Inject Web3 provider when page loads
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            url?.let { dappUrl ->
                                viewModel.connectToPlat(dappUrl, ChainId.EVM_MAINNET)
                            }
                        }
                    }
                }
            },
            update = { webView ->
                // Inject JavaScript interface when connected
                if (connectionState is ConnectionState.Connected) {
                    val provider = viewModel.web3InjectionService.getProviderForCurrentChain()
                    // webView.addJavascriptInterface(provider.getJavaScriptInterface(), "ethereum")
                }
            }
        )

        // Connection status
        when (val state = connectionState) {
            is ConnectionState.Connecting -> {
                Text("Connecting...")
            }
            is ConnectionState.Connected -> {
                Text("Connected to ${state.chainId.value}")
                Text("Account: ${state.accounts.firstOrNull()?.value}")
            }
            is ConnectionState.Error -> {
                Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
            }
            ConnectionState.Disconnected -> {
                Text("Not connected")
            }
        }
    }
}
```

## Working with Different Chains

### Example 7: Multi-Chain Portfolio Screen

```kotlin
class PortfolioViewModel(
    private val adapterRegistry: AdapterRegistry,
    private val loadAllBalancesUseCase: LoadAllBalancesUseCase
) : ViewModel() {

    private val _balances = MutableStateFlow<Map<ChainId, List<TokenHolding>>>(emptyMap())
    val balances: StateFlow<Map<ChainId, List<TokenHolding>>> = _balances.asStateFlow()

    init {
        loadBalances()
    }

    private fun loadBalances() {
        viewModelScope.launch {
            val supportedChains = adapterRegistry.getSupportedChains()

            val balancesByChain = supportedChains.associate { chainId ->
                chainId to loadAllBalancesUseCase(chainId)
            }

            _balances.value = balancesByChain
        }
    }
}
```

Usage in Composable:

```kotlin
@Composable
fun PortfolioScreen(viewModel: PortfolioViewModel = koinViewModel()) {
    val balances by viewModel.balances.collectAsState()

    LazyColumn {
        balances.forEach { (chainId, holdings) ->
            item {
                ChainHeader(chainId = chainId)
            }
            items(holdings) { holding ->
                TokenHoldingItem(holding = holding)
            }
        }
    }
}

@Composable
fun ChainHeader(chainId: ChainId) {
    val chainName = when (chainId) {
        ChainId.EVM_MAINNET -> "Ethereum"
        ChainId.BTC_MAINNET -> "Bitcoin"
        ChainId.SOLANA_MAINNET -> "Solana"
        else -> chainId.value
    }

    Text(
        text = chainName,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(16.dp)
    )
}
```

## Manual Chain Context Management

### Example 8: Advanced - Manual Context Control

Sometimes you need fine-grained control over the chain context:

```kotlin
class AdvancedViewModel(
    private val chainContextManager: ChainContextManager,
    private val transactionService: TransactionService
) : ViewModel() {

    suspend fun compareFeesAcrossChains(
        amount: BigInteger,
        from: Address,
        to: Address
    ): Map<ChainId, FeePolicy> {
        val chains = listOf(
            ChainId.EVM_MAINNET,
            ChainId.Polygon_MAINNET,
            ChainId.Arbitrum_MAINNET
        )

        return chains.associateWith { chainId ->
            // Manually set context for each chain
            chainContextManager.setContextByChainId(chainId)

            // Get token for this chain (assuming native token)
            val token = getTokenForChain(chainId)

            // Estimate fee
            transactionService.estimateFee(from, to, amount, token)
        }
    }

    suspend fun executeBatchTransfers(
        transfers: List<TransferRequest>
    ): List<TransferResult> {
        return transfers.map { transfer ->
            // Set context for each transfer's token
            chainContextManager.setContextByToken(transfer.tokenId)

            // Execute transfer
            transactionService.executeTransfer(
                tokenId = transfer.tokenId,
                from = transfer.from,
                to = transfer.to,
                amount = transfer.amount,
                coinType = transfer.coinType,
                memo = transfer.memo
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up context when ViewModel is destroyed
        chainContextManager.clearContext()
    }
}

data class TransferRequest(
    val tokenId: TokenId,
    val from: Address,
    val to: Address,
    val amount: BigInteger,
    val coinType: CoinType,
    val memo: String?
)
```

### Example 9: Observing Chain Context Changes

```kotlin
class ChainAwareViewModel(
    private val chainContextManager: ChainContextManager
) : ViewModel() {

    val currentChain: StateFlow<ChainContext?> = chainContextManager.currentChainContext
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        // React to chain context changes
        viewModelScope.launch {
            currentChain.collect { context ->
                context?.let {
                    println("Chain context changed to: ${it.chainId.value}")
                    // Update UI, load chain-specific data, etc.
                }
            }
        }
    }
}
```

Usage in Composable:

```kotlin
@Composable
fun ChainIndicator(viewModel: ChainAwareViewModel = koinViewModel()) {
    val currentChain by viewModel.currentChain.collectAsState()

    currentChain?.let { context ->
        Surface(
            color = getChainColor(context.chainId),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getChainIcon(context.chainId),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = getChainName(context.chainId))
            }
        }
    }
}

@Composable
fun getChainColor(chainId: ChainId): Color {
    return when {
        chainId.value.startsWith("evm:") -> Color(0xFF627EEA) // Ethereum blue
        chainId.value.startsWith("btc:") -> Color(0xFFF7931A) // Bitcoin orange
        chainId.value.startsWith("solana:") -> Color(0xFF14F195) // Solana green
        else -> Color.Gray
    }
}
```

## Error Handling

### Example 10: Comprehensive Error Handling

```kotlin
class RobustSendViewModel(
    private val sendTokenUseCase: SendTokenUseCase,
    private val chainContextManager: ChainContextManager
) : ViewModel() {

    suspend fun sendWithErrorHandling(
        tokenId: TokenId,
        from: Address,
        to: Address,
        amount: BigInteger,
        coinType: CoinType
    ): SendResult {
        return try {
            // Validate chain is supported
            if (!chainContextManager.hasContext()) {
                chainContextManager.setContextByToken(tokenId)
            }

            // Execute transfer
            val result = sendTokenUseCase(
                tokenId = tokenId,
                from = from,
                to = to,
                amount = amount,
                coinType = coinType
            )

            when (result) {
                is TransferResult.Success -> {
                    SendResult.Success(result.txHash)
                }
                is TransferResult.Error -> {
                    SendResult.Failure(parseError(result.message))
                }
            }
        } catch (e: IllegalArgumentException) {
            SendResult.Failure(SendError.UnsupportedChain(e.message))
        } catch (e: IllegalStateException) {
            SendResult.Failure(SendError.InvalidState(e.message))
        } catch (e: Exception) {
            SendResult.Failure(SendError.Unknown(e.message))
        }
    }

    private fun parseError(message: String): SendError {
        return when {
            message.contains("insufficient funds", ignoreCase = true) ->
                SendError.InsufficientFunds

            message.contains("invalid address", ignoreCase = true) ->
                SendError.InvalidAddress

            message.contains("network", ignoreCase = true) ->
                SendError.NetworkError(message)

            else -> SendError.Unknown(message)
        }
    }
}

sealed class SendResult {
    data class Success(val txHash: String) : SendResult()
    data class Failure(val error: SendError) : SendResult()
}

sealed class SendError {
    object InsufficientFunds : SendError()
    object InvalidAddress : SendError()
    data class UnsupportedChain(val message: String?) : SendError()
    data class InvalidState(val message: String?) : SendError()
    data class NetworkError(val message: String?) : SendError()
    data class Unknown(val message: String?) : SendError()
}
```

## Testing

### Example 11: Testing with Mock Adapters

```kotlin
class SendTokenUseCaseTest {

    private lateinit var mockAdapter: IChainAdapter
    private lateinit var mockContextManager: ChainContextManager
    private lateinit var mockTokenRepository: TokenRepository
    private lateinit var transactionService: TransactionService
    private lateinit var sendTokenUseCase: SendTokenUseCase

    @Before
    fun setup() {
        mockAdapter = mockk()
        mockTokenRepository = mockk()

        val adapterRegistry = AdapterRegistry(
            adapters = mapOf(ChainId.EVM_MAINNET.value to mockAdapter),
            tokenRepository = mockTokenRepository
        )

        mockContextManager = ChainContextManager(adapterRegistry)
        transactionService = TransactionService(mockContextManager, mockTokenRepository)
        sendTokenUseCase = SendTokenUseCase(transactionService)
    }

    @Test
    fun `sendTokenUseCase should successfully send ETH`() = runTest {
        // Given
        val tokenId = TokenId("eth-mainnet")
        val from = Address("0xSender")
        val to = Address("0xRecipient")
        val amount = BigInteger("1000000000000000000") // 1 ETH in wei
        val coinType = CoinType.ETHEREUM

        val mockToken = Token(
            tokenId = tokenId,
            chainId = ChainId.EVM_MAINNET,
            standard = TokenStandard.NATIVE,
            contract = null,
            symbol = "ETH",
            name = "Ethereum",
            decimals = 18,
            iconUrl = null,
            enabled = true,
            sortOrder = 0,
            createdAt = 0,
            updatedAt = 0
        )

        coEvery { mockTokenRepository.getById(tokenId.value) } returns mockToken
        coEvery {
            mockAdapter.estimateTransferFee(from, to, mockToken, amount)
        } returns FeePolicy(feeAmount = BigInteger("21000000000000000")) // 0.021 ETH

        coEvery {
            mockAdapter.buildTransferTx(any(), any(), any(), any(), any(), any())
        } returns UnsignedTx(
            chainId = ChainId.EVM_MAINNET,
            from = from,
            to = to,
            tokenId = tokenId,
            amount = amount,
            fee = null
        )

        coEvery {
            mockAdapter.signAndBroadcast(any(), coinType)
        } returns "0x1234567890abcdef" // Mock tx hash

        // When
        val result = sendTokenUseCase(tokenId, from, to, amount, coinType)

        // Then
        assertTrue(result is TransferResult.Success)
        assertEquals("0x1234567890abcdef", (result as TransferResult.Success).txHash)

        coVerify {
            mockAdapter.estimateTransferFee(from, to, mockToken, amount)
            mockAdapter.buildTransferTx(from, to, mockToken, amount, any(), null)
            mockAdapter.signAndBroadcast(any(), coinType)
        }
    }
}
```

## Summary

The multi-chain domain layer provides:

1. **Automatic routing** - No need to manually select adapters
2. **Unified API** - Same code works across all chains
3. **Type safety** - Compile-time checks for chain IDs and tokens
4. **Testability** - Easy to mock and test
5. **Extensibility** - Add new chains without changing existing code

All you need to remember:
- Use **Use Cases** in ViewModels (they handle context automatically)
- Pass **TokenId** to identify which chain to use
- The domain layer handles the rest!

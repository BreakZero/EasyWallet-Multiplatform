# AGENTS.md

This file provides comprehensive context for AI coding assistants working on this project.

## Project Overview

**EasyWallet** is a decentralized cryptocurrency wallet built with Kotlin Multiplatform (KMP). It supports Android and iOS platforms with shared business logic and UI code using Compose Multiplatform.

### Core Purpose
- Manage cryptocurrency wallets via mnemonic phrases
- Support multiple blockchains (Ethereum, Bitcoin, Solana)
- View token balances and transaction history
- Send/receive tokens
- Browse blockchain news and market data

## Architecture

### Pattern: Clean Architecture + MVI/MVVM

```
UI Layer (Compose) → ViewModel → Use Cases → Domain Services → Data/Network
```

### Key Architectural Concepts

1. **Chain Adapter Pattern**: The app auto-detects which blockchain to use based on the selected token. `ChainContextManager` routes operations to the correct adapter (EVM, Bitcoin, Solana).

2. **Use Cases**: All business operations go through use cases in the domain layer. ViewModels should never directly access repositories or adapters.

3. **Reactive State**: Uses Kotlin `StateFlow` for state management. UI observes state from ViewModels.

## Module Structure

```
EasyWallet-Multiplatform/
├── composeApp/                    # Main application module
│   └── src/
│       ├── commonMain/            # Shared UI + ViewModels (Compose)
│       ├── androidMain/           # Android-specific (MainActivity, etc.)
│       └── iosMain/               # iOS-specific implementations
├── platform/                      # Shared business logic
│   ├── model/                     # Data models, entities, value classes
│   ├── domain/                    # Use cases, business rules, interfaces
│   ├── data/                      # Repository implementations, adapters
│   ├── network/                   # Ktor HTTP clients, API definitions
│   ├── database/                  # SQLDelight schemas and DAOs
│   └── datastore/                 # DataStore preferences
├── build-logic/                   # Custom Gradle convention plugins
│   └── convention/src/main/kotlin/org/easy/
│       ├── plugins/               # Plugin implementations
│       └── configs/               # Build configurations
└── iosApp/                        # iOS native container (Xcode project)
```

### Module Responsibilities

| Module | Responsibility | Key Classes |
|--------|---------------|-------------|
| `composeApp` | UI screens, ViewModels, navigation | `*Screen.kt`, `*ViewModel.kt` |
| `platform:model` | Data models shared across layers | `Token`, `Wallet`, `Transfer` |
| `platform:domain` | Business logic, use cases | `*UseCase.kt`, services |
| `platform:data` | Repository implementations | `*Repository.kt`, `*Adapter.kt` |
| `platform:network` | API clients | `*Controller.kt`, Ktor setup |
| `platform:database` | Local persistence | SQLDelight `.sq` files |
| `platform:datastore` | Preferences/settings | DataStore definitions |

## Key Technologies & Versions

| Technology | Version | Purpose |
|------------|---------|---------|
| Kotlin | 2.3.0 | Language |
| Compose Multiplatform | 1.10.0 | Shared UI framework |
| Ktor | 3.4.0 | HTTP client |
| SQLDelight | 2.2.1 | Local database |
| DataStore | 1.2.0 | Preferences storage |
| Koin | 4.1.1 | Dependency injection |
| TrustWallet Core | 4.3.21 | Blockchain operations |
| Coil | 3.3.0 | Image loading |
| Navigation Compose | 2.9.1 | Navigation |

## Code Conventions

### File Naming
- Screens: `*Screen.kt` (e.g., `AssetsScreen.kt`)
- ViewModels: `*ViewModel.kt` (e.g., `AssetsViewModel.kt`)
- Use Cases: `*UseCase.kt` (e.g., `SendTokenUseCase.kt`)
- Navigation: `*Navigation.kt` in `navigation/` subfolder
- DI Modules: `*Module.kt` in `di/` subfolder

### Package Structure for Features
```
feature/
└── featurename/
    ├── FeatureScreen.kt
    ├── FeatureViewModel.kt
    ├── FeatureState.kt          # UI state data class
    ├── FeatureEvent.kt          # One-time events (navigation, snackbar)
    ├── FeatureAction.kt         # User actions/intents (optional)
    └── navigation/
        └── FeatureNavigation.kt
```

### ViewModel Pattern
```kotlin
class ExampleViewModel(
    private val someUseCase: SomeUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExampleState())
    val uiState: StateFlow<ExampleState> = _uiState.asStateFlow()

    private val _events = Channel<ExampleEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: ExampleAction) {
        when (action) {
            is ExampleAction.Load -> load()
            // ...
        }
    }
}
```

### Compose Screen Pattern
```kotlin
@Composable
fun ExampleScreen(
    viewModel: ExampleViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    ObserveAsEvent(viewModel.events) { event ->
        when (event) {
            is ExampleEvent.NavigateBack -> onNavigateBack()
        }
    }
    
    ExampleContent(
        state = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ExampleContent(
    state: ExampleState,
    onAction: (ExampleAction) -> Unit
) {
    // UI implementation
}
```

### Dependency Injection (Koin)
- ViewModels: Register in `composeApp/.../di/ViewModelModule.kt`
- Domain: Register in `platform/domain/.../di/DomainModule.kt`
- Data: Register in `platform/data/.../di/DataModule.kt`
- Network: Register in `platform/network/.../di/NetworkModule.kt`

## Common Tasks

### Adding a New Screen
1. Create feature folder: `composeApp/src/commonMain/kotlin/org/easy/wallet/feature/newfeature/`
2. Create `NewFeatureScreen.kt`, `NewFeatureViewModel.kt`, `NewFeatureState.kt`
3. Create `navigation/NewFeatureNavigation.kt` with route and composable
4. Register ViewModel in `ViewModelModule.kt`
5. Add navigation route to `WalletNavHost.kt`

### Adding a New Use Case
1. Create in `platform/domain/src/commonMain/kotlin/org/easy/wallet/domain/usecase/`
2. Follow naming: `VerbNounUseCase.kt` (e.g., `FetchTokenPriceUseCase.kt`)
3. Register in `DomainModule.kt`
4. Inject into ViewModel

### Adding a New API Endpoint
1. Add to appropriate controller in `platform/network/src/commonMain/kotlin/org/easy/wallet/network/`
2. Create response model in `platform/model/`
3. Use in repository/adapter in `platform/data/`

### Working with Blockchain Operations
- All chain-specific logic goes through adapters implementing `IChainAdapter`
- Use `ChainContextManager` for chain detection
- Use `TransactionService` for transaction operations
- Never call adapters directly from ViewModels; use Use Cases

## Build Commands

```bash
# Build Android debug APK
./gradlew :composeApp:assembleDebug

# Build Android release APK
./gradlew :composeApp:assembleRelease

# Run ktlint check
./gradlew ktlintCheck

# Fix ktlint issues
./gradlew ktlintFormat

# Generate BuildKonfig (API keys)
./gradlew -p platform generateBuildKonfig

# Clean build
./gradlew clean

# iOS: Install pods (run from iosApp directory)
cd iosApp && pod install
```

## Configuration Files

### Required Setup
1. `configs/package_read.properties` - GitHub token for TrustWallet Core
   ```properties
   gpr.name=<github-username>
   gpr.key=<github-token>
   ```

2. `configs/apikeys.properties` - API keys
   ```properties
   etherscan=<etherscan-api-key>
   coingecko=<coingecko-api-key>
   opensea=<opensea-api-key>
   ```

## Important Files Reference

| File | Purpose |
|------|---------|
| `gradle/libs.versions.toml` | All dependency versions |
| `build-logic/convention/` | Custom Gradle plugins |
| `composeApp/src/commonMain/.../di/Koin.kt` | Main DI setup |
| `composeApp/src/commonMain/.../navhost/WalletNavHost.kt` | App navigation graph |
| `composeApp/src/commonMain/.../ui/EasyWalletApp.kt` | Root composable |
| `platform/domain/.../di/DomainModule.kt` | Domain layer DI |
| `platform/data/.../di/DataModule.kt` | Data layer DI |
| `platform/database/src/commonMain/sqldelight/` | Database schemas |

## Domain Concepts

### Key Entities
- **Wallet**: User's wallet, created from mnemonic
- **Token**: A cryptocurrency token (native or ERC-20/SPL)
- **Transfer**: A transaction record
- **ChainId**: Identifies a blockchain (e.g., "evm:1" for Ethereum mainnet)
- **TokenId**: Unique token identifier

### Chain Support
| Chain | ChainId | Status |
|-------|---------|--------|
| Ethereum | `evm:1` | Implemented |
| Polygon | `evm:137` | Partial |
| Bitcoin | `btc:main` | Skeleton |
| Solana | `solana:mainnet` | Skeleton |

## Testing

- Unit tests: `src/test/` in each module
- Use JUnit 5 for testing
- Mock dependencies with Koin test utilities

## Code Style

- Enforced by ktlint (version 1.4.0)
- Run `./gradlew ktlintFormat` before committing
- Follow Kotlin coding conventions
- Use meaningful names; avoid abbreviations
- Prefer immutable data (`val`, `data class`)

## Git Workflow

- Branch naming: `feature/feature-name`, `fix/bug-name`
- Commit messages: Clear, concise, present tense
- Pre-commit hook runs ktlint check

## Troubleshooting

### Common Issues

1. **TrustWallet Core dependency fails**: Check `configs/package_read.properties` has valid GitHub token

2. **BuildKonfig not found**: Run `./gradlew -p platform generateBuildKonfig`

3. **iOS build fails**: Ensure pods are installed (`cd iosApp && pod install`)

4. **Ktlint failures**: Run `./gradlew ktlintFormat` to auto-fix

## Resources

- [Kotlin Multiplatform Docs](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [TrustWallet Core](https://github.com/trustwallet/wallet-core)
- Architecture diagrams: `docs/ARCHITECTURE_DIAGRAMS.md`
- Usage examples: `docs/USAGE_EXAMPLES.md`

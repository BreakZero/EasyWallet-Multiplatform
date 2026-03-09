# State Management in Jetpack Compose

## State Holders

- Prefer `ViewModel` from `androidx.lifecycle` for screen-level state management.
- Use plain Kotlin classes (State Holders) for component-level state when ViewModel is not appropriate.
- Example of a component state holder:
  ```kotlin
  @Stable
  class ExpandingListState {
      var expandedItem by mutableStateOf<String?>(null)
          private set
      
      fun expand(item: String) {
          expandedItem = item
      }
      
      fun collapse() {
          expandedItem = null
      }
  }
  
  @Composable
  fun rememberExpandingListState() = remember { ExpandingListState() }
  ```

## State Flow vs LiveData

- Always prefer `StateFlow` over `LiveData` for new code.
- Expose state from ViewModels as `StateFlow`:
  ```kotlin
  class MyViewModel : ViewModel() {
      private val _uiState = MutableStateFlow(UiState())
      val uiState: StateFlow<UiState> = _uiState.asStateFlow()
  }
  ```
- Collect StateFlow in composables using `collectAsStateWithLifecycle()`:
  ```kotlin
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  ```

## Remember APIs

- Use `remember` for state that doesn't need to survive configuration changes:
  ```kotlin
  var count by remember { mutableIntStateOf(0) }
  ```
- Use `rememberSaveable` for state that must survive configuration changes:
  ```kotlin
  var text by rememberSaveable { mutableStateOf("") }
  ```
- Provide custom `Saver` for complex objects with `rememberSaveable`:
  ```kotlin
  val state = rememberSaveable(saver = MyState.Saver) {
      MyState()
  }
  ```

## Derived State

- Use `derivedStateOf` for expensive calculations that depend on other state:
  ```kotlin
  val filteredItems by remember(items, query) {
      derivedStateOf { items.filter { it.contains(query) } }
  }
  ```
- Always pass dependencies to `remember` when using `derivedStateOf`.
- Avoid using `derivedStateOf` for simple transformations that don't benefit from caching.

## Snapshot State APIs

- Use `mutableStateOf()` for observable mutable state.
- Use `mutableIntStateOf()`, `mutableFloatStateOf()`, etc. for primitive types (more efficient).
- Use `snapshotFlow { }` to convert Compose state to Flow:
  ```kotlin
  snapshotFlow { searchText }
      .debounce(300)
      .flatMapLatest { query -> search(query) }
      .collectAsStateWithLifecycle(initialValue = emptyList())
  ```

## State Hoisting

- Hoist state to the lowest common ancestor of all composables that need to read or write it.
- Pattern for state hoisting:
  ```kotlin
  // Stateless composable
  @Composable
  fun Counter(
      count: Int,
      onIncrement: () -> Unit,
      onDecrement: () -> Unit
  )
  
  // Stateful wrapper
  @Composable
  fun Counter() {
      var count by remember { mutableIntStateOf(0) }
      Counter(
          count = count,
          onIncrement = { count++ },
          onDecrement = { count-- }
      )
  }
  ```

## ViewModel Best Practices

- ViewModels should survive configuration changes but not process death (use SavedStateHandle for that).
- Initialize ViewModel state in `init` block or use `viewModelScope` for async initialization.
- Use `SavedStateHandle` for state that needs to survive process death:
  ```kotlin
  class MyViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
      private val _query = savedStateHandle.getStateFlow("query", "")
      val query: StateFlow<String> = _query
      
      fun setQuery(newQuery: String) {
          savedStateHandle["query"] = newQuery
      }
  }
  ```

## Avoiding State-Related Bugs

- Never create `MutableState` inside `remember` without a key that changes:
  ```kotlin
  // BAD - creates new state on every recomposition
  val state = mutableStateOf(0)
  
  // GOOD - state is remembered across recompositions
  val state = remember { mutableStateOf(0) }
  ```
- Don't use `rememberSaveable` for large objects or objects with heavy dependencies.
- Avoid passing `State<T>` or `MutableState<T>` directly; prefer passing `T` and lambdas.

## CompositionLocal

- Use `CompositionLocal` sparingly for values that are truly global to a subtree (theme, locale, etc.).
- Provide default values for `CompositionLocal` to avoid crashes.
- Document when a composable requires a specific `CompositionLocal` to be provided.
- Prefer explicit parameters over `CompositionLocal` for most cases.

## BackHandler

- Use `BackHandler` from `androidx.activity:activity-compose` for intercepting back button presses:
  ```kotlin
  BackHandler(enabled = showDialog) {
      showDialog = false
  }
  ```

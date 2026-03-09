# Kotlin Best Practices for Jetpack Compose

## Modern Kotlin Features

- Use Kotlin 2.0+ features when available.
- Prefer `when` expressions over `if-else` chains:
  ```kotlin
  // GOOD
  val color = when (status) {
      Status.Success -> Color.Green
      Status.Error -> Color.Red
      Status.Loading -> Color.Gray
  }
  
  // BAD
  val color = if (status == Status.Success) Color.Green
      else if (status == Status.Error) Color.Red
      else Color.Gray
  ```

## Null Safety

- Use non-nullable types by default.
- Use `?.let` for nullable operations:
  ```kotlin
  user?.let { displayUser(it) }
  ```
- Prefer `orEmpty()` for nullable collections/strings:
  ```kotlin
  val items = nullableList.orEmpty()
  ```
- Use `requireNotNull()` for early null checks with meaningful messages.

## Coroutines

- Use `viewModelScope` for ViewModel coroutines.
- Use `rememberCoroutineScope()` for composable coroutines triggered by UI:
  ```kotlin
  val scope = rememberCoroutineScope()
  Button(onClick = { scope.launch { viewModel.refresh() } }) { }
  ```
- Prefer `Flow` over `LiveData` for reactive streams.
- Use `StateFlow` for state observation.
- Use `SharedFlow` for one-time events.

## Flow Operators

- Use `map` for transformations:
  ```kotlin
  val userNameFlow = userFlow.map { it.name }
  ```
- Use `filter` for filtering:
  ```kotlin
  val activeUsersFlow = usersFlow.filter { it.isActive }
  ```
- Use `combine` for combining multiple flows:
  ```kotlin
  val combinedFlow = combine(flow1, flow2) { v1, v2 ->
      Pair(v1, v2)
  }
  ```
- Use `flatMapLatest` for search-like operations:
  ```kotlin
  searchQueryFlow
      .debounce(300)
      .flatMapLatest { query -> search(query) }
  ```

## Data Classes

- Use data classes for model objects:
  ```kotlin
  data class User(
      val id: String,
      val name: String,
      val email: String
  )
  ```
- Use `copy()` for creating modified instances:
  ```kotlin
  val updatedUser = user.copy(name = "New Name")
  ```
- Keep data classes immutable (use `val` properties).

## Sealed Classes and Interfaces

- Use sealed classes for restricted hierarchies:
  ```kotlin
  sealed class UiState {
      data object Loading : UiState()
      data class Success(val data: List<Item>) : UiState()
      data class Error(val message: String) : UiState()
  }
  ```
- Use `when` with sealed classes (exhaustive):
  ```kotlin
  when (uiState) {
      is UiState.Loading -> LoadingScreen()
      is UiState.Success -> SuccessScreen(uiState.data)
      is UiState.Error -> ErrorScreen(uiState.message)
  }
  ```

## Extension Functions

- Use extension functions for utility operations:
  ```kotlin
  fun String.capitalizeWords(): String =
      split(" ").joinToString(" ") { it.capitalize() }
  ```
- Keep extension functions focused and reusable.
- Place extensions in appropriate files (e.g., `StringExtensions.kt`).

## Type Aliases

- Use type aliases for complex types:
  ```kotlin
  typealias UserMap = Map<String, User>
  typealias ClickHandler = (String) -> Unit
  ```

## Scope Functions

- Use `let` for transformations:
  ```kotlin
  val length = text?.let { it.length } ?: 0
  ```
- Use `run` for object configuration:
  ```kotlin
  val paint = Paint().run {
      color = Color.RED
      strokeWidth = 5f
      this
  }
  ```
- Use `with` for multiple operations on an object:
  ```kotlin
  with(canvas) {
      drawLine(...)
      drawCircle(...)
  }
  ```
- Use `apply` for object initialization:
  ```kotlin
  val paint = Paint().apply {
      color = Color.RED
      strokeWidth = 5f
  }
  ```
- Use `also` for side effects:
  ```kotlin
  val list = mutableListOf<Int>().also {
      it.add(1)
      it.add(2)
  }
  ```

## Collection Operations

- Prefer immutable collections (`listOf`, `mapOf`, `setOf`).
- Use `buildList`, `buildMap` for building collections:
  ```kotlin
  val list = buildList {
      add(1)
      add(2)
      addAll(otherList)
  }
  ```
- Use `groupBy`, `associateBy`, `partition` for collection transformations.

## String Templates

- Use string templates over concatenation:
  ```kotlin
  // GOOD
  val greeting = "Hello, $name!"
  
  // BAD
  val greeting = "Hello, " + name + "!"
  ```
- Use `${}` for expressions:
  ```kotlin
  val message = "You have ${items.size} items"
  ```

## Default Arguments

- Use default arguments instead of overloads:
  ```kotlin
  fun greet(name: String, greeting: String = "Hello") { }
  ```
- Use default arguments in composables for optional parameters.

## Named Arguments

- Use named arguments for clarity:
  ```kotlin
  createUser(
      name = "John",
      email = "john@example.com",
      age = 30
  )
  ```

## Inline Functions

- Use `inline` for higher-order functions with lambdas:
  ```kotlin
  inline fun measureTime(block: () -> Unit): Long {
      val start = System.currentTimeMillis()
      block()
      return System.currentTimeMillis() - start
  }
  ```

## Contracts

- Use contracts for smart casts:
  ```kotlin
  fun isNotEmpty(list: List<*>?): Boolean {
      contract {
          returns(true) implies (list != null)
      }
      return list != null && list.isNotEmpty()
  }
  ```

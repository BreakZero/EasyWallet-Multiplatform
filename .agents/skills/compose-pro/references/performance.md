# Performance Optimization in Jetpack Compose

## Recomposition Optimization

- Use `@Stable` and `@Immutable` annotations for custom classes:
  ```kotlin
  @Immutable
  data class User(val id: String, val name: String)
  
  @Stable
  class UserState {
      var user by mutableStateOf<User?>(null)
          private set
  }
  ```
- Prefer `val` over `var` in data classes used by Compose.
- Use `remember` for expensive calculations:
  ```kotlin
  val expensiveValue = remember(input) { expensiveCalculation(input) }
  ```
- Use `derivedStateOf` for filtering/sorting that depends on state:
  ```kotlin
  val filteredItems by remember(items, query) {
      derivedStateOf { items.filter { it.matches(query) } }
  }
  ```

## Stability

- Compose uses stability to skip recompositions. Make classes stable:
  - Use `data class` with only stable properties.
  - Use `@Immutable` for classes that don't change after creation.
  - Use `@Stable` for classes with observable mutable state.
- Avoid unstable types in composable parameters:
  ```kotlin
  // BAD - List is unstable
  @Composable
  fun ItemList(items: List<Item>)
  
  // GOOD - ImmutableList is stable
  @Composable
  fun ItemList(items: ImmutableList<Item>)
  ```

## Lazy Lists Performance

- Always use `LazyColumn`/`LazyRow` for large lists instead of `Column`/`Row`.
- Use `key` parameter for stable item identification:
  ```kotlin
  LazyColumn {
      items(items, key = { it.id }) { item ->
          ItemComposable(item)
      }
  }
  ```
- Use `contentType` for heterogeneous lists:
  ```kotlin
  LazyColumn {
      items(
          items = mixedItems,
          key = { it.id },
          contentType = { it.type }
      ) { item ->
          when (item) {
              is HeaderItem -> Header(item)
              is ContentItem -> Content(item)
          }
      }
  }
  ```

## Modifier Order

- Modifier order matters for performance and behavior:
  ```kotlin
  // Padding is applied, then background, then clip
  Modifier
      .padding(16.dp)
      .background(Color.Blue)
      .clip(RoundedCornerShape(8.dp))
  ```
- Place expensive modifiers like `graphicsLayer` after cheaper ones.

## Avoiding Unnecessary Work

- Move work out of composable body when possible:
  ```kotlin
  // BAD - sorting on every recomposition
  @Composable
  fun SortedList(items: List<Item>) {
      val sorted = items.sortedBy { it.name }
      // ...
  }
  
  // GOOD - remember the sorted list
  @Composable
  fun SortedList(items: List<Item>) {
      val sorted = remember(items) { items.sortedBy { it.name } }
      // ...
  }
  ```
- Use `SideEffect` for non-Compose side effects:
  ```kotlin
  SideEffect {
      analytics.trackScreenView(screenName)
  }
  ```

## Graphics Performance

- Use `graphicsLayer` for transformations:
  ```kotlin
  Modifier.graphicsLayer {
      alpha = 0.5f
      scaleX = 1.2f
      scaleY = 1.2f
  }
  ```
- Use `Modifier.drawWithContent()` for custom drawing.
- Avoid unnecessary `Canvas` redraws by scoping state properly.

## Image Loading

- Use Coil's `AsyncImage` with proper sizing:
  ```kotlin
  AsyncImage(
      model = imageUrl,
      contentDescription = null,
      modifier = Modifier.size(48.dp),
      contentScale = ContentScale.Crop
  )
  ```
- Provide placeholder and error images.
- Use appropriate image sizes; don't load full-resolution images for thumbnails.

## State Read Optimization

- Read state as late as possible:
  ```kotlin
  // GOOD - state read inside lambda
  Box(modifier = Modifier.clickable { onClick(state.value) })
  
  // BAD - state read during composition
  val value = state.value
  Box(modifier = Modifier.clickable { onClick(value) })
  ```
- Use `snapshotFlow` to convert state to Flow:
  ```kotlin
  val flow = snapshotFlow { searchText.value }
      .debounce(300)
      .flatMapLatest { search(it) }
  ```

## Memory Management

- Don't hold references to `Context` in composables.
- Use `remember` with proper keys to avoid memory leaks.
- Clear `LazyListState` or other heavy states when no longer needed.

## Layout Inspector

- Use Layout Inspector in Android Studio to debug recompositions.
- Look for unexpected recompositions in the Recomposition Counts overlay.
- Enable "Show recompositions" in Compose Preview.

## Baseline Profiles

- Include baseline profile rules for Compose libraries:
  ```
  // In baseline-prof.txt
  Landroidx/compose/runtime/ComposerKt;
  Landroidx/compose/ui/platform/ComposeView;
  ```
- Generate app-specific baseline profiles using Macrobenchmark.

## Testing Performance

- Use Macrobenchmark for measuring Compose performance:
  ```kotlin
  @RunWith(AndroidJUnit4::class)
  class StartupBenchmark {
      @get:Rule
      val benchmarkRule = MacrobenchmarkRule()
      
      @Test
      fun startup() = benchmarkRule.measureRepeated(
          packageName = "com.example.app",
          metrics = listOf(StartupTimingMetric()),
          iterations = 5,
          startupMode = StartupMode.COLD
      ) {
          pressHome()
          startActivityAndWait()
      }
  }
  ```

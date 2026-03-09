# Jetpack Compose Components

## Composable Structure

- Strongly prefer to avoid breaking up composables using private composable functions or extension functions that return `Unit`. Extract them into separate `@Composable` functions instead, placing each into its own file when appropriate.
- Flag `@Composable` functions that are excessively long (more than ~100 lines); they should be broken into extracted subcomposables.
- Button actions should be extracted from composable bodies into separate handler methods or ViewModel methods, to avoid mixing layout and logic.
- Similarly, general business logic should not live inline in `LaunchedEffect`, `DisposableEffect`, or elsewhere in the composable body.
- Prefer to place business logic into ViewModels or similar, so it can be tested.
- Each major UI component should be in its own Kotlin file. Flag files containing multiple unrelated composable definitions.
- Unless a full-screen editing experience is required, prefer using `OutlinedTextField` or `TextField` with appropriate configuration over custom input implementations.
- If a button action can be provided directly as an `onClick` parameter, do so. For example:
  ```kotlin
  Button(onClick = viewModel::onSubmit) { Text("Submit") }
  ```
  is preferred over
  ```kotlin
  Button(onClick = { viewModel.onSubmit() }) { Text("Submit") }
  ```

## Slot APIs

- When creating reusable components, prefer slot-based APIs (composable lambda parameters) over fixed content parameters.
- Example of good slot API:
  ```kotlin
  @Composable
  fun CustomCard(
      modifier: Modifier = Modifier,
      header: @Composable () -> Unit = {},
      content: @Composable () -> Unit
  )
  ```
- Provide sensible defaults for optional slots.

## Preview Best Practices

- `@Preview` should be used for all significant UI components.
- Place previews in the same file as the composable they preview, or in a dedicated `*Preview.kt` file.
- Use `@PreviewParameter` for previewing composables with different data states.
- Preview names should be descriptive: `@Preview(name = "With Long Text")`.

## Recomposition Optimization

- Use `Stable` and `Immutable` annotations from `androidx.compose.runtime` for custom classes to help the compiler optimize recompositions.
- Prefer `data class` with `@Immutable` for model classes used in Compose.
- Avoid using `var` properties in classes that are read by composables; prefer `val` with copy-on-write patterns.

## Animating Composables

- Strongly prefer to use `animate*AsState()` APIs over manual `Animatable` management for simple value animations.
- Use `AnimatedVisibility` for show/hide animations:
  ```kotlin
  AnimatedVisibility(visible = isVisible) {
      Content()
  }
  ```
- Use `AnimatedContent` for content swap animations:
  ```kotlin
  AnimatedContent(targetState = selectedTab) { tab ->
      when (tab) { ... }
  }
  ```
- Never use `LaunchedEffect` with manual delay loops for animations; use proper animation APIs.
- Chaining animations should be done using `animate*AsState()` with `animationSpec` or `updateTransition`.

## Lazy Lists

- Prefer `LazyColumn` and `LazyRow` over `Column` and `Row` with scroll modifiers for large datasets.
- Use `items()` with `key` parameter for stable item identification:
  ```kotlin
  LazyColumn {
      items(items, key = { it.id }) { item ->
          ItemComposable(item)
      }
  }
  ```
- Use `contentType` parameter in `items()` for heterogeneous lists to improve composition performance.
- Prefer `LazyVerticalGrid` and `LazyHorizontalGrid` over custom grid implementations.
- Use `rememberLazyListState()` for controlling scroll position and observing scroll state.

## Custom Layouts

- Use `Layout` composable for custom layouts only when standard layouts don't suffice.
- Prefer `SubcomposeLayout` for layouts that need to measure children conditionally.
- Consider `Modifier.layout()` for simple single-child layout modifications.

## Theme and Styling

- Always use `MaterialTheme` for accessing colors, typography, and shapes.
- Create custom design systems by wrapping Material 3 theme or creating a custom theme.
- Use `CompositionLocalProvider` sparingly for theme-like values that need to be passed down the tree.
- Prefer `LocalContentColor`, `LocalTextStyle`, and other built-in composition locals over custom ones when possible.

## Input Handling

- Use `Modifier.pointerInput()` for custom gesture detection.
- Prefer `detectTapGestures`, `detectDragGestures`, `detectTransformGestures` over manual pointer event handling.
- Use `Modifier.clickable()` with proper semantics for clickable elements.
- For draggable items, use `Modifier.draggable()` or `Modifier.dragAndDropSource()`/`Modifier.dragAndDropTarget()`.

## Side Effects

- Use `LaunchedEffect` for side effects that need to run when a key changes.
- Use `DisposableEffect` for side effects that need cleanup.
- Use `SideEffect` for side effects that don't need to be scoped to a key change.
- Use `rememberCoroutineScope()` for launching coroutines in response to user interactions.
- Never use `LaunchedEffect(Unit)` with infinite loops without proper cancellation handling.

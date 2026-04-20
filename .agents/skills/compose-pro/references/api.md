# Using modern Jetpack Compose API

## Material Design

- Always use Material 3 (`androidx.compose.material3`) for new projects. Material 2 (`androidx.compose.material`) is deprecated for new development.
- Use `androidx.compose.material3:material3` instead of the older Material 2 dependencies.
- Prefer `FilledTonalButton`, `ElevatedButton`, and `OutlinedButton` over the legacy `Button` variants when appropriate for the UI hierarchy.

## Modifier Best Practices

- Always use `Modifier.fillMaxWidth()` instead of `Modifier.width(Int.MAX_VALUE.dp)`.
- Always use `Modifier.fillMaxHeight()` instead of `Modifier.height(Int.MAX_VALUE.dp)`.
- Always use `Modifier.fillMaxSize()` instead of setting both width and height to maximum.
- Prefer `Modifier.clip(RoundedCornerShape(8.dp))` over the older `Modifier.background(shape = ...)` for clipping.
- Use `Modifier.then()` sparingly and only when conditionally chaining modifiers dynamically.

## State APIs

- Never use `rememberSaveable()` without a custom `Saver` for complex objects that aren't Parcelable/Serializable.
- Prefer `rememberSaveable()` over `remember()` for state that must survive configuration changes.
- Use `collectAsStateWithLifecycle()` from `androidx.lifecycle:lifecycle-runtime-compose` instead of `collectAsState()` when collecting flows in composables.
- Never use the `LiveData.observeAsState()` directly in composables; convert to StateFlow first.

## Animation APIs

- Prefer `animate*AsState()` (e.g., `animateFloatAsState`, `animateColorAsState`) over manual `Animatable` usage for simple animations.
- Use `AnimatedContent` for content swap animations rather than manual crossfade implementations.
- Use `AnimatedVisibility` for show/hide animations instead of manual alpha animations.
- Prefer `Crossfade` for simple crossfade between two composables.
- Use `updateTransition()` for coordinating multiple animations together.

## Text and Typography

- Always use `TextStyle` from Material 3 theme (`MaterialTheme.typography`) instead of hardcoded text styles.
- Use `buildAnnotatedString` and `AnnotatedString` for complex text with multiple spans.
- Prefer `TextOverflow.Ellipsis` and `TextOverflow.Clip` over custom truncation logic.
- Use `LineHeightStyle` and `TextAlign` for proper text alignment.

## Image Loading

- Prefer `AsyncImage` from Coil (`io.coil-kt:coil-compose`) over custom image loading implementations.
- Use `rememberAsyncImagePainter` only when you need more control over the image loading process.
- Always provide a `placeholder` and `error` parameter for `AsyncImage`.

## Window Insets

- Use `WindowInsets` APIs (`androidx.compose.foundation:layout`) instead of the deprecated `Accompanist Insets` library.
- Prefer `WindowInsets.statusBars`, `WindowInsets.navigationBars`, and `WindowInsets.ime` over manual calculations.
- Use `Modifier.windowInsetsPadding()` and `Modifier.consumeWindowInsets()` appropriately.

## Flow APIs

- Use `FlowRow` and `FlowColumn` from `androidx.compose.foundation:foundation-layout` for flowing layouts instead of custom implementations.
- Prefer `HorizontalPager` and `VerticalPager` from Accompanist or official Foundation for paging.

## Deprecated Patterns to Avoid

- Never use `ScaffoldState` with `rememberScaffoldState()`; use individual state holders for drawer, snackbar, etc.
- Avoid `ModalBottomSheetLayout` from Material 2; use `ModalBottomSheet` from Material 3.
- Do not use `SwipeToDismiss` from Material 2; use the version from Material 3 or custom implementation.
- Avoid `BackdropScaffold` from Material 2; implement custom backdrop behavior if needed.

## Preview Annotations

- Always use `@Preview` from `androidx.compose.ui.tooling.preview` for previews.
- Use `@PreviewScreenSizes`, `@PreviewFontScale`, `@PreviewLightDark`, and `@PreviewDynamicColors` for comprehensive preview coverage.
- Prefer multiple `@Preview` annotations with different configurations over a single preview.

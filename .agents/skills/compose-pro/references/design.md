# Material Design Guidelines for Jetpack Compose

## Material 3 Design System

- Always use Material 3 components and theme for new projects.
- Follow Material 3 color system with `ColorScheme`:
  ```kotlin
  val LightColorScheme = lightColorScheme(
      primary = md_theme_light_primary,
      onPrimary = md_theme_light_onPrimary,
      primaryContainer = md_theme_light_primaryContainer,
      // ... other colors
  )
  ```
- Use Material 3 typography scale:
  ```kotlin
  val Typography = Typography(
      displayLarge = baseline.displayLarge.copy(fontFamily = appFontFamily),
      displayMedium = baseline.displayMedium.copy(fontFamily = appFontFamily),
      // ... other text styles
  )
  ```

## Color Usage

- Never hardcode colors; always use theme colors:
  ```kotlin
  // GOOD
  Text(text = "Hello", color = MaterialTheme.colorScheme.primary)
  
  // BAD
  Text(text = "Hello", color = Color(0xFF4285F4))
  ```
- Use `contentColorFor()` for automatic content color selection:
  ```kotlin
  Surface(color = MaterialTheme.colorScheme.primary) {
      Text(text = "Hello") // Automatically uses onPrimary
  }
  ```
- Use `LocalContentColor` for text that should adapt to parent surface color.

## Elevation and Shadows

- Use `Surface` composable for elevated surfaces:
  ```kotlin
  Surface(
      tonalElevation = 3.dp,
      shadowElevation = 3.dp,
      shape = RoundedCornerShape(12.dp)
  ) {
      Content()
  }
  ```
- Prefer `tonalElevation` over `shadowElevation` for Material 3 elevation.
- Use `Modifier.shadow()` sparingly; prefer Surface elevation.

## Spacing and Layout

- Use consistent spacing values (4.dp grid system):
  ```kotlin
  object Spacing {
      val xs = 4.dp
      val sm = 8.dp
      val md = 16.dp
      val lg = 24.dp
      val xl = 32.dp
  }
  ```
- Use `Arrangement.spacedBy()` for consistent spacing in rows/columns:
  ```kotlin
  Column(verticalArrangement = Arrangement.spacedBy(16.dp)) { ... }
  ```

## Shape System

- Use theme shapes for consistent corner radii:
  ```kotlin
  MaterialTheme.shapes.small   // 4.dp
  MaterialTheme.shapes.medium  // 8.dp
  MaterialTheme.shapes.large   // 12.dp
  ```
- Apply shapes consistently to cards, buttons, and text fields.

## Iconography

- Use Material Icons Extended for comprehensive icon set:
  ```kotlin
  implementation "androidx.compose.material:material-icons-extended"
  ```
- Use appropriate icon sizes:
  - Small: 16.dp
  - Default: 24.dp
  - Large: 36.dp
  - Extra Large: 48.dp

## Component Usage

### Buttons

- Use `Button` for primary actions.
- Use `FilledTonalButton` for secondary actions.
- Use `OutlinedButton` for tertiary actions.
- Use `TextButton` for low-emphasis actions.
- Use `ElevatedButton` for actions that need elevation.

### Cards

- Use `Card` for grouped content.
- Use `ElevatedCard` for cards that need more emphasis.
- Use `OutlinedCard` for subtle card boundaries.

### Text Fields

- Use `OutlinedTextField` as the default text input.
- Use `TextField` (filled) when it fits the design better.
- Always provide proper labels and supporting text.

### Chips

- Use `AssistChip` for actions.
- Use `FilterChip` for filtering.
- Use `InputChip` for user input.
- Use `SuggestionChip` for suggestions.

## Responsive Design

- Use `WindowSizeClass` for responsive layouts:
  ```kotlin
  val windowSizeClass = calculateWindowSizeClass(activity)
  when (windowSizeClass.widthSizeClass) {
      WindowWidthSizeClass.Compact -> CompactLayout()
      WindowWidthSizeClass.Medium -> MediumLayout()
      WindowWidthSizeClass.Expanded -> ExpandedLayout()
  }
  ```
- Use `BoxWithConstraints` for constraint-based responsive layouts.

## Dark Theme

- Always provide dark theme support:
  ```kotlin
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  ```
- Test UI in both light and dark themes.
- Use `isSystemInDarkTheme()` to follow system theme.

## Motion and Animation

- Follow Material motion guidelines:
  - Use `MotionTheme` for consistent motion.
  - Standard duration: 300ms for medium transitions.
  - Use `FastOutSlowInEasing` for standard transitions.
- Use shared element transitions for element-to-element navigation.

## Content Description

- Always provide content descriptions for icons and images.
- Use `null` for decorative images that should be ignored by TalkBack.
- Mark purely decorative elements with `Modifier.semantics { invisibleToUser() }`.

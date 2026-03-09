# Accessibility in Jetpack Compose

## TalkBack Support

- Always provide `contentDescription` for icons and images:
  ```kotlin
  Icon(
      imageVector = Icons.Default.Add,
      contentDescription = "Add new item"
  )
  ```
- Use `contentDescription = null` for decorative images:
  ```kotlin
  Image(
      painter = painterResource(R.drawable.decoration),
      contentDescription = null
  )
  ```
- For icon-only buttons, the content description is critical:
  ```kotlin
  IconButton(
      onClick = { /* action */ },
      modifier = Modifier.semantics { 
          contentDescription = "Close dialog"
      }
  ) {
      Icon(Icons.Default.Close, contentDescription = null)
  }
  ```

## Semantic Properties

- Use `Modifier.semantics()` to provide accessibility information:
  ```kotlin
  Box(
      modifier = Modifier.semantics {
          contentDescription = "User profile picture"
          stateDescription = if (isOnline) "Online" else "Offline"
      }
  )
  ```
- Use `heading()` for headings:
  ```kotlin
  Text(
      text = "Section Title",
      modifier = Modifier.semantics { heading() },
      style = MaterialTheme.typography.headlineSmall
  )
  ```
- Use `liveRegion` for dynamic content announcements:
  ```kotlin
  Text(
      text = statusMessage,
      modifier = Modifier.semantics { liveRegion = LiveRegion.Assertive }
  )
  ```

## Touch Targets

- Ensure minimum touch target size of 48x48dp:
  ```kotlin
  IconButton(
      modifier = Modifier.minimumInteractiveComponentSize(),
      onClick = { }
  ) { ... }
  ```
- Use `minimumInteractiveComponentSize()` for small interactive elements.

## Font Scaling

- Use `sp` units for all text sizes (handled automatically by Compose).
- Test layouts with font sizes up to 200%.
- Avoid fixed heights for text containers; use `wrapContentHeight()` or `intrinsic measurements`.
- Use `TextOverflow.Ellipsis` with `maxLines` for text that might overflow:
  ```kotlin
  Text(
      text = longText,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis
  )
  ```

## Color Contrast

- Ensure minimum contrast ratio of 4.5:1 for normal text.
- Ensure minimum contrast ratio of 3:1 for large text (18sp+ or 14sp+ bold).
- Use `MaterialTheme.colorScheme` colors which are designed for accessibility.
- Test with color blindness simulators.

## Reduce Motion

- Respect `LocalView` animation scale settings:
  ```kotlin
  val view = LocalView.current
  val animationScale = Settings.Global.getFloat(
      view.context.contentResolver,
      Settings.Global.ANIMATOR_DURATION_SCALE,
      1f
  )
  ```
- Or use `withInfiniteAnimationFrameMillis` with proper checks.
- Provide instant alternatives for animations when reduce motion is enabled.

## Keyboard Navigation

- Ensure all interactive elements are focusable:
  ```kotlin
  Modifier.focusable()
  ```
- Use `Modifier.focusProperties()` for custom focus behavior:
  ```kotlin
  Modifier.focusProperties {
      canFocus = false
  }
  ```
- Provide visual focus indicators using `LocalIndication`.

## Screen Reader Optimization

- Group related content with `semantics(mergeDescendants = true)`:
  ```kotlin
  Row(
      modifier = Modifier.semantics(mergeDescendants = true) {
          contentDescription = "$username, $status"
      }
  ) {
      Text(username)
      Text(status)
  }
  ```
- Hide decorative elements from screen readers:
  ```kotlin
  Modifier.clearAndSetSemantics { }
  ```

## Custom Actions

- Provide custom actions for complex components:
  ```kotlin
  Modifier.semantics {
      customActions = listOf(
          CustomAccessibilityAction("Mark as read") { /* action */ },
          CustomAccessibilityAction("Delete") { /* action */ }
      )
  }
  ```

## Testing Accessibility

- Use TalkBack to test actual screen reader experience.
- Use Accessibility Scanner app for automated checks.
- Test with font scaled to maximum.
- Test with display size set to largest.
- Test with color inversion enabled.

## Accessibility in Previews

- Add accessibility-focused previews:
  ```kotlin
  @Preview(fontScale = 2.0f)
  @Composable
  fun PreviewLargeFont() { ... }
  ```

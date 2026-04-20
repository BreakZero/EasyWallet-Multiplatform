# Code Hygiene for Jetpack Compose Projects

## Project Structure

- Organize by feature, not by layer:
  ```
  app/src/main/java/com/example/app/
  ├── feature/
  │   ├── home/
  │   │   ├── HomeScreen.kt
  │   │   ├── HomeViewModel.kt
  │   │   ├── HomeUiState.kt
  │   │   └── components/
  │   │       ├── HomeHeader.kt
  │   │       └── HomeContent.kt
  │   └── profile/
  ├── core/
  │   ├── ui/
  │   ├── data/
  │   └── domain/
  └── MainActivity.kt
  ```
- Keep related files close together.
- Use `internal` visibility for feature-internal code.

## Naming Conventions

- Composable functions use PascalCase:
  ```kotlin
  @Composable
  fun UserProfile(user: User) { }
  ```
- Preview functions use `Preview` suffix:
  ```kotlin
  @Preview
  @Composable
  fun UserProfilePreview() { }
  ```
- State holder classes use `State` or `ViewModel` suffix:
  ```kotlin
  class HomeViewModel : ViewModel() { }
  class ExpandingCardState { }
  ```
- UI state classes use `UiState` suffix:
  ```kotlin
  data class HomeUiState(
      val isLoading: Boolean = false,
      val items: List<Item> = emptyList()
  )
  ```

## File Organization

- One major composable per file.
- Related small composables can be in the same file.
- Place previews at the bottom of the file or in a separate `*Preview.kt` file.

## Imports

- Use wildcard imports for Compose:
  ```kotlin
  import androidx.compose.material3.*
  import androidx.compose.foundation.layout.*
  ```
- Remove unused imports.
- Use explicit imports for conflicting names.

## Documentation

- Document public APIs with KDoc:
  ```kotlin
  /**
   * Displays a user profile card with avatar, name, and bio.
   *
   * @param user The user to display
   * @param onClick Called when the card is clicked
   * @param modifier Modifier to be applied to the card
   */
  @Composable
  fun UserProfileCard(
      user: User,
      onClick: () -> Unit,
      modifier: Modifier = Modifier
  )
  ```
- Document complex business logic with inline comments.
- Use `// TODO:` for temporary code with issue tracking reference.

## Secrets Management

- Never commit API keys or secrets to version control.
- Use `local.properties` for local secrets:
  ```properties
  API_KEY=your_api_key_here
  ```
- Use environment variables or CI/CD secrets for production.
- Access secrets via BuildConfig:
  ```kotlin
  val apiKey = BuildConfig.API_KEY
  ```

## Static Analysis

- Configure Detekt for Kotlin static analysis:
  ```kotlin
  // detekt-config.yml
  build:
    maxIssues: 0
  ```
- Configure KtLint for code formatting:
  ```kotlin
  // .editorconfig
  [*.{kt,kts}]
  indent_size = 4
  max_line_length = 120
  ```
- Run static analysis in CI/CD pipeline.
- Fix all warnings before merging.

## Testing

- Unit tests for ViewModels and business logic:
  ```kotlin
  @Test
  fun `when refresh, then loading state is emitted`() = runTest {
      val viewModel = HomeViewModel()
      viewModel.refresh()
      
      assertEquals(UiState.Loading, viewModel.uiState.value)
  }
  ```
- UI tests for critical user flows:
  ```kotlin
  @Test
  fun userCanNavigateToDetails() {
      composeTestRule.setContent { App() }
      
      composeTestRule.onNodeWithText("Item 1").performClick()
      composeTestRule.onNodeWithText("Details").assertIsDisplayed()
  }
  ```
- Screenshot tests for UI regression:
  ```kotlin
  @Test
  fun testHomeScreen() {
      composeTestRule.setContent { HomeScreen() }
      compareScreenshot(composeTestRule)
  }
  ```

## Version Catalog

- Use Gradle Version Catalogs for dependency management:
  ```toml
  # libs.versions.toml
  [versions]
  compose-bom = "2024.02.00"
  
  [libraries]
  compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
  compose-ui = { group = "androidx.compose.ui", name = "ui" }
  ```

## ProGuard/R8

- Configure ProGuard rules for Compose:
  ```proguard
  # Keep Compose-related classes
  -keep class androidx.compose.** { *; }
  -keepclassmembers class * {
      @androidx.compose.ui.tooling.preview.Preview <methods>;
  }
  ```
- Test release builds with minification enabled.

## Build Configuration

- Use `buildConfigField` for build-specific values:
  ```kotlin
  buildConfigField("String", "API_URL", "\"https://api.example.com\"")
  ```
- Use `resValue` for resource values:
  ```kotlin
  resValue("string", "app_name", "MyApp")
  ```
- Use `buildTypes` for debug/release configurations:
  ```kotlin
  buildTypes {
      debug {
          isDebuggable = true
          applicationIdSuffix = ".debug"
      }
      release {
          isMinifyEnabled = true
          proguardFiles(...)
      }
  }
  ```

## CI/CD

- Run tests on every pull request.
- Build release artifacts on tag creation.
- Use code coverage tools (JaCoCo, Kover).
- Automate Play Store deployment.

## Dependency Updates

- Use Dependabot or Renovate for automated dependency updates.
- Review changelogs before updating major versions.
- Test thoroughly after updating Compose BOM.

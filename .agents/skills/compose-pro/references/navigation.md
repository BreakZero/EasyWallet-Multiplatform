# Navigation in Jetpack Compose

## Navigation Component

- Use Jetpack Navigation Compose (`androidx.navigation:navigation-compose`) for navigation.
- Prefer type-safe navigation with Kotlin Serialization (Navigation 2.8.0+):
  ```kotlin
  @Serializable
  data class Profile(val userId: String)
  
  @Serializable
  object Home
  
  // In NavHost
  composable<Home> { ... }
  composable<Profile> { backStackEntry ->
      val profile: Profile = backStackEntry.toRoute()
      ProfileScreen(profile.userId)
  }
  ```
- For older versions, use string-based routes with proper argument handling.

## NavHost Setup

- Define navigation graph in a single `NavHost` when possible:
  ```kotlin
  NavHost(
      navController = navController,
      startDestination = Home
  ) {
      composable<Home> { HomeScreen() }
      composable<Profile> { entry ->
          ProfileScreen(entry.toRoute<Profile>().userId)
      }
  }
  ```
- Use nested navigation graphs for feature-based navigation:
  ```kotlin
  navigation<AuthGraph>(startDestination = Login) {
      composable<Login> { LoginScreen() }
      composable<Register> { RegisterScreen() }
  }
  ```

## Navigation Actions

- Navigate using type-safe routes:
  ```kotlin
  navController.navigate(Profile(userId = "123"))
  ```
- Use `popUpTo` with `inclusive` for clearing back stack:
  ```kotlin
  navController.navigate(Home) {
      popUpTo(Login) { inclusive = true }
  }
  ```
- Use `launchSingleTop` to avoid duplicate destinations:
  ```kotlin
  navController.navigate(Details) {
      launchSingleTop = true
  }
  ```

## Deep Links

- Define deep links in composable declarations:
  ```kotlin
  composable<Profile>(
      deepLinks = listOf(
          navDeepLink<Profile>(basePath = "$URI/profile/{userId}")
      )
  ) { ... }
  ```
- Handle deep links in `AndroidManifest.xml`:
  ```xml
  <intent-filter>
      <action android:name="android.intent.action.VIEW" />
      <category android:name="android.intent.category.DEFAULT" />
      <category android:name="android.intent.category.BROWSABLE" />
      <data android:scheme="https" android:host="example.com" />
  </intent-filter>
  ```

## Bottom Navigation

- Use `NavigationBar` (Material 3) with `NavHost` for bottom navigation:
  ```kotlin
  var selectedItem by rememberSaveable { mutableIntStateOf(0) }
  val items = listOf(Screen.Home, Screen.Search, Screen.Profile)
  
  Scaffold(
      bottomBar = {
          NavigationBar {
              items.forEachIndexed { index, screen ->
                  NavigationBarItem(
                      icon = { Icon(screen.icon, contentDescription = screen.label) },
                      label = { Text(screen.label) },
                      selected = selectedItem == index,
                      onClick = { selectedItem = index }
                  )
              }
          }
      }
  ) { padding ->
      content()
  }
  ```
- Preserve navigation state per tab using multiple back stacks (Navigation 2.4.0+).

## Navigation with ViewModel

- Pass navigation actions as lambdas to screens, don't pass NavController to ViewModels:
  ```kotlin
  // In ViewModel
  private val _navigationEvents = Channel<NavigationEvent>()
  val navigationEvents = _navigationEvents.receiveAsFlow()
  
  fun onItemClick(itemId: String) {
      _navigationEvents.trySend(NavigationEvent.ToDetails(itemId))
  }
  
  // In Composable
  LaunchedEffect(Unit) {
      viewModel.navigationEvents.collect { event ->
          when (event) {
              is NavigationEvent.ToDetails -> navController.navigate(Details(event.itemId))
          }
      }
  }
  ```

## Dialogs and Bottom Sheets

- Use `dialog()` in NavHost for dialog destinations:
  ```kotlin
  dialog<ConfirmationDialog> { ConfirmationDialogContent() }
  ```
- Use `bottomSheet()` from navigation-material for bottom sheet destinations:
  ```kotlin
  bottomSheet<FilterSheet> { FilterSheetContent() }
  ```
- For simple dialogs, prefer managing state locally rather than navigation:
  ```kotlin
  var showDialog by remember { mutableStateOf(false) }
  if (showDialog) {
      AlertDialog(onDismissRequest = { showDialog = false }, ...)
  }
  ```

## Navigation State

- Observe current destination:
  ```kotlin
  val currentBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = currentBackStackEntry?.destination
  ```
- Check if can navigate back:
  ```kotlin
  val canPop by navController.previousBackStackEntry.collectAsStateWithLifecycle(null)
  // canPop != null means there's a previous destination
  ```

## Result Handling

- Use SavedStateHandle for returning results:
  ```kotlin
  // In destination A
  navController.currentBackStackEntry
      ?.savedStateHandle
      ?.set("result_key", result)
  navController.popBackStack()
  
  // In destination B
  val result = navController.currentBackStackEntry
      ?.savedStateHandle
      ?.get<String>("result_key")
  ```
- For more complex scenarios, use shared ViewModel or event bus pattern.

## Testing Navigation

- Test navigation logic separately from UI:
  ```kotlin
  @Test
  fun whenLoginSuccess_navigateToHome() = runTest {
      val viewModel = LoginViewModel()
      viewModel.onLoginSuccess()
      
      assertEquals(NavigationEvent.ToHome, viewModel.navigationEvents.first())
  }
  ```
- Use `createComposeRule()` with `TestNavHostController` for UI navigation tests.

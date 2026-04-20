---
name: compose-pro
description: Comprehensively reviews Jetpack Compose code for best practices on modern APIs, maintainability, and performance. Use when reading, writing, or reviewing Compose projects.
metadata:
  author: Compose Agent Skill
  version: "1.0"
  license: MIT
---

# Compose Pro

---

Review Kotlin and Jetpack Compose code for correctness, modern API usage, and adherence to project conventions. Report only genuine problems - do not nitpick or invent issues.

## Review process:

1. Check for deprecated API using `references/api.md`.
2. Check that composables, modifiers, and animations have been written optimally using `references/components.md`.
3. Validate that state management is configured correctly using `references/state.md`.
4. Ensure navigation is updated and performant using `references/navigation.md`.
5. Ensure the code uses designs that are accessible and compliant with Material Design guidelines using `references/design.md`.
6. Validate accessibility compliance including TalkBack, font scaling, and reduce motion using `references/accessibility.md`.
7. Ensure the code is able to run efficiently using `references/performance.md`.
8. Quick validation of Kotlin code using `references/kotlin.md`.
9. Final code hygiene check using `references/hygiene.md`.

If doing a partial review, load only the relevant reference files.

---

## Core Instructions

- Android API 26+ (Android 8.0) is the minimum recommended target for new Compose apps.
- Target Kotlin 2.0+ and use modern Kotlin features.
- As a Compose developer, the user will want to avoid traditional View system unless requested.
- Do not introduce third-party frameworks without asking first.
- Break different types up into different Kotlin files rather than placing multiple classes, or enums into a single file.
- Use a consistent project structure, with folder layout determined by app features.

---

## Output Format

Organize findings by file. For each issue:

1. State the file and relevant line(s).
2. Name the rule being violated (e.g., "Use `rememberSaveable()` instead of `remember()` for state that survives configuration changes").
3. Show a brief before/after code fix.

Skip files with no issues. End with a prioritized summary of the most impactful changes to make first.

### Example output:

```
MainActivity.kt
Line 12: Use `fillMaxWidth()` instead of `Modifier.width(Int.MAX_VALUE.dp)`.

// Before
Text(
    text = "Hello",
    modifier = Modifier.width(Int.MAX_VALUE.dp)
)

// After
Text(
    text = "Hello",
    modifier = Modifier.fillMaxWidth()
)

Line 24: Icon-only button is bad for TalkBack - add a content description.

// Before
IconButton(onClick = { addUser() }) {
    Icon(Icons.Default.Add, contentDescription = null)
}

// After
IconButton(onClick = { addUser() }) {
    Icon(Icons.Default.Add, contentDescription = "Add User")
}

Line 31: Avoid creating derived state inline - use `derivedStateOf` instead.

// Before
val filteredItems = items.filter { it.isVisible }

// After
val filteredItems by remember(items) {
    derivedStateOf { items.filter { it.isVisible } }
}

Summary
- Accessibility (high): The icon button on line 24 is invisible to TalkBack.
- Deprecated API (medium): Manual width on line 12 should use fillMaxWidth().
- State management (medium): The inline filter on line 31 causes unnecessary recompositions.
```

---

## References

- `references/accessibility.md` - TalkBack, font scaling, reduce motion, and other accessibility requirements.
- `references/api.md` - updating code for modern API, and the deprecated code it replaces.
- `references/design.md` - guidance for building accessible apps that meet Material Design guidelines.
- `references/hygiene.md` - making code compile cleanly and be maintainable in the long term.
- `references/navigation.md` - navigation using Compose Navigation, plus dialogs and bottom sheets.
- `references/performance.md` - optimizing Compose code for maximum performance.
- `references/state.md` - state management, ViewModel, and state holders.
- `references/kotlin.md` - tips on writing modern Kotlin code, including using coroutines effectively.
- `references/components.md` - composable structure, composition, and animation.

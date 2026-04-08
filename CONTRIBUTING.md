# Contributing to Multi App Uninstaller

Hey there! Thanks for wanting to contribute to Multi App Uninstaller. Whether it's a bug fix, new feature, or just a typo correction - every contribution helps make this app better for everyone.

## Ways to Contribute

### Found a Bug?

1. Search [existing issues](https://github.com/ahmmedrejowan/MultiAppUninstaller/issues) first - maybe it's already reported
2. If not, open a new issue using the Bug Report template
3. The more details you provide, the easier it is to fix!

### Have an Idea?

We love hearing new ideas! Share them in [Discussions](https://github.com/ahmmedrejowan/MultiAppUninstaller/discussions/categories/ideas) or open a Feature Request issue.

### Want to Code?

Awesome! Here's how:

1. Fork the repo
2. Create a branch: `git checkout -b feature/your-feature`
3. Make your changes
4. Test it works
5. Open a Pull Request

Don't worry about getting everything perfect - we can work through it together in the PR.

## Setting Up Locally

**You'll need:**
- Android Studio (Ladybug or newer)
- JDK 17

**Quick start:**
```bash
git clone https://github.com/ahmmedrejowan/MultiAppUninstaller.git
cd MultiAppUninstaller
./gradlew assembleDebug
```

## Code Style

We try to keep things consistent:

- **Kotlin** - Follow standard [Kotlin conventions](https://kotlinlang.org/docs/coding-conventions.html)
- **Compose** - Use `remember`, proper state hoisting, keep composables small
- **Architecture** - MVVM with Repository pattern

For commits, we use short descriptive messages like:
- `add sort bottom sheet`
- `fix ripple clipping on card`
- `redesign app details dialog`

But don't stress too much about this - we can always squash and clean up commits later.

## Project Structure

```
app/src/main/java/com/rejowan/multiappuninstaller/
├── data/                # DataStore helpers
├── di/                  # Koin DI modules
├── feature/
│   └── components/      # Dialogs, bottom sheets, shared components
├── presentation/
│   ├── home/            # Home screen + components
│   └── settings/        # Settings screen + components
├── receivers/           # Broadcast receivers
├── repo/                # Repository interface
├── repoImpl/            # Repository implementation
├── ui/
│   └── theme/           # Color, Theme, Typography
└── utils/               # Utilities
```

## Key Technologies

- **UI:** Jetpack Compose + Material 3
- **DI:** Koin
- **Async:** Kotlin Coroutines + Flow
- **Preferences:** DataStore
- **Font:** Google Fonts (Ubuntu)

## Questions?

- Need help? Ask in [Discussions Q&A](https://github.com/ahmmedrejowan/MultiAppUninstaller/discussions/categories/q-a)
- Found a bug? Open an [Issue](https://github.com/ahmmedrejowan/MultiAppUninstaller/issues)
- Have an idea? Share in [Discussions](https://github.com/ahmmedrejowan/MultiAppUninstaller/discussions/categories/ideas)

---

Thanks again for contributing!

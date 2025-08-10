# MyCyclist

MyCyclist is a Kotlin Multiplatform application built for cycling enthusiasts. It provides a way to
explore data about professional cyclists, cycling teams, and races. The application leverages
Compose Multiplatform for its user interface, ensuring a modern and reactive experience.

## Features

* **Browse Races:** View a list of cycling races. Users can tap on a race to see more details.
    * View race stages and details for each stage.
    * See results for different classifications within a race/stage.
* **Explore Riders:** Display a list of professional cyclists.
    * Search for specific riders.
    * Sort riders based on different criteria.
    * View detailed information for each rider, including their team and race history.
* **Team Information:** (Implicitly) The app allows navigation to team details when viewing riders
  or races.
* **Adaptive UI:** The application uses Material 3 adaptive components to provide an optimized
  layout for different screen sizes, showing list/detail views where appropriate.

## Platforms

This project is built with Kotlin Multiplatform, using Compose Multiplatform for the UI, and currently targets:

* **Android:** Leverages Compose Multiplatform for the UI, including dynamic color theming on Android S+ for a native Android experience.
* **iOS:** Leverages Compose Multiplatform for the UI, with specific iOS configurations in the `iosMain` source set.
* **Desktop:** Leverages Compose Multiplatform for the UI, offering a consistent experience across desktop environments.

## Technology Stack

* **Kotlin Multiplatform:** For sharing code across different platforms.
* **Compose Multiplatform (Jetpack Compose):** For building a shared user interface across Android, iOS, and Desktop.
* **Material 3:** For modern UI components and theming, including adaptive layouts for various
  screen sizes.
* **Coroutines & Flow:** For asynchronous programming and managing data streams.
* **Lifecycle ViewModel:** For managing UI-related data in a lifecycle-aware manner.
* **Navigation Compose:** For navigating between different screens within the app.

## Contributions

Contributions are welcome! Please feel free to submit a pull request or open an issue.

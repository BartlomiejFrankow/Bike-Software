Bike-Software

## About app
It is a simple "cycling helper" with trip information and a drawn path on the maps provided by Gmail at the end of the trip. So what's special about this app?

This application does not have `android.permission.INTERNET` in its manifest file. The app only works locally, and uses the network connection to get maps layers.

Thanks to this, you do not have to worry about using your location for another purpose.

## Technology stack
- Kotlin
- Coroutines
- Flow
- SQLDelight
- Jetpack Compose
- MVVM

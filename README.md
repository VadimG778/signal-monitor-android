# Signal Monitor

Android-приложение для наблюдения за десятью параллельными генераторами случайного блуждания. Один экран содержит SciChart-график и отсортированный список генераторов с таймерами и управлением видимостью линий.

## Модули

```text
:app
├── :core:designsystem
├── :feature:monitor:data
│   └── :feature:monitor:domain
└── :feature:monitor:presentation
    ├── :feature:monitor:domain
    └── :core:designsystem
```

- `:app` — вершина графа: `Application`, single-activity entry point, Navigation 3, Koin composition root и AndroidX Startup initializer.
- `:core:designsystem` — тема, цвета и общие интервалы Compose.
- `:feature:monitor:domain` — чистый JVM-модуль с моделями, контрактами repository/source/factory, interactor и use case сортировки.
- `:feature:monitor:data` — factory, random-walk source, monotonic clock и process-level repository implementation.
- `:feature:monitor:presentation` — Orbit MVI, ViewModel, Compose UI, Navigation destination и адаптер SciChart.
- `build-logic` — convention plugins для Android/Kotlin/Compose/serialization/tests/Detekt/Kover и SciChart license resource.

Data и UI зависят от абстракций domain, но не друг от друга. `SignalPointSource` является стратегией получения точек: random walk можно заменить backend-источником с другим периодом без изменения repository, interactor и экранов. Koin собирает реализации только в `:app`.

## Стек

- Kotlin, Coroutines и Flow;
- Jetpack Compose, Material 3 и Navigation 3;
- Orbit MVI;
- Koin;
- AndroidX Startup;
- SciChart Android;
- Detekt с ktlint-wrapper, Android Lint и Kover;
- JUnit, Coroutines Test, Orbit Test и Compose UI Test.

Версии централизованы в version catalog. Параметры SDK, Java/Kotlin toolchain, lint, Detekt, тестов и покрытия вынесены в convention plugins.

## Запуск

Требуются JDK 17, Android SDK 37 и trial-ключ SciChart. Готовый APK доступен в разделе Releases. Для самостоятельной сборки ключ можно положить в игнорируемый файл `key.txt` в корне проекта:

```text
SignalMonitor/
├── key.txt
├── gradlew
└── ...
```

Дополнительно поддерживаются Gradle property и environment variable:

```properties
# ~/.gradle/gradle.properties
SCICHART_LICENSE_KEY=ключ
```

или:

```shell
SCICHART_LICENSE_KEY=ключ ./gradlew :app:assembleDebug
```

Приоритет источников: Gradle property, environment variable, затем локальный `key.txt`. Ключ добавляется в generated resources только на этапе сборки и не хранится в репозитории.

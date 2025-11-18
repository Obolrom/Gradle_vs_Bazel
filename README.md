### How to build the project with Gradle
```bash
./gradlew assembleDebug
```

---
### How to build the project with Bazel
```bash
bazel build //app:app
```

---
### Setup Gradle Profiler (on Mac or Linux)
```bash
brew install gradle-profiler
```

#### Run a simplest benchmark
```bash
gradle-profiler --benchmark --project-dir . --gradle-user-home ~/.gradle :app:assembleDebug
```

#### Run a benchmark with given .scenario file & generate build scan report
```bash
gradle-profiler --benchmark --project-dir . --gradle-user-home ~/.gradle --profile buildscan --scenario-file gradle_benchmark_scenarios/benchmark.scenario
```

#### Run a BAZEL benchmark with given .scenario file & generate build scan report
```bash
gradle-profiler --benchmark --project-dir . --bazel --scenario-file bazel_benchmark_scenarios/benchmark.scenario
```

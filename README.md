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

---
### Build with docker (build-android). With local repository
```bash
docker run --rm \
  -v "$PWD":/workspace \
  -v "$PWD/out":/out \
  -v "$HOME/.gradle":/gradle \
  -e GRADLE_TASK=":app:assembleDebug" \
  -e GRADLE_ARGS="--no-configuration-cache" \
  android-builder
```

### Build with docker. With repository copying inside a container
```bash
docker run --rm \
  --memory 3g \
  --cpuset-cpus="0-2" \
  --cpus 3 \
  -v "$PWD/out":/out \
  -e GIT_URL="https://github.com/Obolrom/Gradle_vs_Bazel.git" \
  -e GIT_REF="main" \
  -e GRADLE_TASK=":app:assembleDebug" \
  -e GRADLE_ARGS="--max-workers=3" \
  android-builder
```

#### Build docker image
```bash
docker build -t android-builder .
```

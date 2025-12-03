#!/usr/bin/env bash
set -euo pipefail

: "${GRADLE_TASK:=:app:assembleDebug}"
: "${GRADLE_ARGS:=--no-configuration-cache}"
: "${OUT_DIR:=/out}"

cd /workspace

echo "sdk.dir=${ANDROID_SDK_ROOT}" > local.properties

chmod +x ./gradlew || true

echo "==> Running: ./gradlew ${GRADLE_TASK}"
./gradlew --no-daemon --stacktrace ${GRADLE_ARGS} "${GRADLE_TASK}"

mkdir -p "${OUT_DIR}"

echo "==> Collecting APK/AAB into ${OUT_DIR}"

find . -type f -path "*/build/outputs/apk/**/*.apk" -print -exec cp -v "{}" "${OUT_DIR}/" \;

echo "==> Done. Artifacts:"
ls -lah "${OUT_DIR}"

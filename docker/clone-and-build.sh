#!/usr/bin/env bash
set -euo pipefail

: "${MODE:=build}"

: "${GIT_URL:?Set GIT_URL (e.g. https://github.com/org/repo.git)}"
: "${GIT_REF:=main}"
: "${PROJECT_SUBDIR:=.}"

# build mode
: "${GRADLE_TASK:=:app:assembleDebug}"
: "${GRADLE_ARGS:=}"

# profiler mode
: "${PROFILER_MODE:=--benchmark}"
: "${SCENARIO_FILE:=}"
: "${PROFILE_TYPE:=}"
: "${PROFILER_ARGS:=}"
: "${PROFILER_OUT_SUBDIR:=profile-out}"

: "${OUT_DIR:=/out}"

WORKDIR=/workspace
REPO_DIR="${WORKDIR}/repo"

rm -rf "${REPO_DIR}"
mkdir -p "${WORKDIR}" "${OUT_DIR}"

CLONE_URL="${GIT_URL}"
if [[ -n "${GIT_TOKEN:-}" ]]; then
  CLONE_URL="$(echo "${GIT_URL}" | sed -E "s#^https://#https://${GIT_TOKEN}@#")"
fi

echo "==> Cloning ${GIT_URL} @ ${GIT_REF}"
git clone --no-tags --depth 1 --branch "${GIT_REF}" "${CLONE_URL}" "${REPO_DIR}" \
  || { echo "Depth clone failed, retrying full clone..."; git clone "${CLONE_URL}" "${REPO_DIR}"; }

cd "${REPO_DIR}"
git config --global --add safe.directory "${REPO_DIR}"

# if GIT_REF is a commit sha
if git rev-parse --verify "${GIT_REF}^{commit}" >/dev/null 2>&1; then
  git checkout -q "${GIT_REF}"
fi

SHA="$(git rev-parse --short HEAD)"
cd "${REPO_DIR}/${PROJECT_SUBDIR}"

# Android SDK path for AGP
echo "sdk.dir=${ANDROID_SDK_ROOT}" > local.properties
chmod +x ./gradlew || true

if [[ "${MODE}" == "build" ]]; then
  read -r -a TASKS <<< "${GRADLE_TASK}"
  echo "==> Running: ./gradlew ${GRADLE_ARGS} ${GRADLE_TASK}"
  ./gradlew --no-daemon --stacktrace ${GRADLE_ARGS} "${TASKS[@]}"

  echo "==> Collecting APK/AAB into ${OUT_DIR}"
  find . -type f \( -path "*/build/outputs/apk/**/*.apk" -o -path "*/build/outputs/apk/*.apk" \) \
    -print -exec cp -v "{}" "${OUT_DIR}/" \;

  find . -type f -path "*/build/outputs/bundle/**/*.aab" -print -exec cp -v "{}" "${OUT_DIR}/" \;

  echo "==> Done. Artifacts:"
  ls -lah "${OUT_DIR}"
  exit 0
fi

PROFILE_FLAGS=()
if [[ -n "${PROFILE_TYPE}" ]]; then
  PROFILE_FLAGS+=(--profile "${PROFILE_TYPE}")
fi

if [[ "${MODE}" == "profiler" ]]; then
  if [[ -z "${SCENARIO_FILE}" ]]; then
    echo "ERROR: SCENARIO_FILE is required in MODE=profiler"
    exit 2
  fi

  RUN_ID="$(date -u +%Y%m%dT%H%M%SZ)"
  RUN_OUT="${OUT_DIR}/${PROFILER_OUT_SUBDIR}/${SHA}-${RUN_ID}"
  mkdir -p "${RUN_OUT}"

  echo "==> Running: gradle-profiler ${PROFILER_MODE} --project-dir . --scenario-file ${SCENARIO_FILE}"

  gradle-profiler ${PROFILER_MODE} \
    --project-dir . \
    --gradle-user-home "${GRADLE_USER_HOME}" \
    "${PROFILE_FLAGS[@]}" \
    --scenario-file "${SCENARIO_FILE}" \
    --output-dir "${RUN_OUT}" \
    ${PROFILER_ARGS}

  echo "==> Done. Profiler output:"
  ls -lah "${RUN_OUT}"
  exit 0
fi

echo "ERROR: Unknown MODE='${MODE}'. Use build or profiler."
exit 3

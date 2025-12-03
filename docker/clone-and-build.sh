#!/usr/bin/env bash
set -euo pipefail

: "${GIT_URL:?Set GIT_URL (e.g. https://github.com/org/repo.git)}"
: "${GIT_REF:=main}"
: "${PROJECT_SUBDIR:=.}"
: "${GRADLE_TASK:=:app:assembleDebug}"
: "${GRADLE_ARGS:=--no-configuration-cache}"
: "${OUT_DIR:=/out}"

WORKDIR=/workspace
REPO_DIR="${WORKDIR}/repo"

rm -rf "${REPO_DIR}"
mkdir -p "${WORKDIR}"

CLONE_URL="${GIT_URL}"
if [[ -n "${GIT_TOKEN:-}" ]]; then
  CLONE_URL="$(echo "${GIT_URL}" | sed -E "s#^https://#https://${GIT_TOKEN}@#")"
fi

echo "==> Cloning ${GIT_URL} @ ${GIT_REF}"
git clone --no-tags --depth 1 --branch "${GIT_REF}" "${CLONE_URL}" "${REPO_DIR}" \
  || { echo "Depth clone failed, retrying full clone..."; git clone "${CLONE_URL}" "${REPO_DIR}"; }

cd "${REPO_DIR}"
if ! git rev-parse --verify "${GIT_REF}^{commit}" >/dev/null 2>&1; then
  : # ok, branch already checked out
else
  git checkout -q "${GIT_REF}"
fi

git config --global --add safe.directory "${REPO_DIR}"

cd "${REPO_DIR}/${PROJECT_SUBDIR}"

# Android SDK path
echo "sdk.dir=${ANDROID_SDK_ROOT}" > local.properties

chmod +x ./gradlew || true

read -r -a TASKS <<< "${GRADLE_TASK}"

echo "==> Running: ./gradlew ${GRADLE_ARGS} ${GRADLE_TASK}"
./gradlew --no-daemon --stacktrace ${GRADLE_ARGS} "${TASKS[@]}"

mkdir -p "${OUT_DIR}"
echo "==> Collecting artifacts into ${OUT_DIR}"

find . -type f -path "*/build/outputs/apk/**/*.apk" -print -exec cp -v "{}" "${OUT_DIR}/" \;

echo "==> Done. Artifacts:"
ls -lah "${OUT_DIR}"

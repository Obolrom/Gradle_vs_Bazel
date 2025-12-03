FROM eclipse-temurin:17-jdk-jammy

ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl unzip git ca-certificates bash coreutils findutils \
  && rm -rf /var/lib/apt/lists/*

ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV PATH="$PATH:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${ANDROID_SDK_ROOT}/platform-tools"

RUN mkdir -p "${ANDROID_SDK_ROOT}/cmdline-tools" && \
    curl -fsSL -o /tmp/cmdline-tools.zip \
      https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip && \
    unzip -q /tmp/cmdline-tools.zip -d "${ANDROID_SDK_ROOT}/cmdline-tools" && \
    mv "${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools" "${ANDROID_SDK_ROOT}/cmdline-tools/latest" && \
    rm -f /tmp/cmdline-tools.zip

RUN yes | sdkmanager --licenses && \
    sdkmanager --install \
      "platform-tools" \
      "platforms;android-34" \
      "build-tools;34.0.0"

ENV GRADLE_USER_HOME=/gradle

COPY docker/clone-and-build.sh /usr/local/bin/clone-and-build
RUN chmod +x /usr/local/bin/clone-and-build

WORKDIR /workspace
ENTRYPOINT ["/usr/local/bin/clone-and-build"]

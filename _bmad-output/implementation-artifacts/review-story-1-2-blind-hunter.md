Conduct a review of CONTENT.
Look for what's missing, not only what's wrong.
Find at least ten issues to fix or improve.
Output a Markdown list of findings only — no severity, priority, or ranking.
If the content is empty, stop and say so.
If you have zero findings, re-check and keep thinking; do not stop with an empty list.

CONTENT:
diff --git a/docs/baseline.md b/docs/baseline.md
new file mode 100644
index 0000000..c4e6c9a
--- /dev/null
+++ b/docs/baseline.md
@@ -0,0 +1,426 @@
+# Android Baseline Evidence
+
+## Status
+
+The Android software baseline is **proven known-good on this host** after the one user-approved focused correction: replace the 87.5 MiB `material-icons-extended` runtime with version-aligned `material-icons-core` and vendor the seven exact AndroidX 1.6.6 icon definitions used by the app. With the runner's user-authorized opt-in baseline profile, `:app:assembleDebug` completed successfully and produced the documented APK.
+
+This result establishes only a reproducible software build baseline. It is not physical Meta Adventurer validation and makes no claim that Adventurer camera support works.
+
+## Provenance and source divergence
+
+| Item | Observed value |
+| --- | --- |
+| Imported VisionClaw source commit | `fbc72a25686016d015de1099817f73c2bddbdea5` |
+| `upstream/main` | `fbc72a25686016d015de1099817f73c2bddbdea5` |
+| Baseline refs | Local branch `baseline` and tag `baseline-visionclaw`, created only after the successful build, point to the commit whose subject is `chore: establish working Android baseline` |
+| Baseline application | `samples/CameraAccessAndroid` |
+| Android source delta from imported SHA | Only the approved extended-icons correction: dependency alias/use, seven local icon definitions, and the six corresponding UI import files |
+| Tracked build correction | `material-icons-extended` replaced by version-aligned `material-icons-core`; official AndroidX 1.6.6 definitions vendored for `LinkOff`, `PhotoCamera`, `ChevronRight`, `CallEnd`, `AutoAwesome`, `Videocam`, and `Error` |
+| Official icon source | Google Maven `material-icons-extended-android-1.6.6-sources.jar`, SHA-256 `9b22840d9d5ec83fca54783df66640c0e2d76bb164687d654c23bcc6c02f3a96` |
+
+## Version inventory
+
+| Component | Version / setting | Source |
+| --- | --- | --- |
+| Meta Wearables DAT | `0.4.0` (`mwdat-core`, `mwdat-camera`, `mwdat-mockdevice`) | `gradle/libs.versions.toml` |
+| Android Gradle Plugin | `8.6.0` | `gradle/libs.versions.toml` |
+| Gradle | `8.14.1` | `gradle/wrapper/gradle-wrapper.properties` |
+| Kotlin and Compose compiler plugin | `2.1.20` | `gradle/libs.versions.toml` |
+| Compose BOM | `2024.04.01` | `gradle/libs.versions.toml` |
+| LiveKit Android | `2.27.0` | `gradle/libs.versions.toml` |
+| compile / target / minimum SDK | `35` / `34` / `31` | `app/build.gradle.kts` |
+| Java and Kotlin bytecode target | `1.8` | `app/build.gradle.kts` |
+| Build ABI | `arm64-v8a` only | `app/build.gradle.kts` |
+
+## Host prerequisites and credentials
+
+- JDK: Temurin `17.0.20.1+1`, exposed at `/tmp/maria-toolchains/jdk-17`.
+- Android SDK: `/tmp/maria-toolchains/android-sdk`, containing Platform 35 revision 2, Build Tools 34.0.0 and 35.0.0, and Platform Tools 37.0.1.
+- The newest Android command-line tools were installed locally; the AGP selected Build Tools 34.0.0.
+- `hindsight.service` was `active` before the build and remained active after termination.
+- `Secrets.kt` was created from safe defaults with an empty gateway token at its ignored application path. It is excluded by the repository `.gitignore`.
+- GitHub Packages accepts `GITHUB_TOKEN` or the ignored `github_token` property in `samples/CameraAccessAndroid/local.properties`, as implemented by `settings.gradle.kts`. The successful dependency-resolution attempt injected the host `gh` credential into `GITHUB_TOKEN` only for the Gradle process. No token value was printed, logged, or written to the repository.
+- Toolchain directories, caches, `Secrets.kt`, and build outputs are ignored/local inputs and must not be committed.
+
+## Build command and evidence
+
+Working directory: `samples/CameraAccessAndroid`
+
+```bash
+GITHUB_TOKEN=$(/home/hermes/.local/bin/gh auth token) \
+JAVA_HOME=/tmp/maria-toolchains/jdk-17 \
+PATH=/tmp/maria-toolchains/jdk-17/bin:/usr/bin:/bin \
+ANDROID_HOME=/tmp/maria-toolchains/android-sdk \
+ANDROID_SDK_ROOT=/tmp/maria-toolchains/android-sdk \
+/home/hermes/.local/bin/codex-memory-run ./gradlew \
+  --no-daemon --no-parallel --max-workers=1 \
+  -Dorg.gradle.jvmargs="-Xmx512m -Dfile.encoding=UTF-8" \
+  -Dorg.gradle.java.home=/tmp/maria-toolchains/jdk-17 \
+  -Pkotlin.compiler.execution.strategy=in-process \
+  :app:assembleDebug
+```
+
+The authenticated 512 MiB attempt started on 2026-08-28 and was stopped after 60 minutes 31 seconds. It successfully resolved the authenticated DAT 0.4.0 artifacts and completed Android resource/manifest tasks, then remained in `:app:compileDebugKotlin` without emitting any class files or further Gradle output.
+
+At 60 minutes 29 seconds the Gradle daemon had approximately 687 MiB RSS. The systemd scope reported `MemoryCurrent=778866688`, `MemoryPeak=783044608`, `MemorySwapCurrent=0`, 69 tasks, and remained active; no OOM or scope failure was observed. The wrapper and daemon ignored `SIGINT` and `SIGTERM`, including a scope-directed `SIGTERM`, so the exact build scope was finally terminated with `SIGKILL`. The command returned exit 130. No retry was run outside the memory runner.
+
+Earlier observations:
+
+1. The documented command without `JAVA_HOME` could not launch the wrapper because this host has no system `java`; subsequent attempts added the local JDK to `JAVA_HOME` and `PATH` while retaining the required `org.gradle.java.home` override.
+2. An unauthenticated attempt reached `:app:checkDebugAarMetadata` and failed with HTTP 401 for all three DAT 0.4.0 modules. This verified that package authentication is a required local prerequisite. The ephemeral authenticated retry passed this point.
+3. Two preliminary attempts stalled while Gradle downloaded/installed prerequisites. Gradle 8.14.1 and Build Tools 34.0.0 were then prepared locally before the final attempt. Neither attempt changed tracked source.
+
+Warnings observed:
+
+- AGP 8.6.0 understands SDK XML through version 3, while the 2026 command-line tools supplied SDK XML version 4.
+- Gradle reported deprecated features that will be incompatible with Gradle 9.0; individual warnings were not expanded because the acceptance command does not enable `--warning-mode all`.
+- AAPT2 failed to shut down within 30 seconds during the final build and AGP forced its shutdown. The Gradle build continued into Kotlin compilation afterward.
+
+Artifact result: `samples/CameraAccessAndroid/app/build/outputs/apk/debug/app-debug.apk` is absent, so there is no SHA-256 digest to record.
+
+### Resource-reduced offline retry
+
+One additional unchanged-source retry reused the populated Gradle cache and reduced JVM pressure:
+
+```bash
+GITHUB_TOKEN=$(/home/hermes/.local/bin/gh auth token) \
+JAVA_HOME=/tmp/maria-toolchains/jdk-17 \
+PATH=/tmp/maria-toolchains/jdk-17/bin:/usr/bin:/bin \
+ANDROID_HOME=/tmp/maria-toolchains/android-sdk \
+ANDROID_SDK_ROOT=/tmp/maria-toolchains/android-sdk \
+/home/hermes/.local/bin/codex-memory-run ./gradlew \
+  --offline --no-daemon --no-parallel --max-workers=1 \
+  -Dorg.gradle.jvmargs="-Xmx384m -XX:+UseSerialGC -XX:MaxMetaspaceSize=192m -Dfile.encoding=UTF-8" \
+  -Dorg.gradle.vfs.watch=false \
+  -Dorg.gradle.java.home=/tmp/maria-toolchains/jdk-17 \
+  -Pkotlin.compiler.execution.strategy=in-process \
+  -Pkotlin.incremental=false \
+  :app:assembleDebug
+```
+
+The retry used cached authenticated dependencies successfully and reached `:app:compileDebugKotlin`. At the enforced cutoff, the wrapper had run for 60 minutes 2 seconds and the daemon for 60 minutes. The daemon showed 2.4% average CPU and 700,860 KiB RSS. Its systemd scope remained active with `MemoryCurrent=771293184`, `MemoryPeak=772030464`, `MemorySwapCurrent=0`, and 66 tasks. No OOM or scope failure was observed, but no Kotlin class file or APK had appeared. A scope-directed `SIGINT` stopped the build cleanly with exit 130; no later signal and no further retry were needed.
+
+This retry reduced the peak from approximately 747 MiB to approximately 736 MiB but did not stay below the 640 MiB `MemoryHigh` threshold and did not complete within the same 60-minute observation window. It therefore reinforces the host resource/time-blocker finding without establishing a source incompatibility.
+
+### Final 320 MiB offline retry
+
+A final unchanged-source attempt further constrained the JVM while retaining the populated cache:
+
+```bash
+GITHUB_TOKEN=$(/home/hermes/.local/bin/gh auth token) \
+JAVA_HOME=/tmp/maria-toolchains/jdk-17 \
+PATH=/tmp/maria-toolchains/jdk-17/bin:/usr/bin:/bin \
+ANDROID_HOME=/tmp/maria-toolchains/android-sdk \
+ANDROID_SDK_ROOT=/tmp/maria-toolchains/android-sdk \
+/home/hermes/.local/bin/codex-memory-run ./gradlew \
+  --offline --no-daemon --no-parallel --max-workers=1 \
+  -Dorg.gradle.jvmargs="-Xmx320m -XX:+UseSerialGC -XX:MaxMetaspaceSize=160m -XX:ReservedCodeCacheSize=64m -XX:TieredStopAtLevel=1 -Dfile.encoding=UTF-8" \
+  -Dorg.gradle.vfs.watch=false \
+  -Dorg.gradle.java.home=/tmp/maria-toolchains/jdk-17 \
+  -Pkotlin.compiler.execution.strategy=in-process \
+  -Pkotlin.incremental=false \
+  :app:assembleDebug
+```
+
+This attempt made substantially more progress and ended naturally after 43 minutes 11 seconds, before the 45-minute cutoff. `:app:compileDebugKotlin` emitted 274 class files and completed; `:app:compileDebugJavaWithJavac` also completed and emitted one Java class. The build then failed at `:app:mergeDebugJavaResource` because offline mode found no cached JAR for these runtime artifacts:
+
+- `androidx.lifecycle:lifecycle-common-java8:2.9.4`
+- `androidx.concurrent:concurrent-futures-ktx:1.1.0`
+- `androidx.concurrent:concurrent-futures:1.1.0`
+- `androidx.collection:collection-ktx:1.5.0`
+- `org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.7.3`
+- `org.jetbrains.kotlin:kotlin-parcelize-runtime:1.9.22`
+- `org.jetbrains.kotlin:kotlin-android-extensions-runtime:1.9.22`
+- `com.google.crypto.tink:tink-android:1.7.0`
+- `javax.sip:android-jain-sip-ri:1.3.0-91`
+- `com.google.dagger:dagger:2.46`
+- `com.google.auto.value:auto-value-annotations:1.6.3`
+
+Gradle warned that the daemon had run out of the configured 160 MiB metaspace and would expire after the build; it reported an effective maximum heap of 213.3 MiB. Despite that warning, Kotlin and Java compilation completed. The last cgroup sample before failure reported `MemoryCurrent=697905152`, `MemoryPeak=755335168`, `MemorySwapCurrent=0`, 66 tasks, and no OOM. After the natural build failure the scope was inactive, so final counters were no longer retained. No APK was produced in that attempt.
+
+This result narrowed the issue: the memory-constrained compilation path works, while the offline attempt could not package the application because its runtime cache was incomplete. It was not evidence of source incompatibility. The following online continuation tested that prerequisite directly.
+
+### Online cache-completion continuation
+
+The same 320 MiB configuration was run once without `--offline`, without cleaning, so the compiled classes and cached dependencies could be reused:
+
+```bash
+GITHUB_TOKEN=$(/home/hermes/.local/bin/gh auth token) \
+JAVA_HOME=/tmp/maria-toolchains/jdk-17 \
+PATH=/tmp/maria-toolchains/jdk-17/bin:/usr/bin:/bin \
+ANDROID_HOME=/tmp/maria-toolchains/android-sdk \
+ANDROID_SDK_ROOT=/tmp/maria-toolchains/android-sdk \
+/home/hermes/.local/bin/codex-memory-run ./gradlew \
+  --no-daemon --no-parallel --max-workers=1 \
+  -Dorg.gradle.jvmargs="-Xmx320m -XX:+UseSerialGC -XX:MaxMetaspaceSize=160m -XX:ReservedCodeCacheSize=64m -XX:TieredStopAtLevel=1 -Dfile.encoding=UTF-8" \
+  -Dorg.gradle.vfs.watch=false \
+  -Dorg.gradle.java.home=/tmp/maria-toolchains/jdk-17 \
+  -Pkotlin.compiler.execution.strategy=in-process \
+  -Pkotlin.incremental=false \
+  :app:assembleDebug
+```
+
+The online run resolved the eleven missing runtime JARs. Kotlin and Java compilation were `UP-TO-DATE`; `:app:mergeDebugJavaResource`, duplicate-class checking, and desugaring passed. The build then spent the remainder of the 30-minute observation window in `:app:mergeExtDexDebug` without further output or an APK. At the cutoff the wrapper had run for 30 minutes 2 seconds and the daemon for 30 minutes 1 second. The daemon showed 22.1% average CPU and 606,272 KiB RSS. Its systemd scope reported `MemoryCurrent=677650432`, `MemoryPeak=689549312`, `MemorySwapCurrent=0`, 41 tasks, and no OOM. A scope-directed `SIGINT` stopped the build cleanly with exit 130; no later signal and no further build were run.
+
+The earlier offline failure is therefore confirmed as a cache-prerequisite issue only. The remaining host blocker moved to memory-constrained external DEX merging; it is still not evidence of source incompatibility.
+
+### Cache-complete offline DEX retry
+
+After the online continuation populated the remaining runtime cache, the same unchanged-source 320 MiB configuration was run once more in offline mode. It did not clean the build tree, so the previously completed Kotlin and Java outputs remained available:
+
+```bash
+GITHUB_TOKEN=$(/home/hermes/.local/bin/gh auth token) \
+JAVA_HOME=/tmp/maria-toolchains/jdk-17 \
+PATH=/tmp/maria-toolchains/jdk-17/bin:/usr/bin:/bin \
+ANDROID_HOME=/tmp/maria-toolchains/android-sdk \
+ANDROID_SDK_ROOT=/tmp/maria-toolchains/android-sdk \
+/home/hermes/.local/bin/codex-memory-run ./gradlew \
+  --offline --no-daemon --no-parallel --max-workers=1 \
+  -Dorg.gradle.jvmargs="-Xmx320m -XX:+UseSerialGC -XX:MaxMetaspaceSize=160m -XX:ReservedCodeCacheSize=64m -XX:TieredStopAtLevel=1 -Dfile.encoding=UTF-8" \
+  -Dorg.gradle.vfs.watch=false \
+  -Dorg.gradle.java.home=/tmp/maria-toolchains/jdk-17 \
+  -Pkotlin.compiler.execution.strategy=in-process \
+  -Pkotlin.incremental=false \
+  :app:assembleDebug
+```
+
+The scope started at 2026-08-28 04:56:44 +01:00 and ended naturally at 05:10:15, before the 90-minute cutoff. Kotlin, Java, resource merging, duplicate checking, and desugaring remained `UP-TO-DATE`; the only executed task was `:app:mergeExtDexDebug`. The daemon log records `ERROR: D8: java.lang.OutOfMemoryError: Java heap space` while transforming `androidx.compose.material:material-icons-extended-android:1.6.6` (`material-icons-extended-release-runtime.jar`). Gradle reported `BUILD FAILED in 13m 30s` with 25 actionable tasks: one executed and 24 up-to-date. After the daemon returned that failure and stopped, the wrapper surfaced the secondary message `Could not receive a message from the daemon` and exited 1.
+
+The systemd journal measured 13 minutes 31.257 seconds wall time, 12 minutes 45.634 seconds CPU time, and a 640.2 MiB scope memory peak. The last retained live sample at daemon elapsed 10 minutes 43 seconds showed 94.4% average CPU, 539,772 KiB RSS, `MemoryCurrent=668905472`, `MemoryPeak=671350784`, `MemorySwapCurrent=0`, and 61 tasks. Neither the accessible scope/kernel journal nor the Gradle log recorded a cgroup OOM kill; the failure was the Java heap limit inside D8. During the monitored merge the build tree remained at 776 files and 95,059,568 bytes, including two `.dex`/`.jar` files. No APK was produced, so no SHA-256 digest exists. The build was not retried outside the memory runner.
+
+### Directed 416 MiB offline DEX retry
+
+A directed unchanged-source retry increased the requested heap while reducing the other bounded JVM memory pools. It remained offline, reused all up-to-date compilation and packaging inputs, and ran only in the required memory scope:
+
+```bash
+GITHUB_TOKEN=$(/home/hermes/.local/bin/gh auth token) \
+JAVA_HOME=/tmp/maria-toolchains/jdk-17 \
+PATH=/tmp/maria-toolchains/jdk-17/bin:/usr/bin:/bin \
+ANDROID_HOME=/tmp/maria-toolchains/android-sdk \
+ANDROID_SDK_ROOT=/tmp/maria-toolchains/android-sdk \
+/home/hermes/.local/bin/codex-memory-run ./gradlew \
+  --offline --no-daemon --no-parallel --max-workers=1 \
+  -Dorg.gradle.jvmargs="-Xmx416m -XX:+UseSerialGC -XX:MaxMetaspaceSize=128m -XX:ReservedCodeCacheSize=32m -XX:MaxDirectMemorySize=32m -XX:TieredStopAtLevel=1 -Dfile.encoding=UTF-8" \
+  -Dorg.gradle.vfs.watch=false \
+  -Dorg.gradle.java.home=/tmp/maria-toolchains/jdk-17 \
+  -Pkotlin.compiler.execution.strategy=in-process \
+  -Pkotlin.incremental=false \
+  :app:assembleDebug
+```
+
+The scope started at 2026-08-28 05:13:20 +01:00. Kotlin, Java, resource merging, duplicate checking, and desugaring were `UP-TO-DATE`; `:app:mergeExtDexDebug` was again the only executed task. Gradle warned that the daemon had exhausted heap, reported an effective maximum heap of 277.3 MiB and maximum metaspace of 128 MiB, then logged repeated `java.lang.OutOfMemoryError: Java heap space` failures in daemon health checks and the asynchronous output dispatcher. D8 ultimately logged the same heap error while transforming `androidx.compose.material:material-icons-extended-android:1.6.6` (`material-icons-extended-release-runtime.jar`). The daemon recorded `BUILD FAILED in 44m 32s` with 25 actionable tasks: one executed and 24 up-to-date, and finished executing at 05:57:52.
+
+Because the output dispatcher had also exhausted heap, that failure was not delivered to the waiting wrapper. At the authorized cutoff, the wrapper had run 45 minutes 4 seconds and the daemon 45 minutes 3 seconds, with the daemon still appearing at 86.0% average CPU and 623,976 KiB RSS. The final live sample showed `MemoryCurrent=693940224`, `MemoryPeak=697143296`, `MemorySwapCurrent=0`, and 42 tasks. A scope-directed `SIGINT` then stopped only this build; the wrapper returned exit 130. The systemd journal measured 45 minutes 15.941 seconds scope wall time, 38 minutes 51.895 seconds CPU time, and a 664.8 MiB memory peak. No accessible journal entry reported a cgroup OOM kill.
+
+The build tree remained at 776 files and 95,059,568 bytes throughout the monitored DEX merge. No APK or digest was produced, no source was changed, and no retry was run outside the memory scope.
+
+### Redistributed 464 MiB offline DEX retry
+
+A final unchanged-source redistribution increased the requested heap again while reducing metaspace, code cache, and direct memory. It remained offline, reused all prior outputs, and ran only inside the required scope:
+
+```bash
+GITHUB_TOKEN=$(/home/hermes/.local/bin/gh auth token) \
+JAVA_HOME=/tmp/maria-toolchains/jdk-17 \
+PATH=/tmp/maria-toolchains/jdk-17/bin:/usr/bin:/bin \
+ANDROID_HOME=/tmp/maria-toolchains/android-sdk \
+ANDROID_SDK_ROOT=/tmp/maria-toolchains/android-sdk \
+/home/hermes/.local/bin/codex-memory-run ./gradlew \
+  --offline --no-daemon --no-parallel --max-workers=1 \
+  -Dorg.gradle.jvmargs="-Xmx464m -XX:+UseSerialGC -XX:MaxMetaspaceSize=96m -XX:ReservedCodeCacheSize=24m -XX:MaxDirectMemorySize=16m -XX:TieredStopAtLevel=1 -Dfile.encoding=UTF-8" \
+  -Dorg.gradle.vfs.watch=false \
+  -Dorg.gradle.java.home=/tmp/maria-toolchains/jdk-17 \
+  -Pkotlin.compiler.execution.strategy=in-process \
+  -Pkotlin.incremental=false \
+  :app:assembleDebug
+```
+
+The scope started at 2026-08-28 06:01:48 +01:00. All tasks before `:app:mergeExtDexDebug` were again `UP-TO-DATE`. D8 did not emit a completed task failure, but the daemon stopped making CPU progress under memory pressure: its cumulative CPU time remained exactly 21 seconds from elapsed 3 minutes 38 seconds through elapsed 8 minutes 22 seconds. Over that interval daemon RSS rose from 668,068 KiB to 681,500 KiB, while the scope rose from `MemoryCurrent=738336768` to `MemoryCurrent=752410624`. The last live sample reported `MemoryPeak=752668672`, `MemorySwapCurrent=0`, and 63 tasks. Its cgroup `memory.events` recorded `high=16909`, `max=0`, `oom=0`, `oom_kill=0`, and `oom_group_kill=0`.
+
+Because the authorized 60-minute window applied while CPU remained active, the build was stopped after approximately 4 minutes 45 seconds of zero CPU progress rather than forcing the cgroup toward `MemoryMax`. A scope-directed `SIGINT` at 06:10:20 stopped only this attempt, and the wrapper returned exit 130. During shutdown Gradle reported that the daemon had run out of the configured 96 MiB JVM metaspace and reported an effective maximum heap of 309.3 MiB. The systemd journal measured 8 minutes 47.052 seconds scope wall time, 24.962 seconds CPU time, and a 719.5 MiB memory peak. No cgroup OOM occurred.
+
+The build tree remained at 776 files and 95,059,568 bytes, with no APK or digest. No source was changed and no build ran outside the scope. This retry shifted the limiting pool from Java heap to metaspace and severe `MemoryHigh` throttling; it did not establish a source incompatibility.
+
+### Final 448 MiB / 112 MiB offline balance
+
+The final permitted memory combination placed the requested heap between the two preceding configurations and restored some metaspace. It remained offline, reused all prior outputs, and ran only in the required scope:
+
+```bash
+GITHUB_TOKEN=$(/home/hermes/.local/bin/gh auth token) \
+JAVA_HOME=/tmp/maria-toolchains/jdk-17 \
+PATH=/tmp/maria-toolchains/jdk-17/bin:/usr/bin:/bin \
+ANDROID_HOME=/tmp/maria-toolchains/android-sdk \
+ANDROID_SDK_ROOT=/tmp/maria-toolchains/android-sdk \
+/home/hermes/.local/bin/codex-memory-run ./gradlew \
+  --offline --no-daemon --no-parallel --max-workers=1 \
+  -Dorg.gradle.jvmargs="-Xmx448m -XX:+UseSerialGC -XX:MaxMetaspaceSize=112m -XX:ReservedCodeCacheSize=24m -XX:MaxDirectMemorySize=16m -XX:TieredStopAtLevel=1 -Dfile.encoding=UTF-8" \
+  -Dorg.gradle.vfs.watch=false \
+  -Dorg.gradle.java.home=/tmp/maria-toolchains/jdk-17 \
+  -Pkotlin.compiler.execution.strategy=in-process \
+  -Pkotlin.incremental=false \
+  :app:assembleDebug
+```
+
+The scope started at 2026-08-28 06:12:06 +01:00, and every task before `:app:mergeExtDexDebug` was `UP-TO-DATE`. The daemon accumulated 21 seconds of CPU by elapsed 1 minute 34 seconds and remained at exactly 21 seconds through elapsed 5 minutes 34 seconds: four minutes of sustained inactivity. During the same interval its RSS rose from 647,072 KiB to 666,804 KiB, `MemoryCurrent` rose from 721,940,480 to 741,941,248 bytes, and cgroup `high` events rose from 6,869 to 13,822. The final live sample reported `MemoryPeak=742199296`, `MemorySwapCurrent=0`, 63 tasks, `max=0`, `oom=0`, `oom_kill=0`, and `oom_group_kill=0`.
+
+The build was therefore stopped on the explicit sustained-inactivity criterion rather than allowed to push closer to `MemoryMax`. A scope-directed `SIGINT` at 06:17:52 stopped only this attempt; the wrapper returned exit 130. The systemd journal measured 5 minutes 55.696 seconds scope wall time, 24.920 seconds CPU time, and a 708.7 MiB memory peak. The daemon logged only cancellation and normal shutdown; it did not emit a completed D8 failure or heap/metaspace OOM before the stop.
+
+The build tree remained at 776 files and 95,059,568 bytes, and no APK or digest was produced. No source changed and no build ran outside the scope. This final balance confirmed severe `MemoryHigh` throttling before either bounded JVM pool reported its own failure. The user subsequently authorized the focused icon correction documented below.
+
+## Approved icon correction and post-correction evidence
+
+The user authorized one narrow divergence from the imported source to remove the D8 input that the cache-complete failure identified. No version or toolchain was upgraded:
+
+- `androidx.compose.material:material-icons-extended` was replaced with the version-aligned `androidx.compose.material:material-icons-core` alias and dependency.
+- The exact AndroidX 1.6.6 filled definitions for `LinkOff`, `PhotoCamera`, `ChevronRight`, `CallEnd`, `AutoAwesome`, `Videocam`, and `Error` were placed in the local `com.meta.wearable.dat.externalsampleapps.cameraaccess.icons.filled` package, and only the corresponding imports were changed. Existing core imports for `ArrowBack`, `Call`, `CheckCircle`, `Close`, `Refresh`, and `Settings` were left unchanged.
+- The authoritative source was downloaded from `https://dl.google.com/dl/android/maven2/androidx/compose/material/material-icons-extended-android/1.6.6/material-icons-extended-android-1.6.6-sources.jar` to `/tmp/material-icons-extended-android-1.6.6-sources.jar`. Its SHA-256 is `9b22840d9d5ec83fca54783df66640c0e2d76bb164687d654c23bcc6c02f3a96`.
+- Each local file retains the Android Open Source Project Apache 2.0 header. A mechanical comparison against the corresponding JAR entry passed after substituting only the package declaration; the builder calls and vector data are unchanged.
+
+The first post-correction `:app:assembleDebug` with the established 320 MiB / 160 MiB configuration was interrupted by an AAPT2 daemon startup failure at `:app:processDebugResources`, after 2 minutes 49 seconds wall time, 56.926 seconds CPU, and a 650.4 MiB scope peak. An identical retry passed resources and reached `:app:compileDebugKotlin`, then failed after 27 minutes 23 seconds with `GradleKotlinCompilerWorkAction > InvocationTargetException` and no source diagnostic. The scope used 1 minute 24.434 seconds CPU and peaked at 723.4 MiB; there was no cgroup OOM or swap use.
+
+A directed compilation using the same command and replacing the final task with `:app:compileDebugKotlin --stacktrace` succeeded. It produced 281 Kotlin class files in 42 minutes 23.861 seconds wall time, consumed 1 minute 38.346 seconds CPU, peaked at 733.8 MiB, and emitted no source error. This proves that the approved correction itself compiles under the pinned toolchain.
+
+The exact cache-complete assemble command used after that directed compile was:
+
+```bash
+GITHUB_TOKEN=$(/home/hermes/.local/bin/gh auth token) \
+JAVA_HOME=/tmp/maria-toolchains/jdk-17 \
+PATH=/tmp/maria-toolchains/jdk-17/bin:/usr/bin:/bin \
+ANDROID_HOME=/tmp/maria-toolchains/android-sdk \
+ANDROID_SDK_ROOT=/tmp/maria-toolchains/android-sdk \
+/home/hermes/.local/bin/codex-memory-run ./gradlew \
+  --offline --no-daemon --no-parallel --max-workers=1 \
+  -Dorg.gradle.jvmargs="-Xmx320m -XX:+UseSerialGC -XX:MaxMetaspaceSize=160m -XX:ReservedCodeCacheSize=64m -XX:TieredStopAtLevel=1 -Dfile.encoding=UTF-8" \
+  -Dorg.gradle.vfs.watch=false \
+  -Dorg.gradle.java.home=/tmp/maria-toolchains/jdk-17 \
+  -Pkotlin.compiler.execution.strategy=in-process \
+  -Pkotlin.incremental=false \
+  :app:assembleDebug
+```
+
+Kotlin, Java, resources, Java-resource merging, duplicate checking, and desugaring passed or were `UP-TO-DATE`. `:app:mergeExtDexDebug` did not complete. After 35 minutes 29 seconds and a sustained low-CPU interval, the command was stopped with `SIGINT`; the exact scope later required scope-directed `SIGTERM` to close its daemon. The scope journal measured 36 minutes 20.561 seconds wall time, 8 minutes 20.988 seconds CPU, and a 661.4 MiB peak, with no cgroup OOM or swap use.
+
+The following exact directed command was then used; later directed runs changed only the quoted JVM profile shown in the results table:
+
+```bash
+GITHUB_TOKEN=$(/home/hermes/.local/bin/gh auth token) \
+JAVA_HOME=/tmp/maria-toolchains/jdk-17 \
+PATH=/tmp/maria-toolchains/jdk-17/bin:/usr/bin:/bin \
+ANDROID_HOME=/tmp/maria-toolchains/android-sdk \
+ANDROID_SDK_ROOT=/tmp/maria-toolchains/android-sdk \
+/home/hermes/.local/bin/codex-memory-run ./gradlew \
+  --offline --no-daemon --no-parallel --max-workers=1 --stacktrace \
+  -Dorg.gradle.jvmargs="-Xmx320m -XX:+UseSerialGC -XX:MaxMetaspaceSize=160m -XX:ReservedCodeCacheSize=64m -XX:TieredStopAtLevel=1 -Dfile.encoding=UTF-8" \
+  -Dorg.gradle.vfs.watch=false \
+  -Dorg.gradle.java.home=/tmp/maria-toolchains/jdk-17 \
+  -Pkotlin.compiler.execution.strategy=in-process \
+  -Pkotlin.incremental=false \
+  :app:mergeExtDexDebug
+```
+
+| Directed JVM profile | Result and evidence |
+| --- | --- |
+| `-Xmx320m`, metaspace 160 MiB, code cache 64 MiB | One run sustained CPU for most of 2h10m47.561s (1h53m44.702s CPU, 640.4 MiB peak) before later stalling; it was stopped cleanly and its daemon terminated within the scope. A fresh no-attach run then naturally stalled and its daemon log proved `java.lang.OutOfMemoryError: Java heap space` in daemon health checks. It ran 15m19.789s wall / 5m59.480s CPU and peaked at 640.2 MiB. |
+| `-Xmx384m`, metaspace 128 MiB, code cache 64 MiB | D8/R8 failed with `CompilationFailedException` caused by `java.lang.OutOfMemoryError: Java heap space`; 9m17.193s wall / 7m31.016s CPU, 640.2 MiB peak. |
+| `-Xmx448m`, metaspace 112 MiB, code cache 48 MiB | Gradle warned that the 112 MiB metaspace was exhausted, then D8/R8 failed from Java heap exhaustion; 17m37.618s wall / 6m08.391s CPU, 711.2 MiB peak. |
+| `-Xmx480m -Xss512k`, metaspace 128 MiB, code cache 32 MiB, `ActiveProcessorCount=1` | Four DEX intermediates were written, then no output changed for 15m26s while CPU advanced only 14.487s (1.56%). The run was stopped on the authorized livelock threshold. Journal totals were 44m45.654s wall / 1m36.173s CPU and a 758.2 MiB peak. Last live counters were `MemoryCurrent=775090176`, `MemoryPeak=795054080`, `high=51537`, `max=0`, `oom=0`, `oom_kill=0`, swap 0, and 59 tasks. No JVM or cgroup OOM was emitted before the cutoff. |
+
+A pre-final-run snapshot contained four incomplete DEX intermediates under `app/build/intermediates/dex/debug/mergeExtDexDebug/`; all passed Build Tools 34.0.0 `dexdump -f` with exit 0. Their sizes were 15,384,880, 13,533,228, 9,217,908, and 5,049,616 bytes, with respective SHA-256 values `2fbb23fa54c3bc4ab44881b442861572d2e643f7da86d5f4dce11bd55deab884`, `df2c12956477fd3f9e06b2732a2e21a733fdf93470d8502ab55d4880f84136c5`, `b5b2cf4727045020aed58a493dac0507c0866301cd5b27af7c6eb8a18231a97a`, and `3446e345a872e3fc04edb512b15c17ab38175fe5ea96403dc8bc4c7cc058e58e`. They were valid partial outputs, not a completed merge or APK.
+
+Lightweight offline `help` diagnostics showed that Gradle 8.14.1 still forks a single-use daemon even when client and daemon options are aligned. With the 320 MiB profile repeated through `JAVA_OPTS`, `GRADLE_OPTS`, and `org.gradle.jvmargs`, the wrapper used 85,072 KiB RSS and 15 threads while the daemon used 194,364 KiB RSS and 36 threads; the scope peaked at 444.5 MiB. Using only `JAVA_OPTS` and an empty `org.gradle.jvmargs` still created a second daemon, now without the memory bounds, so that form is unsafe for a heavy retry. A final `help` check limited the wrapper to a 32 MiB heap and the daemon to the final 480 MiB profile; it succeeded with wrapper RSS 80,380 KiB / 16 threads, daemon RSS 273,732 KiB / 39 threads, and a 351.6 MiB scope peak. This saves only about 5 MiB of observed wrapper RSS and does not eliminate the second JVM.
+
+One final directed retry tested that smaller wrapper without changing the already established daemon profile. Its exact command was:
+
+```bash
+env -u GRADLE_OPTS \
+  GITHUB_TOKEN=$(/home/hermes/.local/bin/gh auth token) \
+  JAVA_HOME=/tmp/maria-toolchains/jdk-17 \
+  PATH=/tmp/maria-toolchains/jdk-17/bin:/usr/bin:/bin \
+  ANDROID_HOME=/tmp/maria-toolchains/android-sdk \
+  ANDROID_SDK_ROOT=/tmp/maria-toolchains/android-sdk \
+  JAVA_OPTS="-Xms16m -Xmx32m -Xss256k -XX:+UseSerialGC -XX:MaxMetaspaceSize=64m -XX:ReservedCodeCacheSize=16m -XX:ActiveProcessorCount=1 -Dfile.encoding=UTF-8" \
+  /home/hermes/.local/bin/codex-memory-run ./gradlew \
+  --offline --no-daemon --no-parallel --max-workers=1 --stacktrace \
+  -Dorg.gradle.jvmargs="-Xmx480m -Xss512k -XX:+UseSerialGC -XX:MaxMetaspaceSize=128m -XX:ReservedCodeCacheSize=32m -XX:TieredStopAtLevel=1 -XX:ActiveProcessorCount=1 -Dfile.encoding=UTF-8" \
+  -Dorg.gradle.vfs.watch=false \
+  -Dorg.gradle.java.home=/tmp/maria-toolchains/jdk-17 \
+  -Pkotlin.compiler.execution.strategy=in-process \
+  -Pkotlin.incremental=false \
+  :app:mergeExtDexDebug
+```
+
+At the absolute 20-minute cutoff, `classes.dex` existed only as a zero-byte placeholder and no other DEX/output had appeared. The final live sample at 20 minutes 3 seconds showed scope CPU 61.678 seconds (5.13% average), `MemoryCurrent=776368128`, `MemoryPeak=776626176`, `high=25422`, 61 tasks, `max=0`, `oom=0`, `oom_kill=0`, and swap 0. `SIGINT` stopped the wrapper with exit 130; its single-use daemon received the client cancellation and exited on its own. The journal recorded 20 minutes 53.086 seconds wall time, 1 minute 5.760 seconds CPU, and a 745.5 MiB peak. The daemon tail contained only cancellation, disconnect, and broken-pipe messages, with no heap, metaspace, fatal, or cgroup OOM.
+
+During the approximately 15-second cancellation interval, D8 finished writing the first `classes.dex` at 15,384,880 bytes. It passes `dexdump -f`, and its SHA-256 is `2fbb23fa54c3bc4ab44881b442861572d2e643f7da86d5f4dce11bd55deab884`; no `classes2.dex` or later file remains after this final attempt. The task never completed, so this is still only a valid partial output and cannot be packaged or treated as build success.
+
+Through the end of this constrained-profile investigation, no post-correction command ran outside `codex-memory-run`, no build cleaned prior outputs, all cgroup observations showed zero swap and zero cgroup OOM kills, and `hindsight.service` remained untouched. At that point no APK or baseline ref existed; the later authorized expanded-scope success is recorded next.
+
+## Successful expanded-scope baseline build
+
+After the constrained runs above, the user authorized an opt-in runner profile while requiring the original profile to remain unchanged. The installed local runner therefore retains its default `MemoryHigh=640M`, `MemoryMax=768M`, `MemorySwapMax=0`, and `TasksMax=128`. `CODEX_MEMORY_RUN_PROFILE=maria-baseline-expanded` selects only the authorized baseline envelope: `MemoryHigh=1400M`, `MemoryMax=1536M`, `MemorySwapMax=0`, and the same `TasksMax=128`. This runner change is machine-local and is not part of the repository.
+
+The first expanded-profile assemble used a 512 MiB Gradle heap and reached `:app:mergeExtDexDebug`, but D8 failed naturally with `java.lang.OutOfMemoryError: Java heap space` after 2 minutes 22 seconds. A 896 MiB attempt with the initially authorized 1 GiB `MemoryHigh` avoided that heap failure but became heavily throttled above `MemoryHigh`; it was stopped after sustained near-zero CPU progress with no cgroup OOM and no swap. A 768 MiB attempt was stopped during startup, before DEX, when the user superseded the earlier envelope by authorizing `MemoryHigh=1400M`. These attempts changed no source or toolchain version.
+
+The successful command started on 2026-08-28 at approximately 18:26 WEST from `samples/CameraAccessAndroid`:
+
+```bash
+CODEX_MEMORY_RUN_PROFILE=maria-baseline-expanded \
+GITHUB_TOKEN=$(/home/hermes/.local/bin/gh auth token) \
+JAVA_HOME=/tmp/maria-toolchains/jdk-17 \
+PATH=/tmp/maria-toolchains/jdk-17/bin:/usr/bin:/bin \
+ANDROID_HOME=/tmp/maria-toolchains/android-sdk \
+ANDROID_SDK_ROOT=/tmp/maria-toolchains/android-sdk \
+/home/hermes/.local/bin/codex-memory-run ./gradlew \
+  --no-daemon --no-parallel --max-workers=1 \
+  -Dorg.gradle.jvmargs="-Xmx896m -Dfile.encoding=UTF-8" \
+  -Dorg.gradle.java.home=/tmp/maria-toolchains/jdk-17 \
+  -Pkotlin.compiler.execution.strategy=in-process \
+  :app:assembleDebug
+```
+
+Result: `BUILD SUCCESSFUL in 1m 48s`; 36 actionable tasks, 12 executed and 24 up-to-date. The systemd scope consumed 1 minute 23.616 seconds of CPU over 1 minute 49.103 seconds wall time and reported a 1.3 GiB memory peak. `hindsight.service` was active before and after the build. No other heavy scope ran concurrently, and the build used one Gradle worker.
+
+Warnings observed in the successful run:
+
+- AGP 8.6.0 understands SDK XML through version 3 while the installed command-line tools supplied SDK XML version 4.
+- Gradle reported deprecated features that will become incompatible with Gradle 9.0; no baseline dependency or toolchain version was changed.
+- `stripDebugDebugSymbols` could not strip a set of packaged third-party native libraries and explicitly packaged them unchanged.
+- Gradle generated its incubating problems report at `samples/CameraAccessAndroid/build/reports/problems/problems-report.html`.
+
+Artifact result:
+
+| Item | Value |
+| --- | --- |
+| APK | `samples/CameraAccessAndroid/app/build/outputs/apk/debug/app-debug.apk` |
+| Size | 78,830,897 bytes |
+| SHA-256 | `519adc557a8476a63ee52eeb7a94d84d9d2cc53c126cd1f10447283461827d30` |
+
+The APK is an ignored build output and is not committed. Its successful creation proves only the documented software baseline; physical Meta Adventurer acceptance remains a separate milestone.
+
+## Current Android integration map
+
+- **App and permissions:** `MainActivity.kt` initializes settings, requests only the current capture mode's Android permissions, and lazily starts DAT monitoring for glasses mode.
+- **Wearable registration and selection:** `wearables/WearablesInit.kt` owns one-time DAT initialization; `wearables/WearablesViewModel.kt` observes registration, permission, and device state. Runtime streaming uses `AutoDeviceSelector`.
+- **Primary session path:** `livekit/LiveKitSessionViewModel.kt` is the active voice-and-vision client. It obtains a short-lived room ticket from the configured gateway, connects a LiveKit room, publishes microphone/video, observes agent state, captions, and UI cards, and maps state to `Disconnected`, `Connecting`, `Connected`, or `Failed`.
+- **Phone camera and preview:** phone mode uses LiveKit's CameraX-backed rear camera. `startPreview()` keeps a camera-only preview while no call is active; the call reuses/replaces that track. Zoom and frame-freeze operate on the displayed/published track.
+- **Glasses camera lifecycle:** glasses mode starts a DAT `StreamSession` at medium quality and 24 fps, bridges I420 frames into a LiveKit capturer, keeps a foreground service/wake lock, detects 10-second stalls, and retries up to three times.
+- **Frame handling:** the active LiveKit path forwards DAT frames to its capturer without the README's claimed ~1 fps JPEG throttle. JPEG conversion is used for the one-shot frozen-frame bitmap. The legacy `stream/StreamViewModel.kt` converts every DAT frame to JPEG quality 50 for display and forwards every bitmap to legacy WebRTC.
+- **Audio, Gemini, and OpenClaw:** Android source contains no `gemini/` or `openclaw/` packages described by the root README. Audio/turn-taking are delegated to LiveKit/WebRTC and the server-side agent selected by the gateway; no direct Android Gemini Live or OpenClaw router is present.
+- **Reconnect:** glasses video has bounded stall retries. LiveKit room events refresh agent state. The retained legacy `webrtc/WebRTCSessionViewModel.kt` reconnects signaling and rejoins its room when the app returns to the foreground.
+- **Legacy raw WebRTC:** `webrtc/` and `stream/` retain signaling, peer connection, bitmap capture, bidirectional media, room code, foreground reconnection, and streaming service code, but `MainActivity.kt` routes the primary UI through `LiveKitStreamScreen` rather than the legacy stream screens.
+- **Settings:** `settings/SettingsManager.kt` persists capture source, intelligence engine, gateway base URL/token, identity, caption, and autostart choices in DataStore, with `Secrets.kt` fallbacks. `settings/GatewayApi.kt` validates gateway connectivity and interprets its JSON error format.
+
+## Documentation and test mismatches
+
+- The root README lists Android `gemini/*` and `openclaw/*` files that do not exist in the imported tree and describes direct Gemini/OpenClaw routing that has been replaced by LiveKit plus a server-side agent.
+- The root README says GitHub credentials are `gpr.user` / `gpr.token`; `settings.gradle.kts` actually reads `GITHUB_TOKEN` or `github_token`.
+- The root README claims both glasses and phone frames are throttled to roughly 1 fps JPEG for Gemini. The active LiveKit path publishes continuous video; the legacy path also forwards every frame.
+- `samples/CameraAccessAndroid/README.md` still describes the original basic DAT camera sample, JDK 11+, SDK 31+, and an older Android Studio baseline rather than the imported VisionClaw/LiveKit application.
+- `InstrumentationTest.kt` pairs only a Ray-Ban Meta mock, grants Bluetooth and Internet permissions but not the runtime Camera/Record Audio permissions used by the active application, and asserts legacy home/non-stream/stream UI text. It neither exercises `LiveKitStreamScreen` nor verifies the current gateway/session path. No instrumentation tests were run because no emulator/device is part of this software-baseline story.
+
+## Known problems and next action
+
+- The default 768 MiB runner profile remains insufficient for this application's cache-complete DEX merge on this host. Reproduction requires the explicit machine-local `maria-baseline-expanded` profile; the runner's safer defaults remain unchanged.
+- `--no-daemon` still creates a wrapper JVM plus a single-use daemon for this Gradle configuration. Both processes are included in the documented systemd memory scope.
+- AGP/SDK XML compatibility and unstripped third-party native-library warnings remain present. They did not block the debug APK and were not addressed because broad toolchain modernization is outside this baseline.
+- The retained instrumentation suite is stale and was not run because this story has no emulator or physical-device scope. Its routing and permission gaps are documented above.
+- This baseline permits the planned DAT migration to begin from a reproducible software starting point, but physical Meta Adventurer acceptance remains separate and unproven.
diff --git a/samples/CameraAccessAndroid/app/build.gradle.kts b/samples/CameraAccessAndroid/app/build.gradle.kts
index fde2685..d45eda3 100644
--- a/samples/CameraAccessAndroid/app/build.gradle.kts
+++ b/samples/CameraAccessAndroid/app/build.gradle.kts
@@ -64,7 +64,7 @@ dependencies {
   implementation(libs.androidx.exifinterface)
   implementation(libs.androidx.lifecycle.runtime.compose)
   implementation(libs.androidx.lifecycle.viewmodel.compose)
-  implementation(libs.androidx.material.icons.extended)
+  implementation(libs.androidx.material.icons.core)
   implementation(libs.androidx.material3)
   implementation(libs.kotlinx.collections.immutable)
   implementation(libs.mwdat.core)
diff --git a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/AutoAwesome.kt b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/AutoAwesome.kt
new file mode 100644
index 0000000..6f65b45
--- /dev/null
+++ b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/AutoAwesome.kt
@@ -0,0 +1,66 @@
+/*
+ * Copyright 2024 The Android Open Source Project
+ *
+ * Licensed under the Apache License, Version 2.0 (the "License");
+ * you may not use this file except in compliance with the License.
+ * You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ */
+
+package com.meta.wearable.dat.externalsampleapps.cameraaccess.icons.filled
+
+import androidx.compose.material.icons.Icons
+import androidx.compose.material.icons.materialIcon
+import androidx.compose.material.icons.materialPath
+import androidx.compose.ui.graphics.vector.ImageVector
+
+public val Icons.Filled.AutoAwesome: ImageVector
+    get() {
+        if (_autoAwesome != null) {
+            return _autoAwesome!!
+        }
+        _autoAwesome = materialIcon(name = "Filled.AutoAwesome") {
+            materialPath {
+                moveTo(19.0f, 9.0f)
+                lineToRelative(1.25f, -2.75f)
+                lineTo(23.0f, 5.0f)
+                lineToRelative(-2.75f, -1.25f)
+                lineTo(19.0f, 1.0f)
+                lineToRelative(-1.25f, 2.75f)
+                lineTo(15.0f, 5.0f)
+                lineToRelative(2.75f, 1.25f)
+                lineTo(19.0f, 9.0f)
+                close()
+                moveTo(11.5f, 9.5f)
+                lineTo(9.0f, 4.0f)
+                lineTo(6.5f, 9.5f)
+                lineTo(1.0f, 12.0f)
+                lineToRelative(5.5f, 2.5f)
+                lineTo(9.0f, 20.0f)
+                lineToRelative(2.5f, -5.5f)
+                lineTo(17.0f, 12.0f)
+                lineToRelative(-5.5f, -2.5f)
+                close()
+                moveTo(19.0f, 15.0f)
+                lineToRelative(-1.25f, 2.75f)
+                lineTo(15.0f, 19.0f)
+                lineToRelative(2.75f, 1.25f)
+                lineTo(19.0f, 23.0f)
+                lineToRelative(1.25f, -2.75f)
+                lineTo(23.0f, 19.0f)
+                lineToRelative(-2.75f, -1.25f)
+                lineTo(19.0f, 15.0f)
+                close()
+            }
+        }
+        return _autoAwesome!!
+    }
+
+private var _autoAwesome: ImageVector? = null
diff --git a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/CallEnd.kt b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/CallEnd.kt
new file mode 100644
index 0000000..624a74d
--- /dev/null
+++ b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/CallEnd.kt
@@ -0,0 +1,58 @@
+/*
+ * Copyright 2024 The Android Open Source Project
+ *
+ * Licensed under the Apache License, Version 2.0 (the "License");
+ * you may not use this file except in compliance with the License.
+ * You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ */
+
+package com.meta.wearable.dat.externalsampleapps.cameraaccess.icons.filled
+
+import androidx.compose.material.icons.Icons
+import androidx.compose.material.icons.materialIcon
+import androidx.compose.material.icons.materialPath
+import androidx.compose.ui.graphics.vector.ImageVector
+
+public val Icons.Filled.CallEnd: ImageVector
+    get() {
+        if (_callEnd != null) {
+            return _callEnd!!
+        }
+        _callEnd = materialIcon(name = "Filled.CallEnd") {
+            materialPath {
+                moveTo(12.0f, 9.0f)
+                curveToRelative(-1.6f, 0.0f, -3.15f, 0.25f, -4.6f, 0.72f)
+                verticalLineToRelative(3.1f)
+                curveToRelative(0.0f, 0.39f, -0.23f, 0.74f, -0.56f, 0.9f)
+                curveToRelative(-0.98f, 0.49f, -1.87f, 1.12f, -2.66f, 1.85f)
+                curveToRelative(-0.18f, 0.18f, -0.43f, 0.28f, -0.7f, 0.28f)
+                curveToRelative(-0.28f, 0.0f, -0.53f, -0.11f, -0.71f, -0.29f)
+                lineTo(0.29f, 13.08f)
+                curveToRelative(-0.18f, -0.17f, -0.29f, -0.42f, -0.29f, -0.7f)
+                curveToRelative(0.0f, -0.28f, 0.11f, -0.53f, 0.29f, -0.71f)
+                curveTo(3.34f, 8.78f, 7.46f, 7.0f, 12.0f, 7.0f)
+                reflectiveCurveToRelative(8.66f, 1.78f, 11.71f, 4.67f)
+                curveToRelative(0.18f, 0.18f, 0.29f, 0.43f, 0.29f, 0.71f)
+                curveToRelative(0.0f, 0.28f, -0.11f, 0.53f, -0.29f, 0.71f)
+                lineToRelative(-2.48f, 2.48f)
+                curveToRelative(-0.18f, 0.18f, -0.43f, 0.29f, -0.71f, 0.29f)
+                curveToRelative(-0.27f, 0.0f, -0.52f, -0.11f, -0.7f, -0.28f)
+                curveToRelative(-0.79f, -0.74f, -1.69f, -1.36f, -2.67f, -1.85f)
+                curveToRelative(-0.33f, -0.16f, -0.56f, -0.5f, -0.56f, -0.9f)
+                verticalLineToRelative(-3.1f)
+                curveTo(15.15f, 9.25f, 13.6f, 9.0f, 12.0f, 9.0f)
+                close()
+            }
+        }
+        return _callEnd!!
+    }
+
+private var _callEnd: ImageVector? = null
diff --git a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/ChevronRight.kt b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/ChevronRight.kt
new file mode 100644
index 0000000..f23a046
--- /dev/null
+++ b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/ChevronRight.kt
@@ -0,0 +1,43 @@
+/*
+ * Copyright 2024 The Android Open Source Project
+ *
+ * Licensed under the Apache License, Version 2.0 (the "License");
+ * you may not use this file except in compliance with the License.
+ * You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ */
+
+package com.meta.wearable.dat.externalsampleapps.cameraaccess.icons.filled
+
+import androidx.compose.material.icons.Icons
+import androidx.compose.material.icons.materialIcon
+import androidx.compose.material.icons.materialPath
+import androidx.compose.ui.graphics.vector.ImageVector
+
+public val Icons.Filled.ChevronRight: ImageVector
+    get() {
+        if (_chevronRight != null) {
+            return _chevronRight!!
+        }
+        _chevronRight = materialIcon(name = "Filled.ChevronRight") {
+            materialPath {
+                moveTo(10.0f, 6.0f)
+                lineTo(8.59f, 7.41f)
+                lineTo(13.17f, 12.0f)
+                lineToRelative(-4.58f, 4.59f)
+                lineTo(10.0f, 18.0f)
+                lineToRelative(6.0f, -6.0f)
+                close()
+            }
+        }
+        return _chevronRight!!
+    }
+
+private var _chevronRight: ImageVector? = null
diff --git a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/Error.kt b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/Error.kt
new file mode 100644
index 0000000..6204b88
--- /dev/null
+++ b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/Error.kt
@@ -0,0 +1,54 @@
+/*
+ * Copyright 2024 The Android Open Source Project
+ *
+ * Licensed under the Apache License, Version 2.0 (the "License");
+ * you may not use this file except in compliance with the License.
+ * You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ */
+
+package com.meta.wearable.dat.externalsampleapps.cameraaccess.icons.filled
+
+import androidx.compose.material.icons.Icons
+import androidx.compose.material.icons.materialIcon
+import androidx.compose.material.icons.materialPath
+import androidx.compose.ui.graphics.vector.ImageVector
+
+public val Icons.Filled.Error: ImageVector
+    get() {
+        if (_error != null) {
+            return _error!!
+        }
+        _error = materialIcon(name = "Filled.Error") {
+            materialPath {
+                moveTo(12.0f, 2.0f)
+                curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
+                reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
+                reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
+                reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
+                close()
+                moveTo(13.0f, 17.0f)
+                horizontalLineToRelative(-2.0f)
+                verticalLineToRelative(-2.0f)
+                horizontalLineToRelative(2.0f)
+                verticalLineToRelative(2.0f)
+                close()
+                moveTo(13.0f, 13.0f)
+                horizontalLineToRelative(-2.0f)
+                lineTo(11.0f, 7.0f)
+                horizontalLineToRelative(2.0f)
+                verticalLineToRelative(6.0f)
+                close()
+            }
+        }
+        return _error!!
+    }
+
+private var _error: ImageVector? = null
diff --git a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/LinkOff.kt b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/LinkOff.kt
new file mode 100644
index 0000000..9de46f4
--- /dev/null
+++ b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/LinkOff.kt
@@ -0,0 +1,72 @@
+/*
+ * Copyright 2024 The Android Open Source Project
+ *
+ * Licensed under the Apache License, Version 2.0 (the "License");
+ * you may not use this file except in compliance with the License.
+ * You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ */
+
+package com.meta.wearable.dat.externalsampleapps.cameraaccess.icons.filled
+
+import androidx.compose.material.icons.Icons
+import androidx.compose.material.icons.materialIcon
+import androidx.compose.material.icons.materialPath
+import androidx.compose.ui.graphics.vector.ImageVector
+
+public val Icons.Filled.LinkOff: ImageVector
+    get() {
+        if (_linkOff != null) {
+            return _linkOff!!
+        }
+        _linkOff = materialIcon(name = "Filled.LinkOff") {
+            materialPath {
+                moveTo(17.0f, 7.0f)
+                horizontalLineToRelative(-4.0f)
+                verticalLineToRelative(1.9f)
+                horizontalLineToRelative(4.0f)
+                curveToRelative(1.71f, 0.0f, 3.1f, 1.39f, 3.1f, 3.1f)
+                curveToRelative(0.0f, 1.43f, -0.98f, 2.63f, -2.31f, 2.98f)
+                lineToRelative(1.46f, 1.46f)
+                curveTo(20.88f, 15.61f, 22.0f, 13.95f, 22.0f, 12.0f)
+                curveToRelative(0.0f, -2.76f, -2.24f, -5.0f, -5.0f, -5.0f)
+                close()
+                moveTo(16.0f, 11.0f)
+                horizontalLineToRelative(-2.19f)
+                lineToRelative(2.0f, 2.0f)
+                lineTo(16.0f, 13.0f)
+                close()
+                moveTo(2.0f, 4.27f)
+                lineToRelative(3.11f, 3.11f)
+                curveTo(3.29f, 8.12f, 2.0f, 9.91f, 2.0f, 12.0f)
+                curveToRelative(0.0f, 2.76f, 2.24f, 5.0f, 5.0f, 5.0f)
+                horizontalLineToRelative(4.0f)
+                verticalLineToRelative(-1.9f)
+                lineTo(7.0f, 15.1f)
+                curveToRelative(-1.71f, 0.0f, -3.1f, -1.39f, -3.1f, -3.1f)
+                curveToRelative(0.0f, -1.59f, 1.21f, -2.9f, 2.76f, -3.07f)
+                lineTo(8.73f, 11.0f)
+                lineTo(8.0f, 11.0f)
+                verticalLineToRelative(2.0f)
+                horizontalLineToRelative(2.73f)
+                lineTo(13.0f, 15.27f)
+                lineTo(13.0f, 17.0f)
+                horizontalLineToRelative(1.73f)
+                lineToRelative(4.01f, 4.0f)
+                lineTo(20.0f, 19.74f)
+                lineTo(3.27f, 3.0f)
+                lineTo(2.0f, 4.27f)
+                close()
+            }
+        }
+        return _linkOff!!
+    }
+
+private var _linkOff: ImageVector? = null
diff --git a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/PhotoCamera.kt b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/PhotoCamera.kt
new file mode 100644
index 0000000..2c11adb
--- /dev/null
+++ b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/PhotoCamera.kt
@@ -0,0 +1,62 @@
+/*
+ * Copyright 2024 The Android Open Source Project
+ *
+ * Licensed under the Apache License, Version 2.0 (the "License");
+ * you may not use this file except in compliance with the License.
+ * You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ */
+
+package com.meta.wearable.dat.externalsampleapps.cameraaccess.icons.filled
+
+import androidx.compose.material.icons.Icons
+import androidx.compose.material.icons.materialIcon
+import androidx.compose.material.icons.materialPath
+import androidx.compose.ui.graphics.vector.ImageVector
+
+public val Icons.Filled.PhotoCamera: ImageVector
+    get() {
+        if (_photoCamera != null) {
+            return _photoCamera!!
+        }
+        _photoCamera = materialIcon(name = "Filled.PhotoCamera") {
+            materialPath {
+                moveTo(12.0f, 12.0f)
+                moveToRelative(-3.2f, 0.0f)
+                arcToRelative(3.2f, 3.2f, 0.0f, true, true, 6.4f, 0.0f)
+                arcToRelative(3.2f, 3.2f, 0.0f, true, true, -6.4f, 0.0f)
+            }
+            materialPath {
+                moveTo(9.0f, 2.0f)
+                lineTo(7.17f, 4.0f)
+                lineTo(4.0f, 4.0f)
+                curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
+                verticalLineToRelative(12.0f)
+                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
+                horizontalLineToRelative(16.0f)
+                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
+                lineTo(22.0f, 6.0f)
+                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
+                horizontalLineToRelative(-3.17f)
+                lineTo(15.0f, 2.0f)
+                lineTo(9.0f, 2.0f)
+                close()
+                moveTo(12.0f, 17.0f)
+                curveToRelative(-2.76f, 0.0f, -5.0f, -2.24f, -5.0f, -5.0f)
+                reflectiveCurveToRelative(2.24f, -5.0f, 5.0f, -5.0f)
+                reflectiveCurveToRelative(5.0f, 2.24f, 5.0f, 5.0f)
+                reflectiveCurveToRelative(-2.24f, 5.0f, -5.0f, 5.0f)
+                close()
+            }
+        }
+        return _photoCamera!!
+    }
+
+private var _photoCamera: ImageVector? = null
diff --git a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/Videocam.kt b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/Videocam.kt
new file mode 100644
index 0000000..c01fc00
--- /dev/null
+++ b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/icons/filled/Videocam.kt
@@ -0,0 +1,50 @@
+/*
+ * Copyright 2024 The Android Open Source Project
+ *
+ * Licensed under the Apache License, Version 2.0 (the "License");
+ * you may not use this file except in compliance with the License.
+ * You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ */
+
+package com.meta.wearable.dat.externalsampleapps.cameraaccess.icons.filled
+
+import androidx.compose.material.icons.Icons
+import androidx.compose.material.icons.materialIcon
+import androidx.compose.material.icons.materialPath
+import androidx.compose.ui.graphics.vector.ImageVector
+
+public val Icons.Filled.Videocam: ImageVector
+    get() {
+        if (_videocam != null) {
+            return _videocam!!
+        }
+        _videocam = materialIcon(name = "Filled.Videocam") {
+            materialPath {
+                moveTo(17.0f, 10.5f)
+                verticalLineTo(7.0f)
+                curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
+                horizontalLineTo(4.0f)
+                curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
+                verticalLineToRelative(10.0f)
+                curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
+                horizontalLineToRelative(12.0f)
+                curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
+                verticalLineToRelative(-3.5f)
+                lineToRelative(4.0f, 4.0f)
+                verticalLineToRelative(-11.0f)
+                lineToRelative(-4.0f, 4.0f)
+                close()
+            }
+        }
+        return _videocam!!
+    }
+
+private var _videocam: ImageVector? = null
diff --git a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/CameraAccessScaffold.kt b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/CameraAccessScaffold.kt
index 1648b07..6d6048b 100644
--- a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/CameraAccessScaffold.kt
+++ b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/CameraAccessScaffold.kt
@@ -29,7 +29,7 @@ import androidx.compose.foundation.layout.padding
 import androidx.compose.foundation.layout.width
 import androidx.compose.foundation.shape.RoundedCornerShape
 import androidx.compose.material.icons.Icons
-import androidx.compose.material.icons.filled.Error
+import com.meta.wearable.dat.externalsampleapps.cameraaccess.icons.filled.Error
 import androidx.compose.material3.ExperimentalMaterial3Api
 import androidx.compose.material3.Icon
 import androidx.compose.material3.MaterialTheme
diff --git a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/CircleButton.kt b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/CircleButton.kt
index da01529..c1615bb 100644
--- a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/CircleButton.kt
+++ b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/CircleButton.kt
@@ -13,7 +13,7 @@ import androidx.compose.foundation.layout.RowScope
 import androidx.compose.foundation.layout.aspectRatio
 import androidx.compose.foundation.shape.CircleShape
 import androidx.compose.material.icons.Icons
-import androidx.compose.material.icons.filled.PhotoCamera
+import com.meta.wearable.dat.externalsampleapps.cameraaccess.icons.filled.PhotoCamera
 import androidx.compose.material3.Button
 import androidx.compose.material3.ButtonDefaults
 import androidx.compose.material3.Icon
diff --git a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/ControlsRow.kt b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/ControlsRow.kt
index f8c0689..c5590dc 100644
--- a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/ControlsRow.kt
+++ b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/ControlsRow.kt
@@ -9,8 +9,8 @@ import androidx.compose.foundation.layout.height
 import androidx.compose.foundation.layout.navigationBarsPadding
 import androidx.compose.foundation.shape.CircleShape
 import androidx.compose.material.icons.Icons
-import androidx.compose.material.icons.filled.AutoAwesome
-import androidx.compose.material.icons.filled.Videocam
+import com.meta.wearable.dat.externalsampleapps.cameraaccess.icons.filled.AutoAwesome
+import com.meta.wearable.dat.externalsampleapps.cameraaccess.icons.filled.Videocam
 import androidx.compose.material3.Button
 import androidx.compose.material3.ButtonDefaults
 import androidx.compose.material3.Icon
diff --git a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/LiveKitStreamScreen.kt b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/LiveKitStreamScreen.kt
index 4f6b825..a38b3c8 100644
--- a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/LiveKitStreamScreen.kt
+++ b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/LiveKitStreamScreen.kt
@@ -32,9 +32,9 @@ import androidx.compose.foundation.shape.RoundedCornerShape
 import androidx.compose.foundation.verticalScroll
 import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.filled.Call
-import androidx.compose.material.icons.filled.CallEnd
 import androidx.compose.material.icons.filled.Close
 import androidx.compose.material.icons.filled.Settings
+import com.meta.wearable.dat.externalsampleapps.cameraaccess.icons.filled.CallEnd
 import androidx.compose.material3.CircularProgressIndicator
 import androidx.compose.material3.Icon
 import androidx.compose.material3.IconButton
diff --git a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/NonStreamScreen.kt b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/NonStreamScreen.kt
index f691d1a..3d6c9d6 100644
--- a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/NonStreamScreen.kt
+++ b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/NonStreamScreen.kt
@@ -27,8 +27,8 @@ import androidx.compose.foundation.layout.size
 import androidx.compose.foundation.layout.systemBarsPadding
 import androidx.compose.foundation.layout.width
 import androidx.compose.material.icons.Icons
-import androidx.compose.material.icons.filled.LinkOff
 import androidx.compose.material.icons.filled.Settings
+import com.meta.wearable.dat.externalsampleapps.cameraaccess.icons.filled.LinkOff
 import androidx.compose.material3.DropdownMenu
 import androidx.compose.material3.DropdownMenuItem
 import androidx.compose.material3.ExperimentalMaterial3Api
diff --git a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/SettingsScreen.kt b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/SettingsScreen.kt
index 272b4e0..96b776a 100644
--- a/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/SettingsScreen.kt
+++ b/samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/ui/SettingsScreen.kt
@@ -17,7 +17,7 @@ import androidx.compose.foundation.text.KeyboardOptions
 import androidx.compose.foundation.verticalScroll
 import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.automirrored.filled.ArrowBack
-import androidx.compose.material.icons.filled.ChevronRight
+import com.meta.wearable.dat.externalsampleapps.cameraaccess.icons.filled.ChevronRight
 import androidx.compose.material3.AlertDialog
 import androidx.compose.material3.CircularProgressIndicator
 import androidx.compose.material3.ExperimentalMaterial3Api
diff --git a/samples/CameraAccessAndroid/gradle/libs.versions.toml b/samples/CameraAccessAndroid/gradle/libs.versions.toml
index 0b459f7..aebfc81 100644
--- a/samples/CameraAccessAndroid/gradle/libs.versions.toml
+++ b/samples/CameraAccessAndroid/gradle/libs.versions.toml
@@ -23,7 +23,7 @@ lifecycleProcess = "2.8.7"
 androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
 androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
 androidx-material3 = { group = "androidx.compose.material3", name = "material3" }
-androidx-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
+androidx-material-icons-core = { group = "androidx.compose.material", name = "material-icons-core" }
 mwdat-core = { group = "com.meta.wearable", name = "mwdat-core", version.ref = "mwdat" }
 mwdat-camera = { group = "com.meta.wearable", name = "mwdat-camera", version.ref = "mwdat" }
 mwdat-mockdevice = { group = "com.meta.wearable", name = "mwdat-mockdevice", version.ref = "mwdat" }


Do not invoke any skill. Return only the review result.


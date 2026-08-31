# Prompt: clone and validate the Android DAT migration on a Mac

Copy the text below into the coding assistant running on the iMac.

````text
You are validating, not redesigning, the focused Android DAT 0.9 migration in this repository. Start by obtaining the repository and branch:

```bash
git clone https://github.com/rubensousa-uw/macgyver.git
cd macgyver
git fetch origin
git switch feat/dat-0.9
git pull --ff-only origin feat/dat-0.9
```

Then read `docs/handoffs/android-instrumentation-mac.md` and `docs/dat-migration.md`.

Important source-state check: confirm `samples/CameraAccessAndroid/gradle/libs.versions.toml` pins `mwdat = "0.9.0"` and that the DAT migration files are present. The branch must contain the four-test `InstrumentationTest` suite, including `rawI420GateRejectsCompressedCodecConfigurationAndMalformedFrames`; do not test an older baseline.

Goal: run the existing MockDeviceKit instrumentation suite successfully on this Mac and record truthful evidence. This is not physical Meta Adventurer validation.

Constraints:
- Preserve the current DAT 0.9 migration and the release arm64-only artifact. The debug build may retain x86_64/arm64 emulator support.
- Never print, write, commit, or log GITHUB_TOKEN, OAuth credentials, API keys, or raw private audio.
- Do not change production code just to make a test green. Diagnose first and report any necessary fix separately.
- Run Gradle with one worker. Do not run unrelated full-repository typechecks or builds.

Steps:
1. Inspect the current branch and dirty worktree after the source-state check. Confirm JDK 17 and an Android API-35 AVD are available. Use arm64-v8a on Apple Silicon or x86_64 on Intel. Boot it fully and confirm `adb devices -l` shows `device`.
2. Authenticate only if needed using GitHub CLI, then export GITHUB_TOKEN only in the shell running Gradle: `export GITHUB_TOKEN="$(gh auth token)"`. Do not echo it.
3. From `samples/CameraAccessAndroid`, create the ignored local configuration required for compilation. Do not add a real gateway token:
   `cp app/src/main/java/io/github/rubensousa/macgyver/Secrets.kt.example app/src/main/java/io/github/rubensousa/macgyver/Secrets.kt`
4. Run:
   ./gradlew :app:connectedDebugAndroidTest --no-daemon --max-workers=1 --console=plain -Dddmlib.getprop.timeout.sec=60 -Dorg.gradle.jvmargs=-Xmx2g -Dkotlin.daemon.jvm.options=-Xmx1g
5. If it passes, inspect `app/build/reports/androidTests/connected/debug/index.html`, confirm all four InstrumentationTest cases passed, run `git diff --check`, and append the exact command/result/date to docs/dat-migration.md. State explicitly that this proves MockDeviceKit/SDK behavior only, not physical Adventurer camera support.
6. If it fails, do not guess or broaden scope. Return the failure tail, report path, `adb devices -l`, Mac architecture, emulator API/image/ABI, and any relevant logcat tail. Leave the migration record accurate.

Final response: state pass/fail/inconclusive, list the tests actually executed, link or name the report, list changed files, and clearly separate emulator evidence from physical-hardware verification.
````

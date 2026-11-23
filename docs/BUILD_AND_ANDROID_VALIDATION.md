# Android build and release validation

The nCrypt AI fork retains Agora's existing Android application structure,
native build, product flavors, and Room database history while the rebrand is
being completed. Avoid changing the application ID or Room database package
without a dedicated migration plan and upgrade tests.

## Local build

Use JDK 21 and the Android SDK/NDK versions pinned by the project. Initialize
submodules before building because native components and the PRoot runtime are
part of the release process.

```bash
git submodule update --init --recursive
chmod +x ./gradlew ./build-proot.sh
./gradlew :app:testFdroidDebugUnitTest
./gradlew assembleFdroidRelease
```

The upstream build workflow performs additional module tests and verifies that
the required PRoot shared libraries are packaged in the F-Droid APK. Keep those
checks when updating the workflow.

## GitHub Actions

The existing workflow builds the F-Droid release APK after unit tests and
uploads it as a workflow artifact. It is not a Play Store-signed release unless
the repository owner configures signing secrets. Do not publish a release APK
until the package identity, app branding, license notices, and device testing
have been reviewed.

## Package migration policy

The current Android namespace/application ID is `com.newoether.agora`.
Changing it to `com.ncrypt.ai` is a breaking install identity change and may
also affect source packages, manifest component names, Room schema locations,
deep links, file-provider authorities, and third-party integrations. Perform
that migration as a separate PR with explicit data migration and clean-install
and upgrade-install tests. A display-name rebrand does not require that risky
migration.

# Android app (A Round Tuit)

Turns this web app into an installable Android APK using Capacitor.
Every push to `main` triggers `.github/workflows/android-apk.yml`, which runs
`build.sh` on GitHub's servers and posts the APK under **Releases → Latest**.

- `capacitor.config.json` — app ID, name, background colour
- `inject.html` + `native/MainActivity.java` — downloads don't work inside an
  app, so calendar exports (.ics) open straight in the phone's calendar app
  and the text export goes to the share sheet
- `res/` — launcher icons and file-sharing paths

Signing: repo secrets `ANDROID_KEYSTORE_B64` and `ANDROID_KEYSTORE_PASSWORD`
(same key as the other apps). Without them each build uses a throwaway key.

Nothing in here affects the GitHub Pages site.

Signing secrets added Nov 2026: builds now use the permanent key and install over each other.

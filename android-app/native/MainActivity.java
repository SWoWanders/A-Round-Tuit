package io.github.swowanders.roundtuit;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import androidx.core.content.FileProvider;
import com.getcapacitor.BridgeActivity;
import java.io.File;
import java.io.FileOutputStream;

// Browser-style downloads (the calendar .ics export, the text export) do
// nothing inside an app, so android-app/inject.html hands those files to
// window.TuitNative.openFile(), which saves them and opens them in another
// app: .ics files open in the calendar, anything else goes to the share sheet.
public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bridge.getWebView().addJavascriptInterface(new NativeFiles(), "TuitNative");
    }

    public class NativeFiles {
        @JavascriptInterface
        public void openFile(String name, String mime, String base64) {
            try {
                File dir = new File(getCacheDir(), "exports");
                dir.mkdirs();
                File f = new File(dir, name.replaceAll("[^A-Za-z0-9._-]", "_"));
                try (FileOutputStream out = new FileOutputStream(f)) {
                    out.write(Base64.decode(base64, Base64.DEFAULT));
                }
                final Uri uri = FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".fileprovider", f);
                runOnUiThread(() -> {
                    Intent send = new Intent(Intent.ACTION_SEND).setType(mime)
                        .putExtra(Intent.EXTRA_STREAM, uri)
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (mime.startsWith("text/calendar")) {
                        Intent view = new Intent(Intent.ACTION_VIEW).setDataAndType(uri, mime)
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try { startActivity(view); return; } catch (ActivityNotFoundException ignored) { }
                    }
                    startActivity(Intent.createChooser(send, "Save or share " + f.getName()));
                });
            } catch (Exception e) {
                runOnUiThread(() -> bridge.getWebView().evaluateJavascript(
                    "alert('Sorry, that file could not be opened on this phone.')", null));
            }
        }
    }
}

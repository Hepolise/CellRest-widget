package ru.hepolise.cellrest.Activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.util.Locale;

import ru.hepolise.cellrest.BuildConfig;
import ru.hepolise.cellrest.R;


public class HelpActivity extends AppCompatActivity {

    private WebView mWebView;

    Locale currentLocale = Locale.getDefault();



    String locale = currentLocale.toString();
    String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
//        try {
//            version = getPackageManager().getPackageInfo(getPackageName(), 0 ).versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//
//        }
        int versionCode = BuildConfig.VERSION_CODE;
        version = Integer.toString(versionCode);
        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //Log.d("traff: ", locale);
        //Log.d("cellL", ("https://srvr.su/traffic/help.php?l=" + locale + "&ver=" + version));
        mWebView.loadUrl("https://srvr.su/traffic/help.php?l=" + locale + "&ver=" + version);
        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}

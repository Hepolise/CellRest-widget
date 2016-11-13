package am.hepolise.traffic;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class webview extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Uri url = request.getUrl();
        Log.d("traff", "url:" + url.getHost());
        if(url.getHost().endsWith("srvr.tk")) {
            Log.d("traff", "url:" + url.getHost());
            return false;
        }
        //Log.d("traff", url.getHost());

        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        view.getContext().startActivity(intent);
        return true;
    }
}
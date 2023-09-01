package com.example.webview.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;

public class MyAppWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i(getClass().getName(), "URL : " + url);
        try {
            if (shouldTryIntent(url)) {
                Intent intent;

                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                } catch (URISyntaxException ex) {
                    Log.e(getClass().getName(),"Bad request URI format : [" + url + "] =" + ex.getMessage());
                    return false;
                }

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    if (view.getContext().getPackageManager().resolveActivity(intent, 0) == null) {
                        String pkgName = intent.getPackage();
                        if (pkgName != null) {
                            Uri uri = Uri.parse("market://search?q=pname:" + pkgName);
                            intent = new Intent(Intent.ACTION_VIEW, uri);
                            view.getContext().startActivity(intent);
                        }
                    } else {
                        Uri uri = Uri.parse(intent.getDataString());
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        view.getContext().startActivity(intent);
                    }
                } else {
                    try {
                        view.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Uri uri = Uri.parse("market://search?q=pname:" + intent.getPackage());
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        view.getContext().startActivity(intent);
                    }
                }
            } else {
                view.loadUrl(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean shouldTryIntent(String url){
        return url != null && (url.startsWith("intent:") ||
                url.contains("market://") ||
                url.contains("vguard") ||
                url.contains("droidxantivirus") ||
                url.contains("v3mobile") ||
                url.contains(".apk") ||
                url.contains("mvaccine") ||
                url.contains("smartwall://") ||
                url.contains("nidlogin://") ||
                url.contains("http://m.ahnlab.com/kr/site/download"));
    }
}

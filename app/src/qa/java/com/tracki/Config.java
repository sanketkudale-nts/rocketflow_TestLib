package com.tracki;

import android.os.Build;
import android.webkit.WebView;

/**
 * Created by rahul on 16/4/19
 */
public class Config {

    /**
     * Method is used to enable debugging of web pages when build is in debug mode
     *
     * @param webView webview reference
     */
    public static void enableWebDebug(WebView webView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }
}

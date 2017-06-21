package com.qdigo.jindouyun.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qdigo.jindouyun.BaseActivity;
import com.qdigo.jindouyun.R;

import butterknife.Bind;
@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
public class WebActivity extends BaseActivity {

    private int index;

    @Bind(R.id.mWebView)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getIntent().getStringExtra("title");
        index = getIntent().getIntExtra("index", 0);
        setContentViewWithDefaultTitle(R.layout.activity_web,title);
    }

    @Override
    protected void initView() {

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebClient());
        webView.addJavascriptInterface(new JavaScriptInterface(), "load");
        String url = "";
        switch (index){

            case 1:
                url = "about.html";
                break;
            case 2:
                url = "problem.html";
                break;
            default:
                break;
        }
        webView.loadUrl("file:///android_asset/"+url);

    }

    public class JavaScriptInterface{

    }

    public class WebClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);


        }
    }
}

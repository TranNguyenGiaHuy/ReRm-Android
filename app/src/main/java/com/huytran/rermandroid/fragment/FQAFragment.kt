package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.huytran.rermandroid.R
import com.huytran.rermandroid.fragment.base.BaseFragment

class FQAFragment:BaseFragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_fqa, container, false)

        val webView = view.findViewById<WebView>(R.id.webView);

        val webSetting = webView.settings
        webSetting.builtInZoomControls = true
        webSetting.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl("file:///android_asset/menu.html")


        return view;
    }
}
package com.gal.marvelpedia

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_character_detail_web_view.*

class CharacterDetailWebView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_detail_web_view)

        setPointer()
    }

    private fun setPointer() {
        val webView = findViewById<WebView>(R.id.webView)
        //WebView
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.overScrollMode = WebView.OVER_SCROLL_NEVER
//        val url = intent.getStringExtra("url")
//        webView.loadUrl(url)
        val url = intent.getStringExtra("url")
        val titleName = intent.getStringExtra("titleName")
        Log.e(" WebView", url)
        txtCharDetail.text = titleName.toUpperCase()


        webView.loadUrl(url)
    }
}

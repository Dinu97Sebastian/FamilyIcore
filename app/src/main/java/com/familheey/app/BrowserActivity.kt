package com.familheey.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.familheey.app.Models.ChatModel
import com.familheey.app.Post.CreatePostActivity
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Utilities
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_browser.*
import java.net.URISyntaxException


class BrowserActivity : AppCompatActivity() {
    var url: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        url = intent.extras?.getString("URL")
        wbview.loadUrl(url!!)
        val webSettings: WebSettings = wbview.settings
        webSettings.javaScriptEnabled = true
        goBack.setOnClickListener {
            onBackPressed()
        }

        wbview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                if (url.startsWith("intent")){
                    try{
                        val intent = Intent.parseUri(url,Intent.URI_INTENT_SCHEME)
                        val fallbackURL = intent.getStringExtra("browser_fallback_url")
                        if (fallbackURL!=null){
                            view.loadUrl(fallbackURL)
                            return true
                        }
                    }catch (e: URISyntaxException){

                    }
                }
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                progresbar.visibility = View.GONE
            }
        }
        browser_settings.setOnClickListener {
            showMenus(it)
        }
    }

    private fun showMenus(v: View) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(R.menu.browser_menu, popup.menu)

        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.copylink -> {
                    Utilities.setClipboard(this, url, "Url copied to clipboard")
                }
                R.id.openinchrome -> {
                    val uri = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
                R.id.share_external -> {
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_SUBJECT, "post")
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, url)
                    startActivity(Intent.createChooser(intent, "Share"))
                }
                R.id.share_inside -> {
                    val s = ChatModel(url)
                    startActivity(Intent(this, CreatePostActivity::class.java).putExtra("FROM", "CHAT").putExtra(Constants.Bundle.DATA, Gson().toJson(s)))
                }

            }
            true
        }
        popup.show()
    }

}
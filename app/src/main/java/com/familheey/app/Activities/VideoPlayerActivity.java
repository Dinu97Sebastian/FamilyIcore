package com.familheey.app.Activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.familheey.app.R;

import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;

public class VideoPlayerActivity extends AppCompatActivity {
    ProgressBar progressVideo;
    WebView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        progressVideo = findViewById(R.id.progressVideo);
        videoView = findViewById(R.id.videoView);
        setStatusBarColor();
        String url = getIntent().getStringExtra("URL");
        //showProgress();
        String previwurl = IMAGE_BASE_URL + "default_video.jpg";
        String sourc = "<source src=\"" + url + "\"  type=\"video/mp4\">";
        String html = "<html><body><video width=\"100%\" height=\"100%\" poster=\"" + previwurl + "\"controls=\"controls\" >" + sourc + "</vieo></body></html>";
        videoView.setBackgroundColor(Color.TRANSPARENT);

        videoView.setWebViewClient(new WebViewClient());
        videoView.getSettings().setJavaScriptEnabled(true);
        videoView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        videoView.getSettings().setPluginState(WebSettings.PluginState.ON);
        videoView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        videoView.setWebChromeClient(new WebChromeClient());

        videoView.loadData(html, "text/html", "UTF-8");
    }


    private void setStatusBarColor() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }
    }

}

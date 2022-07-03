package com.familheey.app.Post;

import android.Manifest;
import android.app.Dialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;
import java.util.Objects;


public class VideoActivity extends AppCompatActivity {
    String STREAM_URL = "";
    String Fname = "";
    private SimpleExoPlayer player;
    com.google.android.exoplayer2.ui.PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        STREAM_URL = Objects.requireNonNull(getIntent().getExtras()).getString("URL");
        Fname = Objects.requireNonNull(getIntent().getExtras()).getString("NAME");
        setContentView(R.layout.activity_video);
        playerView = findViewById(R.id.playerView);
        findViewById(R.id.btn_close).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.imagedownload).setOnClickListener(view -> {
                    if (isReadStoragePermissionGranted()) {


                        if (!STREAM_URL.equals("")) {
                            Toast.makeText(this, "Downloading media...", Toast.LENGTH_SHORT).show();
                            Utilities.downloadDocuments(this, STREAM_URL, Fname);
                        } else {
                            Toast.makeText(this, "Unable to download ", Toast.LENGTH_SHORT).show();
                        }
                    } else
                        showPermission();

                }
        );
    }

    private boolean isReadStoragePermissionGranted() {
        return TedPermission.isGranted(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void requestPermission() {
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        findViewById(R.id.imagedownload).performClick();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not download images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private void showPermission() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogue_permission);
        Window window = dialog.getWindow();
        assert window != null;
        window.setBackgroundDrawableResource(android.R.color.transparent);

        TextView txt_decs = dialog.findViewById(R.id.txt_decs);
        txt_decs.setText("To view images, allow Familheey access to your device's photos,media, and files.");
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.btn_continue).setOnClickListener(view -> {
            dialog.dismiss();
            requestPermission();
        });
        dialog.show();
    }

    private void initializePlayer() {

        player = ExoPlayerFactory.newSimpleInstance(this);
        DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"));
        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(mediaDataSourceFactory)
                .createMediaSource(Uri.parse(STREAM_URL));
        player.prepare(mediaSource, false, false);
        player.setPlayWhenReady(true);
        playerView.setShutterBackgroundColor(Color.TRANSPARENT);
        playerView.setPlayer(player);
        playerView.requestFocus();

    }

    private void releasePlayer() {
        player.release();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) initializePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23) initializePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) releasePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) releasePlayer();
    }

}

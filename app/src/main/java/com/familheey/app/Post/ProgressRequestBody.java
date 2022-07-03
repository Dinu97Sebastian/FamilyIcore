package com.familheey.app.Post;


import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;


public class ProgressRequestBody extends RequestBody {
    private final int position;
    private File mFile;
    private UploadCallbacks mListener;
    private String content_type;

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage,int position);
//        void onError();
//        void onFinish();
    }

    public ProgressRequestBody(final File file, String content_type, final UploadCallbacks listener, int position) {
        this.content_type = content_type;
        mFile = file;
        mListener = listener;
        this.position=position;
    }



    @Override
    public MediaType contentType() {
        return MediaType.parse(content_type);
    }

    @Override
    public long contentLength() {
        return mFile.length();
    }

    @Override
    public void writeTo(@NotNull BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        try (FileInputStream in = new FileInputStream(mFile)) {
            long uploaded = 0;
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            int num = 0;
            while ((read = in.read(buffer)) != -1) {

                int progress = (int) (100 * uploaded / fileLength);
                if (progress > num + 1) {
                    // update progress on UI thread
                    num = progress;
                }

                uploaded += read;
                sink.write(buffer, 0, read);
                handler.post(new ProgressUpdater(uploaded, fileLength));

            }
        }
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;

        ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            mListener.onProgressUpdate((int)(100 * mUploaded / mTotal),position);
        }
    }

}
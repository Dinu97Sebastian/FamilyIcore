package com.familheey.app.Utilities;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;

public class ImageCompressionTask extends AsyncTask<Void, Void, List<File>> {

    private final Context context;
    private final List<File> files;
    private final OnCompressedFilesListener mListener;

    public ImageCompressionTask(Context context, OnCompressedFilesListener mListener, List<File> files) {
        this.context = context;
        this.mListener = mListener;
        this.files = files;
    }

    @Override
    protected List<File> doInBackground(Void... voids) {
        List<File> compressedFiles = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            try {
                File compressedFile = new Compressor(context)
                        .setQuality(50)
                        .compressToFile(files.get(i));
                compressedFiles.add(compressedFile);
            } catch (IOException e) {
                e.printStackTrace();
                compressedFiles.add(files.get(i));
            }
        }
        return compressedFiles;
    }

    @Override
    protected void onPostExecute(List<File> files) {
        super.onPostExecute(files);
        mListener.onImageFilesCompressed(files);
    }

    public interface OnCompressedFilesListener {
        void onImageFilesCompressed(List<File> compressedImageFiles);
    }
}

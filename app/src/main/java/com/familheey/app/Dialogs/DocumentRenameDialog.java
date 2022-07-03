package com.familheey.app.Dialogs;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.familheey.app.Models.Response.ViewContents;
import com.familheey.app.R;
import com.familheey.app.Utilities.FileUtil;
import com.familheey.app.Utilities.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DocumentRenameDialog extends DialogFragment {
    @BindView(R.id.documentName)
    EditText documentName;
    @BindView(R.id.labelChangeDocumentName)
    TextView labelChangeDocumentName;


    private DocumentRenameDialog.OnDocumentNameChanged onDocumentNameChanged;
    private String fileName="";
    private static List<Uri> fileUris;
    private static List<ViewContents.Document> documents = new ArrayList<>();
    public DocumentRenameDialog() {
        // Required empty public constructor
    }
    public static DocumentRenameDialog newInstance(List<ViewContents.Document> document, List<Uri> fileUri){
        DocumentRenameDialog fragment = new DocumentRenameDialog();
        fileUris=fileUri;
        documents=document;
        Bundle args = new Bundle();
       // args.putParcelable(fileUris, fileUri);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
//        if (getArguments() != null) {
//            document = getArguments().getParcelable(DATA);
//        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document_name_editor_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        File file;
        try {
            file =readContentToFile(fileUris.get(0));

        fileName=file.getName();
        documentName.setText(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDocumentNameChanged = Utilities.getListener(this, OnDocumentNameChanged.class);
        if (onDocumentNameChanged == null) {
            throw new RuntimeException(context.toString() + " must implement OnDocumentNameChanged");
        }
    }
    @OnClick({R.id.rename, R.id.cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rename:
                if(validate()){
                    onDocumentNameChanged.onDocumentNameChanged(documentName.getText().toString(),fileUris);
                    dismiss();
                }
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }
    private boolean validate() {
        if (documentName.getText().toString().trim().length() == 0) {
            Toast.makeText(getContext(), "Please enter document name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (documentName.getText().toString().trim().equals( fileName )) {
            Toast.makeText(getContext(), "Please enter another name", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (int i = 0; i < documents.size(); i++) {
            if (documents.get(i).getOriginalname().equals(documentName.getText().toString())){
                Toast.makeText(getContext(), "The name already exist", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public interface OnDocumentNameChanged {
        void onDocumentNameChanged(String FileName,List<Uri> fileUri);
    }

    private File readContentToFile(Uri uri) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        String timeStamp = dateFormat.format(new Date());
        final File file = new File(getActivity().getCacheDir(), getDisplayName(uri));
        try (
                final InputStream in = getActivity().getContentResolver().openInputStream(uri);
                final OutputStream out = new FileOutputStream(file, false)
        ) {
            byte[] buffer = new byte[1024];
            for (int len; (len = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, len);
            }


            return file;
        }
    }
    private String getDisplayName(Uri uri) {
        final String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
        try (
                Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null)
        ) {
            assert cursor != null;
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
        }
        return uri.getPath().replaceAll(" ", "");
    }
}

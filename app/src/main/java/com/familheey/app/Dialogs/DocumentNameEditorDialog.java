package com.familheey.app.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.ViewContents;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class DocumentNameEditorDialog extends DialogFragment {

    @BindView(R.id.labelChangeDocumentName)
    TextView labelChangeDocumentName;
    @BindView(R.id.documentName)
    EditText documentName;
    @BindView(R.id.rename)
    MaterialButton rename;
    @BindView(R.id.cancel)
    MaterialButton cancel;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ViewContents.Document document;
    private OnDocumentNameChanged onDocumentNameChanged;
    private static List<ViewContents.Document> allDocuments = new ArrayList<>();
    public DocumentNameEditorDialog() {
        // Required empty public constructor
    }

    public static DocumentNameEditorDialog newInstance(ViewContents.Document document,List<ViewContents.Document> documents) {
        DocumentNameEditorDialog fragment = new DocumentNameEditorDialog();
        Bundle args = new Bundle();
        allDocuments=documents;
        args.putParcelable(DATA, document);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        if (getArguments() != null) {
            document = getArguments().getParcelable(DATA);
        }
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
        documentName.setText(document.getOriginalname());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDocumentNameChanged = Utilities.getListener(this, OnDocumentNameChanged.class);
        if (onDocumentNameChanged == null) {
            throw new RuntimeException(context.toString() + " must implement OnDocumentNameChanged");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @OnClick({R.id.rename, R.id.cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rename:
                updateDocumentName();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    private void updateDocumentName() {
        if (documentName.getText().toString().trim().length() == 0) {
            Toast.makeText(getContext(), "Please enter document name", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < allDocuments.size(); i++) {
            if (allDocuments.get(i).getOriginalname().equals(documentName.getText().toString()) && !allDocuments.get(i).getOriginalname().equals(document.getOriginalname())){
                Toast.makeText(getContext(), "The name already exist", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        progressBar.setVisibility(View.VISIBLE);
        rename.setEnabled(false);
        cancel.setEnabled(false);
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("id", document.getId().toString());
        requestJson.addProperty("user_id", SharedPref.getUserRegistration().getId());
        requestJson.addProperty("file_name", documentName.getText().toString());//f_link
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.updateFileName(requestJson, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                progressBar.setVisibility(View.GONE);
                rename.setEnabled(true);
                cancel.setEnabled(true);
                document.setOriginalname(documentName.getText().toString());
                onDocumentNameChanged.onDocumentNameChanged(document);
                dismiss();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                Toast.makeText(getContext(), Constants.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
                rename.setEnabled(true);
                cancel.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public interface OnDocumentNameChanged {
        void onDocumentNameChanged(ViewContents.Document document);
    }
}

package com.familheey.app.Dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.familheey.app.Activities.VolunteersActivity;
import com.familheey.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DocumentsAccessDialog extends BottomSheetDialogFragment {

    @BindView(R.id.docAccess)
    MaterialCardView docAccess;

    public DocumentsAccessDialog() {
    }

    public static DocumentsAccessDialog newInstance() {
        DocumentsAccessDialog fragment = new DocumentsAccessDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_documents_access_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick({R.id.Onlyyou, R.id.Private, R.id.Everyone, R.id.Infamily, R.id.Selected, R.id.onlysome, R.id.Locimg1, R.id.Locimg2, R.id.Locimg3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.Onlyyou:
                break;
            case R.id.Private:
                break;
            case R.id.Everyone:
                break;
            case R.id.Infamily:
                break;
            case R.id.Selected:
            case R.id.onlysome:
            case R.id.Locimg3:
                Intent intent = new Intent(getContext(), VolunteersActivity.class);
                getContext().startActivity(intent);
                break;
            case R.id.Locimg1:
                break;
            case R.id.Locimg2:
                break;
        }
    }
}

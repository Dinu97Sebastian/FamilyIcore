package com.familheey.app.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FamilyEditDialogFragment extends DialogFragment implements RetrofitListener {

    public static final int TYPE_INTRODUCTION = 1;
    public static final int TYPE_FAMILY_NAME = 2;
    public static final int TYPE_HISTORY = 3;

    @BindView(R.id.labelEdit)
    TextView labelEdit;
    @BindView(R.id.editField)
    EditText editField;
    @BindView(R.id.update)
    Button update;
    @BindView(R.id.cancel)
    Button cancel;

    private int TYPE;
    private Family family;

    private OnFamilyEditCompleted mListener;
    private ProgressListener mMainInteractor;

    public FamilyEditDialogFragment() {
        // Required empty public constructor
    }

    public static FamilyEditDialogFragment newInstance(int TYPE, Family family) {
        FamilyEditDialogFragment fragment = new FamilyEditDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, family);
        args.putInt(Constants.Bundle.ID, TYPE);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert);
        if (getArguments() != null) {
            TYPE = getArguments().getInt(Constants.Bundle.ID);
            family = getArguments().getParcelable(Constants.Bundle.DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_edit_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switch (TYPE) {
            case TYPE_FAMILY_NAME:
                labelEdit.setText("Update Family Name");
                editField.setText(family.getGroupName());
                break;
            case TYPE_HISTORY:
                labelEdit.setText("Update Family History");
                editField.setText((family.getHistoryText() == null) ? "" : family.getHistoryText());
                break;
            case TYPE_INTRODUCTION:
                labelEdit.setText("Update Family Introduction");
                editField.setText(family.getIntro());
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = Utilities.getListener(this, OnFamilyEditCompleted.class);
        mMainInteractor = Utilities.getListener(this, ProgressListener.class);
        if (mListener == null) {
            throw new RuntimeException(context.toString() + " must implement OnFamilyEditCompleted");
        }
        if (mMainInteractor == null) {
            throw new RuntimeException(context.toString() + " must implement ProgressListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mMainInteractor = null;
    }

    @OnClick({R.id.update, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.update:
                updateFamily();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    private void updateFamily() {
        mMainInteractor.showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        JsonObject updateJson = new JsonObject();
        updateJson.addProperty("id", family.getId().toString());
        updateJson.addProperty("user_id", SharedPref.getUserRegistration().getId());
        switch (TYPE) {
            case TYPE_INTRODUCTION:
                updateJson.addProperty("intro", editField.getText().toString());
                break;
            case TYPE_FAMILY_NAME:
                updateJson.addProperty("group_name", editField.getText().toString());
                break;
            case TYPE_HISTORY:
                updateJson.addProperty("history_text", editField.getText().toString());
                break;
        }

        apiServiceProvider.updateFamily(updateJson, null, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        if (TYPE == TYPE_FAMILY_NAME)
            family.setGroupName(editField.getText().toString());
        else
            family.setIntro(editField.getText().toString());
        mListener.onFamilyEditCompleted(TYPE, family);
        dismiss();
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        mMainInteractor.showErrorDialog(errorData.getMessage());
    }

    public interface OnFamilyEditCompleted {
        void onFamilyEditCompleted(int TYPE, Family family);
    }

}

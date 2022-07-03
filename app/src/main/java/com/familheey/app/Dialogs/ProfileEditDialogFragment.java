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
import androidx.fragment.app.Fragment;

import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.ProfileResponse.UserProfile;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;

public class ProfileEditDialogFragment extends Fragment implements RetrofitListener {

    public static final int TYPE_INTRODUCTION = 1;
    public static final int TYPE_WORK = 2;

    @BindView(R.id.labelEdit)
    TextView labelEdit;
    @BindView(R.id.editField)
    EditText editField;
    @BindView(R.id.update)
    Button update;
    @BindView(R.id.cancel)
    Button cancel;

    private int TYPE;
    private UserProfile userProfile;

    private OnFamilyMemberEditCompleted mListener;
    private ProgressListener mMainInteractor;

    public ProfileEditDialogFragment() {
        // Required empty public constructor
    }

    public static ProfileEditDialogFragment newInstance(int TYPE, UserProfile userProfile) {
        ProfileEditDialogFragment fragment = new ProfileEditDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, userProfile);
        args.putInt(Constants.Bundle.ID, TYPE);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert);
        if (getArguments() != null) {
            TYPE = getArguments().getInt(Constants.Bundle.ID);
            userProfile = getArguments().getParcelable(Constants.Bundle.DATA);
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
        if (TYPE == TYPE_WORK) {
            labelEdit.setText("Update Work");
            if (userProfile.getProfile().getWork() != null)
                editField.setText(userProfile.getProfile().getWork());
        } else {
            labelEdit.setText("Update Introduction");
            if (userProfile.getProfile().getAbout() != null)
                editField.setText(userProfile.getProfile().getAbout());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = Utilities.getListener(this, OnFamilyMemberEditCompleted.class);
        mMainInteractor = Utilities.getListener(this, ProgressListener.class);
        if (mListener == null) {
            throw new RuntimeException(context.toString() + " must implement OnFamilyMemberEditCompleted");
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
                getActivity().getFragmentManager().popBackStack();
                //dismiss();
                break;
        }
    }

    private void updateFamily() {
        mMainInteractor.showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MediaType.parse("multipart/form-data"));
        builder.addFormDataPart("id", SharedPref.getUserRegistration().getId());
        JsonObject updateJson = new JsonObject();
        if (TYPE == TYPE_INTRODUCTION)
            builder.addFormDataPart("about", editField.getText().toString());
        else
            builder.addFormDataPart("work", editField.getText().toString());
        apiServiceProvider.updateUserProfile(builder, null, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        mMainInteractor.hideProgressDialog();
        if (TYPE == TYPE_WORK)
            userProfile.getProfile().setWork(editField.getText().toString());
        else
            userProfile.getProfile().setAbout(editField.getText().toString());
        mListener.onFamilyMemberEditCompleted(TYPE, userProfile);
       // dismiss();
        getActivity().getFragmentManager().popBackStack();
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        mMainInteractor.showErrorDialog(errorData.getMessage());
    }

    public interface OnFamilyMemberEditCompleted {
        void onFamilyMemberEditCompleted(int TYPE, UserProfile userProfile);
    }

}

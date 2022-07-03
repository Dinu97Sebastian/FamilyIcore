package com.familheey.app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.familheey.app.Utilities.Constants.Bundle.IS_INVITATION;

public class FragmentOthersShare extends Fragment {

    @BindView(R.id.editTextName)
    EditText editTextName;
    @BindView(R.id.editTextCountryPick)
    CountryCodePicker editTextCountryPick;
    @BindView(R.id.editTextPhone)
    EditText editTextPhone;
    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.materialButtonInvite)
    MaterialButton materialButtonInvite;
    @BindView(R.id.progressInvite)
    ProgressBar progressInvite;

    private boolean isInvitation = false;
    private String eventId = "";

    public static Fragment newInstance(String eventId, boolean isInvitation) {
        FragmentOthersShare fragment = new FragmentOthersShare();
        Bundle args = new Bundle();
        args.putBoolean(IS_INVITATION, isInvitation);
        args.putString(Constants.Bundle.DATA, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_invite_friends, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInvitation = getArguments().getBoolean(IS_INVITATION);
        eventId = getArguments().getString(Constants.Bundle.DATA);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        materialButtonInvite.setOnClickListener(view1 -> {
            if (validated()) {
                callApi();
            }
        });
        if (isInvitation)
            materialButtonInvite.setText("Invite");
        else
            materialButtonInvite.setText("Share");
    }

    private void callApi() {

        showProgress();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("event_id", eventId);
        jsonObject.addProperty("from_user", SharedPref.getUserRegistration().getId());

        jsonObject.addProperty("view", "template1");
        if (isInvitation)
            jsonObject.addProperty("type", "invitation");
        else
            jsonObject.addProperty("type", "share");

        JsonObject innerObject = new JsonObject();

        innerObject.addProperty("full_name", editTextName.getText().toString());
        innerObject.addProperty("email", editTextEmail.getText().toString());
        innerObject.addProperty("phone", "+" + editTextCountryPick.getFullNumberWithPlus() + editTextPhone.getText().toString());
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(innerObject);


        jsonObject.add("others", jsonArray);


        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
        apiServiceProvider.eventGroupInvite(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                Toast.makeText(getActivity(), "Invited successfully", Toast.LENGTH_SHORT).show();
                clearData();
                hideProgress();
                getActivity().finish();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgress();
                Toast.makeText(getActivity(), errorData.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void clearData() {

        editTextPhone.setText("");
        editTextName.setText("");
        editTextEmail.setText("");
    }


    void showProgress() {
        if (progressInvite != null) {
            progressInvite.setVisibility(View.VISIBLE);
        }
    }

    private boolean validated() {

        if (editTextName.getText().toString().trim().length() <= 2) {
            Toast.makeText(getActivity(), "Name must be at least 3 character", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidName(editTextName.getText().toString().trim().replace(" ", ""))) {
            Toast.makeText(getActivity(), "No special characters allowed", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!editTextPhone.getText().toString().isEmpty()&&editTextPhone.getText().toString().length() < 6) {
            Toast.makeText(getActivity(), "Enter valid phone", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!editTextEmail.getText().toString().isEmpty()&&!isEmailValid(editTextEmail.getText().toString())) {
            Toast.makeText(getActivity(), "Check your Email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (editTextEmail.getText().toString().isEmpty()&&editTextPhone.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Enter either Email or Phone", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean isValidName(String name) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(name);
        boolean b = m.find();
        return !b;
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    void hideProgress() {
        if (progressInvite != null) {
            progressInvite.setVisibility(View.GONE);
        }
    }
}

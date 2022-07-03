package com.familheey.app.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Activities.InviteFriendsActivity.isEmailValid;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class GroupInviteFragment extends Fragment implements RetrofitListener {

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.countryCodePicker)
    CountryCodePicker countryCodePicker;
    @BindView(R.id.phoneNumber)
    EditText phoneNumber;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.invite)
    androidx.appcompat.widget.AppCompatButton invite;

    private Family family;
    private ProgressListener mListener;

    public GroupInviteFragment() {
        // Required empty public constructor
    }

    public static GroupInviteFragment newInstance(Family family) {
        GroupInviteFragment fragment = new GroupInviteFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, family);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            family = getArguments().getParcelable(DATA);
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_invite, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mListener = Utilities.getListener(this, ProgressListener.class);
        if (mListener == null)
            throw new RuntimeException(context.toString() + " must implement FamilyDashboardListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private boolean validated() {
        if (name.getText().toString().trim().length() <= 2) {
            Toast.makeText(getActivity(), "Name must be at least 3 character", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidName(name.getText().toString().trim().replace(" ", ""))) {
            Toast.makeText(getActivity(), "No special characters allowed", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!phoneNumber.getText().toString().isEmpty()&&phoneNumber.getText().toString().length() < 6) {
            Toast.makeText(getActivity(), "Enter valid phone", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!email.getText().toString().isEmpty()&&!isEmailValid(email.getText().toString())) {
            Toast.makeText(getActivity(), "Check your Email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email.getText().toString().isEmpty()&&phoneNumber.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Enter either Email or Phone", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    private boolean isValidName(String name) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(name);
        boolean b = m.find();
        return !b;
    }

    private void inviteFriendsToFamily() {
        mListener.showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("phone", "+" + countryCodePicker.getFullNumberWithPlus() + phoneNumber.getText().toString());
        if (Utilities.isValidEmail(email.getText().toString().trim()))
            jsonObject.addProperty("email", email.getText().toString());
        else jsonObject.addProperty("email", "");
        jsonObject.addProperty("group_id", family.getId().toString());
        jsonObject.addProperty("name", name.getText().toString());
        jsonObject.addProperty("from_name", SharedPref.getUserRegistration().getFullName());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.inviteViaSms(jsonObject, null, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        mListener.hideProgressDialog();
        phoneNumber.setText("");
        email.setText("");
        name.setText("");
        Toast.makeText(getContext(), "Invitation sent!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        mListener.showErrorDialog(errorData.getMessage());
    }

    @OnClick({R.id.invite})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.invite:
                if (!validated()) {
                    return;
                }
                inviteFriendsToFamily();
                break;
        }
    }

    public void updateFamily(Family family) {
        this.family = family;
    }
}

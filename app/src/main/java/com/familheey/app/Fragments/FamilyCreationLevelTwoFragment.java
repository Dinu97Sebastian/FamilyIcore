package com.familheey.app.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.familheey.app.Interfaces.FamilyCreationListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.FamilyCreation;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Utilities.hideSoftKeyboard;

public class FamilyCreationLevelTwoFragment extends Fragment implements RetrofitListener {

    @BindView(R.id.familyIntro)
    TextInputEditText familyIntro;
    @BindView(R.id.proceed)
    MaterialButton proceed;

    private FamilyCreationListener mListener;
    private Family family;
    private FamilyCreation familyCreation;

    public FamilyCreationLevelTwoFragment() {
        // Required empty public constructor
    }

    //  Rename and change types and number of parameters
    public static FamilyCreationLevelTwoFragment newInstance(Family family) {
        FamilyCreationLevelTwoFragment fragment = new FamilyCreationLevelTwoFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, family);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            family = getArguments().getParcelable(Constants.Bundle.DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_family_creation_level_two, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        familyCreation = mListener.getFamilyCreation();
        familyIntro.setText(familyCreation.getIntroduction());
        //Utilities.setupUI(familyIntro, getActivity());
    }

    private boolean isValid() {
        boolean isValid = true;
        if (familyIntro.getText().toString().length() == 0) {
            isValid = false;
            familyIntro.setError("Required");
        }
        return isValid;
    }

    private void addFamilyIntroduction() {
        mListener.showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        JsonObject updateJson = new JsonObject();
        updateJson.addProperty("id", family.getId().toString());
        updateJson.addProperty("intro", familyIntro.getText().toString());
        updateJson.addProperty("user_id", SharedPref.getUserRegistration().getId());
        apiServiceProvider.updateFamily(updateJson, null, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FamilyCreationListener) {
            mListener = (FamilyCreationListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FamilyCreationResponse");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        mListener.hideProgressDialog();
        try {
            family = FamilyParser.parseFamily(responseBodyString);
            familyCreation.setIntroduction(family.getIntro());
            mListener.loadFamilyCreationLevelThree(family);
        } catch (NullPointerException | JsonParseException e) {
            e.printStackTrace();
            mListener.showErrorDialog(Constants.SOMETHING_WRONG);
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        mListener.showErrorDialog(Constants.SOMETHING_WRONG);
    }

    @OnClick({R.id.proceed})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.proceed) {
            hideSoftKeyboard(getActivity());
            familyCreation.setIntroduction(familyIntro.getText().toString());
            mListener.loadFamilyCreationLevelThree(family);
        }
    }
}

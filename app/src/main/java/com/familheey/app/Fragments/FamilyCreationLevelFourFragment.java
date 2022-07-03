package com.familheey.app.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Activities.LinkFamilyActivity;
import com.familheey.app.CustomViews.Buttons.RegularButton;
import com.familheey.app.Dialogs.SelectFamiliesDialog;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;
import static com.familheey.app.Utilities.Constants.Bundle.IS_CREATED_NOW;
import static com.familheey.app.Utilities.Constants.Bundle.TO_CREATE_FAMILY;


public class FamilyCreationLevelFourFragment extends Fragment implements RetrofitListener {

    @BindView(R.id.proceed)
    RegularButton proceed;
    @BindView(R.id.privateFamily)
    SwitchCompat privateFamily;
    @BindView(R.id.searchableFamily)
    SwitchCompat searchableFamily;
    @BindView(R.id.linkOtherFamilies)
    SwitchCompat linkOtherFamilies;
    @BindView(R.id.advancedSettings)
    TextView advancedSettings;


    private FamilyCreationListener mListener;
    private FamilyCreation familyCreation;
    private Family family;
    private List<Family> families = null;
    private List<Family> selectedFamilies = new ArrayList<>();
    private Boolean toCreatedFamily = false;
    public FamilyCreationLevelFourFragment() {
        // Required empty public constructor
    }

    //  Rename and change types and number of parameters
    public static FamilyCreationLevelFourFragment newInstance(Family family,boolean toCreateFamily) {
        FamilyCreationLevelFourFragment fragment = new FamilyCreationLevelFourFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, family);
        args.putBoolean(TO_CREATE_FAMILY, toCreateFamily);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            family = getArguments().getParcelable(DATA);
            toCreatedFamily=getArguments().getBoolean(TO_CREATE_FAMILY, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_creation_level_four, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    CompoundButton.OnCheckedChangeListener linkOtherFamiliesListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            familyCreation.setLinkable(linkOtherFamilies.isChecked());
            if (!linkOtherFamilies.isChecked())
                return;
            Intent intent = new Intent(getContext(), LinkFamilyActivity.class);
            intent.putExtra(IDENTIFIER, "Vodka");
            intent.putExtra(DATA, family);
            startActivity(intent);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FamilyCreationListener) {
            mListener = (FamilyCreationListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FamilyCreationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    CompoundButton.OnCheckedChangeListener searchableFamilyListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            familyCreation.setSearchable(searchableFamily.isChecked());
        }
    };

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        mListener.showErrorDialog(Constants.SOMETHING_WRONG);
    }

    @OnClick({R.id.advancedSettings, R.id.proceed})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.advancedSettings:
                advancedSettings.setTag(Constants.Selections.SELECTED);
                updateFamilyDetails(false);
                break;
            case R.id.proceed:
                advancedSettings.setTag(Constants.Selections.UN_SELECTED);
                updateFamilyDetails(true);
                break;
        }
    }

    CompoundButton.OnCheckedChangeListener privateFamilyListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            familyCreation.setPrivate(privateFamily.isChecked());
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        familyCreation = mListener.getFamilyCreation();
        privateFamily.setChecked(familyCreation.isPrivate());
        searchableFamily.setChecked(familyCreation.isSearchable());
        linkOtherFamilies.setOnCheckedChangeListener(null);
        linkOtherFamilies.setChecked(familyCreation.isLinkable());
        linkOtherFamilies.setOnCheckedChangeListener(linkOtherFamiliesListener);
        privateFamily.setOnCheckedChangeListener(privateFamilyListener);
        searchableFamily.setOnCheckedChangeListener(searchableFamilyListener);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        mListener.hideProgressDialog();
        try {
            switch (apiFlag) {
                case Constants.ApiFlags.UPDATE_FAMILY:
                    family = FamilyParser.parseFamily(responseBodyString);
                    if (advancedSettings.getTag().toString().equalsIgnoreCase(Constants.Selections.SELECTED)) {
                        if (familyCreation.isPrivate()) {
                            family.setPrivateDefault();
                        } else {
                            family.setPublicDefault();
                        }
                        mListener.loadFamilyCreationLevelAdvanced(family);
                    } else {
                        Intent familyIntent = new Intent(getContext(), FamilyDashboardActivity.class);
                        SharedPref.setUserHasFamily(true);
                        familyIntent.putExtra(DATA, family);
                        familyIntent.putExtra(IS_CREATED_NOW, true);
                        familyIntent.putExtra(TO_CREATE_FAMILY, toCreatedFamily);
                        startActivity(familyIntent);
                        getActivity().finish();
                    }
                    break;
                case Constants.ApiFlags.REQUEST_FAMILY_LINKING:
                    linkOtherFamilies.setOnCheckedChangeListener(null);
                    linkOtherFamilies.setChecked(true);
                    linkOtherFamilies.setOnCheckedChangeListener(linkOtherFamiliesListener);
                    break;
                default:
                    families = FamilyParser.parseLinkedFamilies(responseBodyString);
                    SelectFamiliesDialog.newInstance(this, families).show(getFragmentManager(), "SelectFamiliesDialog");
                    break;
            }
        } catch (NullPointerException | JsonParseException e) {
            e.printStackTrace();
            mListener.showErrorDialog(Constants.SOMETHING_WRONG);
        }
    }

    private void updateFamilyDetails(boolean isActive) {
        mListener.showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        JsonObject updateJsonObject = new JsonObject();
        familyCreation.setPrivate(privateFamily.isChecked());
        familyCreation.setSearchable(searchableFamily.isChecked());
        familyCreation.setLinkable(linkOtherFamilies.isChecked());
        updateJsonObject.addProperty("intro", familyCreation.getIntroduction());
        updateJsonObject.addProperty("group_type", privateFamily.isChecked() ? "private" : "public");
        updateJsonObject.addProperty("searchable", searchableFamily.isChecked());
        updateJsonObject.addProperty("is_linkable", linkOtherFamilies.isChecked());
        updateJsonObject.addProperty("is_active", isActive);
        updateJsonObject.addProperty("id", family.getId().toString());
        updateJsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        apiServiceProvider.updateFamily(updateJsonObject, null, this);
    }

    void requestFamilyLinking(List<Family> selectedFamilies) {
        mListener.showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("from_group", family.getId());
        jsonObject.addProperty("requested_by", SharedPref.getUserRegistration().getId());
        JsonArray selectedFamiliesIds = new JsonArray();
        for (Family family : selectedFamilies)
            selectedFamiliesIds.add(family.getId());
        jsonObject.add("to_group", selectedFamiliesIds);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.requestFamiliesForLinking(jsonObject, null, this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RequestCode.REQUEST_CODE) {
            selectedFamilies = data.getParcelableArrayListExtra(DATA);
            if (selectedFamilies != null && selectedFamilies.size() > 0) {
                requestFamilyLinking(selectedFamilies);
            } else {
                linkOtherFamilies.setOnCheckedChangeListener(null);
                linkOtherFamilies.setChecked(true);
                linkOtherFamilies.setOnCheckedChangeListener(linkOtherFamiliesListener);
            }
        }
    }
}

package com.familheey.app.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.FamilyJoiningAdapter;
import com.familheey.app.Interfaces.FamilyCreationListener;
import com.familheey.app.Interfaces.FamilyJoiningListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Interfaces.RetryListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.FamilyCreation;
import com.familheey.app.Models.Request.CreateFamilyRequest;
import com.familheey.app.Models.Request.SimilarFamilyRequest;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class SimilarFamiliesFragment extends Fragment implements RetrofitListener, RetryListener, FamilyJoiningListener {

    @BindView(R.id.similarFamilyList)
    RecyclerView similarFamilyList;
    @BindView(R.id.addNewFamily)
    Button addNewFamily;

    private FamilyCreationListener mListener;
    private Family family;
    private SimilarFamilyRequest similarFamilyRequest;
    private FamilyCreation familyCreation;
    private ArrayList<Family> similarFamilies = new ArrayList<>();
    private FamilyJoiningAdapter familyJoiningAdapter;

    public SimilarFamiliesFragment() {
        // Required empty public constructor
    }

    public static SimilarFamiliesFragment newInstance(SimilarFamilyRequest similarFamilyRequest, ArrayList<Family> similarFamilies) {
        SimilarFamiliesFragment fragment = new SimilarFamiliesFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, similarFamilyRequest);
        args.putParcelableArrayList(Constants.Bundle.FAMILIES, similarFamilies);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            similarFamilyRequest = getArguments().getParcelable(DATA);
            similarFamilies = getArguments().getParcelableArrayList(Constants.Bundle.FAMILIES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_similar_families, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSimilarFamilies();
        familyCreation = mListener.getFamilyCreation();
    }

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

    private void fetchSimilarFamilies() {
        mListener.showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.fetchSimilarFamilies(similarFamilyRequest, null, this);
    }

    private void createFamily() {
        mListener.showProgressDialog();
        CreateFamilyRequest createFamilyRequest = new CreateFamilyRequest();
        createFamilyRequest.setBaseRegion(similarFamilyRequest.getBaseRegion());
        createFamilyRequest.setGroupCategory(similarFamilyRequest.getGroupCategory());
        createFamilyRequest.setGroupName(similarFamilyRequest.getGroupName());
        createFamilyRequest.setActive(false);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.createFamily(createFamilyRequest, null, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        mListener.hideProgressDialog();
        switch (apiFlag) {
            case Constants.ApiFlags.FETCH_SIMILAR_FAMILIES:
                try {
                    similarFamilies.clear();
                    similarFamilies.addAll(FamilyParser.parseLinkedFamilies(responseBodyString));
                    familyJoiningAdapter.notifyDataSetChanged();
                } catch (NullPointerException | JsonParseException e) {
                    e.printStackTrace();
                    mListener.showErrorDialog("Please try again later !! Something wrong");
                }
                break;
            case Constants.ApiFlags.JOIN_FAMILY:
                for (int i = 0; i < similarFamilies.size(); i++) {
                    if (similarFamilies.get(i).getId().equals(apiCallbackParams.getFamily().getId())) {
                        similarFamilies.get(i).setIsJoined(true);
                        addNewFamily.setTag("exit");
                        addNewFamily.setText("Close");
                        break;
                    }
                }
                familyJoiningAdapter.notifyDataSetChanged();
                SweetAlertDialog sweetAlertDialog = Utilities.succesDialog(getActivity(), "Request sent");
                sweetAlertDialog.show();
                Utilities.addPositiveButtonMargin(sweetAlertDialog);
                sweetAlertDialog.setOnDismissListener(dialogInterface -> {
                    getActivity().finish();
                });
                break;
            default:
                try {
                    family = FamilyParser.parseFamily(responseBodyString);
                    mListener.loadFamilyCreationLevelTwo(family);
                } catch (NullPointerException | JsonParseException e) {
                    e.printStackTrace();
                    mListener.showErrorDialog("Please try again later !! Somrthing wrong");
                }
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        mListener.showErrorDialog(Constants.SOMETHING_WRONG);
        switch (apiFlag) {
            case Constants.ApiFlags.JOIN_FAMILY:
                /*for (int i = 0; i < similarFamilies.size(); i++) {
                    if (similarFamilies.get(i).getId().equals(apiCallbackParams.getFamily().getId())) {
                        similarFamilies.get(i).setDevSelected(true);
                        addNewFamily.setTag("exit");
                        addNewFamily.setText("Close");
                        break;
                    }
                }
                familyJoiningAdapter.notifyDataSetChanged();*/
                break;
        }
    }

    @Override
    public void retryApi(int ApiFlag) {
        fetchSimilarFamilies();
    }

    private void initSimilarFamilies() {
        familyJoiningAdapter = new FamilyJoiningAdapter(this, similarFamilies);
        similarFamilyList.setAdapter(familyJoiningAdapter);
        similarFamilyList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @OnClick(R.id.addNewFamily)
    public void onViewClicked() {
        if (addNewFamily.getTag().toString().equalsIgnoreCase("new"))
            createFamily();
        else
            getActivity().finish();
    }

    @Override
    public void requestFamilyJoining(Family family) {
        mListener.showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        JsonObject jsonObject = new JsonObject();
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setFamily(family);
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("group_id", family.getId().toString());
        apiServiceProvider.joinFamily(jsonObject, apiCallbackParams, this);
    }
}

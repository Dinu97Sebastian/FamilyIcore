package com.familheey.app.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class ChangeFamilyURLFragment extends DialogFragment {

    @BindView(R.id.familyUrl)
    TextInputEditText familyUrl;
    @BindView(R.id.change)
    MaterialButton change;
    @BindView(R.id.cancel)
    MaterialButton cancel;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.baseShareUrl)
    TextView baseShareUrl;
    private OnFamilyUrlChangedListener mListener;
    private Family family;

    public ChangeFamilyURLFragment() {
        // Required empty public constructor
    }

    public static ChangeFamilyURLFragment newInstance(Family family) {
        ChangeFamilyURLFragment fragment = new ChangeFamilyURLFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, family);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        if (getArguments() != null) {
            family = getArguments().getParcelable(DATA);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (family.getUrlPath() != null) {
            familyUrl.setText(family.getUrlPathOnly());
        } else familyUrl.setText("");
        baseShareUrl.setText("" + family.getSharingbasePath());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_family_url, container, false);
        ButterKnife.bind(this, view);
        setCancelable(false);
        return view;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFamilyUrlChangedListener)
            mListener = (OnFamilyUrlChangedListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement OnFamilyUrlChangedListener");*/
        mListener = Utilities.getListener(this, OnFamilyUrlChangedListener.class);
        if (mListener == null) {
            throw new RuntimeException(context.toString() + " must implement OnFamilyUrlChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick({R.id.change, R.id.cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change:
                updateFamilyUrl();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }


    private void updateFamilyUrl() {

        if (familyUrl.getText().toString().trim().length() == 0) {
            Toast.makeText(getContext(), "Please enter family url", Toast.LENGTH_SHORT).show();
            return;
        }
        checkUrl();

    }


    private void checkUrl() {

        progressBar.setVisibility(View.VISIBLE);
        CompositeDisposable subscriptions = new CompositeDisposable();
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("f_text", Objects.requireNonNull(familyUrl.getText()).toString());
        RequestBody requestBody = RequestBody.create(jsonObject1.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        ApiServices apiServices = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        assert application != null;
        subscriptions.add(apiServices.familyLinkExist(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    if ("0".equals(response.getAsJsonPrimitive("count").getAsString()))
                        updateUrl();
                    else {

                        progressBar.setVisibility(View.GONE);
                        Toast toast = Toast.makeText(getActivity(), "URL is already in use.Please enter another one.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }, throwable ->
                {
                }));
    }

    private void updateUrl() {

        progressBar.setVisibility(View.VISIBLE);
        change.setEnabled(false);
        cancel.setEnabled(false);
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("id", family.getId().toString());
        requestJson.addProperty("user_id", SharedPref.getUserRegistration().getId());
        requestJson.addProperty("f_text", Objects.requireNonNull(familyUrl.getText()).toString());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.updateFamily(requestJson, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                progressBar.setVisibility(View.GONE);
                change.setEnabled(true);
                cancel.setEnabled(true);
                Family parsedFamily = FamilyParser.parseFamily(responseBodyString);
                if (parsedFamily != null)
                    family = parsedFamily;
                mListener.onFamilyUrlChanged(parsedFamily);
                dismiss();

            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                Toast.makeText(getContext(), Constants.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
                change.setEnabled(true);
                cancel.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public interface OnFamilyUrlChangedListener {
        void onFamilyUrlChanged(Family family);
    }
}

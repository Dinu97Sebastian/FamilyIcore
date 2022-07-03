package com.familheey.app.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Dialogs.ChangeFamilyURLFragment;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Fragments.FamilyDashboard.BackableFragment;
import com.familheey.app.Interfaces.FamilyDashboardListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Stripe.Error;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.exoplayer2.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.luseen.autolinklibrary.AutoLinkMode;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class FamilySettingsFragment extends BackableFragment implements RetrofitListener, ChangeFamilyURLFragment.OnFamilyUrlChangedListener  {

    @BindView(R.id.goBack)
    ImageView goBack;
    public CompositeDisposable subscriptions;
    @BindView(R.id.labelbankAddUrl)
    RelativeLayout labelbankAddUrl;
    @BindView(R.id.img_status)
    ImageView img_status;
    @BindView(R.id.txt_account_number)
    TextView txt_account_number;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.followGroup)
    Group followGroup;
    @BindView(R.id.unFollowGroup)
    Group unFollowGroup;
    @BindView(R.id.deleteGroup)
    Group deleteGroup;
    @BindView(R.id.editFamilyUrl)
    ImageView editFamilyUrl;
    @BindView(R.id.familyUrl)
    TextView familyUrl;
    @BindView(R.id.leaveFamilyGroup)
    Group leaveFamilyGroup;
    @BindView(R.id.basicSettingsGroup)
    Group basicSettingsGroup;
    @BindView(R.id.txt_status)
    TextView txt_status;
    @BindView(R.id.txt_error_msg)
    TextView txt_error_msg;
    @BindView(R.id.advancedSettingsGroup)
    Group advancedSettingsGroup;
    @BindView(R.id.addbank)
    Group addbank;
    SweetAlertDialog progressDialog;
    private Family family;
    private boolean isFamilyUpdated = false;
    private FamilyDashboardListener mListener;

    public FamilySettingsFragment() {
        // Required empty public constructor
    }

    public static FamilySettingsFragment newInstance(Family family) {
        FamilySettingsFragment fragment = new FamilySettingsFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_settings, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        subscriptions = new CompositeDisposable();
        initializeToolbar();
        initializeRestrictions();
        initializePrefill();
    }

    @Override
    public void onBackButtonPressed() {
        //((FamilyDashboardActivity)requireActivity()).backAction();
        requireActivity().onBackPressed();
    }

    private void initializePrefill() {
        if (family.getUrlPath() != null && family.getGroupType().equalsIgnoreCase("public")){
            familyUrl.setText(family.getWebUrlPath());
        }else if (family.getUrlPath() != null && family.getGroupType().equalsIgnoreCase("private")){
            familyUrl.setText(family.getUrlPath());
        }else{
            familyUrl.setText("");
        }
    }

    private void initializeRestrictions() {

        if (family.getFollowing() != null && family.getFollowing()) {
            followGroup.setVisibility(View.GONE);
            unFollowGroup.setVisibility(View.VISIBLE);
        } else {
            followGroup.setVisibility(View.VISIBLE);
            unFollowGroup.setVisibility(View.GONE);
        }
        if (family.getUserStatus() != null && family.getUserStatus().equalsIgnoreCase("admin")) {
            deleteGroup.setVisibility(View.VISIBLE);
            editFamilyUrl.setVisibility(View.VISIBLE);
            leaveFamilyGroup.setVisibility(View.VISIBLE);
            basicSettingsGroup.setVisibility(View.VISIBLE);
            advancedSettingsGroup.setVisibility(View.VISIBLE);
            addbank.setVisibility(View.VISIBLE);
            if (family.getStripe_account_id() != null) {
                checkBusinessAccountStatus();
                txt_account_number.setText(family.getStripe_account_id());
            } else {
                txt_account_number.setText("Add Stripe Account");
                txt_status.setVisibility(View.GONE);
                img_status.setVisibility(View.GONE);
            }
        } else {
            deleteGroup.setVisibility(View.GONE);
            editFamilyUrl.setVisibility(View.INVISIBLE);
            if (family.isAnonymous())
                leaveFamilyGroup.setVisibility(View.GONE);
            basicSettingsGroup.setVisibility(View.GONE);
            advancedSettingsGroup.setVisibility(View.GONE);
            addbank.setVisibility(View.GONE);
        }
    }

    private void initializeToolbar() {
        toolBarTitle.setText("Family Settings");
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof FamilyDashboardListener) {
            mListener = (FamilyDashboardListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FamilyDashboardListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void followFamily() {
        mListener.showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("group_id", family.getId().toString());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.followtheFamily(jsonObject, null, this);
    }

    private void unFollowFamily() {
        mListener.showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("group_id", family.getId().toString());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.unFollowtheFamily(jsonObject, null, this);
    }

    private void leaveFamily() {
        mListener.showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("group_id", family.getId().toString());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.leaveFamily(jsonObject, null, this);
    }

    private void deleteFamily() {
        String familyName = this.family.getGroupName();
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Delete")
                .setMessage("Do you really want to delete " + familyName + "?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    mListener.showProgressDialog();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
                    jsonObject.addProperty("id", family.getId().toString());
                    jsonObject.addProperty("is_active", false);
                    jsonObject.addProperty("is_removed", true);
                    jsonObject.addProperty("action", "delete_family");
                    ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
                    apiServiceProvider.updateFamily(jsonObject, null, FamilySettingsFragment.this);

                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss()).show();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 20, 0);
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setLayoutParams(params);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        isFamilyUpdated = true;
        mListener.hideProgressDialog();
        switch (apiFlag) {
            case Constants.ApiFlags.LEAVE_FAMILY:
                Toast.makeText(getContext(), "You have left this family", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
                break;
            case Constants.ApiFlags.FOLLOW:
                family.setFollowing(true);
                initializeRestrictions();
                break;
            case Constants.ApiFlags.UNFOLLOW:
                family.setFollowing(false);
                initializeRestrictions();
                break;
            case Constants.ApiFlags.UPDATE_FAMILY:
                requireActivity().finish();
                break;
        }
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

    }

    public void showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(getActivity());
        progressDialog.show();

    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        mListener.showErrorDialog(errorData.getMessage());
    }

    @OnClick({R.id.img_status, R.id.labelbankAddUrl, R.id.goBack, R.id.labelFollow, R.id.labelunFollow, R.id.labelLeaveFamily, R.id.labelBasicSettings, R.id.labelAdvancedSettings, R.id.labelDeleteFamily, R.id.editFamilyUrl, R.id.shareFamilyUrl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_status:
                if (txt_status.getText().equals("Pending")) {

                }
                break;
            case R.id.labelbankAddUrl:
                if (family.getStripe_account_id() == null) {
                    generateBankAddLink();
                }
                break;

            case R.id.goBack:
                if (isFamilyUpdated)
                    mListener.onFamilyUpdated(true);
                else
                    requireActivity().onBackPressed();
                break;
            case R.id.labelFollow:
                followFamily();
                break;
            case R.id.labelunFollow:
                unFollowFamily();
                break;
            case R.id.labelLeaveFamily:
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Do you want to leave this family?")
                        .setConfirmText("Yes")
                        .setCancelText("No");
                sweetAlertDialog.show();
                Utilities.addPositiveButtonMargin(sweetAlertDialog);
                Utilities.addNegativeButtonMargin(sweetAlertDialog);
                sweetAlertDialog.setCancelClickListener(dialogInterface -> sweetAlertDialog.dismiss());
                sweetAlertDialog.setConfirmClickListener(sweetAlertDialog1 -> {
                    sweetAlertDialog.dismiss();
                    leaveFamily();
                });
                break;
            case R.id.labelBasicSettings:
                mListener.loadFamilyBasicSettings(family);
                break;
            case R.id.labelAdvancedSettings:
                mListener.loadFamilyAdvancedSettings(family);
                break;
            case R.id.labelDeleteFamily:
                deleteFamily();
                break;
            case R.id.editFamilyUrl:
                ChangeFamilyURLFragment.newInstance(family).show(getChildFragmentManager(), "ChangeFamilyURLFragment");
                break;
            case R.id.shareFamilyUrl:
                shareFamilyUrl();
                break;
        }
    }

    private void shareFamilyUrl() {
        SharedPref.write(SharedPref.FAMILY_LINK,"");
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, family.getGroupName());
        SharedPref.write(SharedPref.FAMILY_LINK,family.getUrlPath());
        if (family.getGroupType().equalsIgnoreCase("private")) {
            i.putExtra(Intent.EXTRA_TEXT, family.getUrlPath());
        } else if (family.getGroupType().equalsIgnoreCase("public")) {
            i.putExtra(Intent.EXTRA_TEXT, family.getWebUrlPath());
        }
        startActivity(Intent.createChooser(i, "Share " + family.getGroupName()));
    }

    @Override
    public void onFamilyUrlChanged(Family family) {
        this.family = family;
        initializePrefill();
    }


    private void generateBankAddLink() {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("group_id", family.getId().toString());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        final ApiServices authService = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        subscriptions.add(authService.stripe_oauth_link_generation(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                            if (response.code() == 200) {
                                hideProgressDialog();
                                assert response.body() != null;
                                String url = response.body().get("link").getAsString();
                                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                                CustomTabsIntent customTabsIntent = builder.build();
                                customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
                            }
                        }, throwable -> hideProgressDialog()
                ));
    }

    private void checkBusinessAccountStatus() {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("group_id", family.getId().toString());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        final ApiServices authService = RetrofitBase.createRxResource(getActivity(), ApiServices.class);
        FamilheeyApplication application = FamilheeyApplication.get(getActivity());
        subscriptions.add(authService.stripeGetaccountById(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                            hideProgressDialog();
                            if (response.code() == 200) {
                                assert response.body() != null;
                                if (response.body().get("payouts_enabled").getAsBoolean() &&
                                        response.body().get("charges_enabled").getAsBoolean()) {
                                    txt_status.setText("Active");
                                    img_status.setImageResource(R.drawable.ic_check_circle_black_24dp);
                                }
                            } else if (response.code() == 500) {
                                img_status.setImageResource(R.drawable.ic_error_outline_black_24dp);
                                txt_status.setText("Error");
                                Type type = new TypeToken<Error>() {
                                }.getType();
                                Error errorResponse = new Gson().fromJson(response.errorBody().charStream(), type);
                                txt_error_msg.setText(errorResponse.getMessage());
                            } else {
                                img_status.setImageResource(R.drawable.ic_error_outline_black_24dp);
                                txt_status.setText("Pending");
                            }
                        }, throwable -> {
                            hideProgressDialog();
                        }

                ));
    }



    private void dialogOpenLink() {
        final Dialog dialog = new Dialog(requireActivity());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogue_user_block);
        com.luseen.autolinklibrary.AutoLinkTextView textView = dialog.findViewById(R.id.txt_msg);
        TextView btn_close = dialog.findViewById(R.id.btn_close);
        textView.addAutoLinkMode(AutoLinkMode.MODE_URL);
        textView.setUrlModeColor(ContextCompat.getColor(requireActivity(), R.color.buttoncolor));
        dialog.setCanceledOnTouchOutside(false);
        btn_close.setText("Open");
        textView.setAutoLinkText("Your account is not active, few details might be missing, open the link below and sign into stripe to see details.\nhttps://dashboard.stripe.com");

        textView.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
            dialog.dismiss();
            openLink();
        });

        btn_close.setOnClickListener(view -> {
            dialog.dismiss();
            openLink();
        });
        dialog.show();
    }

    private void openLink() {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(requireActivity(), Uri.parse("https://dashboard.stripe.com"));
    }
}

package com.familheey.app.Fragments.Events;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.EventSignUp;
import com.familheey.app.Models.Response.SignUpContributor;
import com.familheey.app.Need.InputFilterMinMax;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.CornerFamily;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.ApiFlags.ADD_EVENTS_SIGNUP;
import static com.familheey.app.Utilities.Constants.ApiFlags.UPDATE_EVENTS_SIGNUP;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;


public class EventSignupDialog extends BottomSheetDialogFragment implements RetrofitListener {


    @BindView(R.id.signUpCount)
    EditText signUpCount;
    @BindView(R.id.signUpContainer)
    MaterialCardView signUpContainer;
    @BindView(R.id.signUpDetails)
    TextView signUpDetails;
    @BindView(R.id.currentCount)
    TextView currentCount;
    @BindView(R.id.ofCount)
    TextView ofCount;

    private OnSignupUpdatedListener mListener;
    private ProgressListener progressListener;

    private boolean isUpdateMode = false;
    private EventSignUp eventSignUp;
    private SignUpContributor signUpContributor;

    public EventSignupDialog() {
        // Required empty public constructor
    }

    public static EventSignupDialog newInstance(EventSignUp eventSignUp) {
        EventSignupDialog fragment = new EventSignupDialog();
        Bundle args = new Bundle();
        args.putParcelable(DATA, eventSignUp);
        args.putBoolean(TYPE, false);
        fragment.setArguments(args);
        return fragment;
    }

    public static EventSignupDialog newInstance(SignUpContributor signUpContributor) {
        EventSignupDialog fragment = new EventSignupDialog();
        Bundle args = new Bundle();
        args.putParcelable(DATA, signUpContributor);
        args.putBoolean(TYPE, true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isUpdateMode = getArguments().getBoolean(TYPE, false);
            if (isUpdateMode) {
                signUpContributor = getArguments().getParcelable(DATA);
            } else {
                eventSignUp = getArguments().getParcelable(DATA);
            }
        }
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_signup_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signUpCount.setFilters(new InputFilter[]{new InputFilterMinMax("0", "999999")});
        applyStyle();
        if (isUpdateMode) {
            prefillDatas(signUpContributor);
        } else prefillDatas(eventSignUp);
    }

    private void prefillDatas(EventSignUp eventSignUp) {
        if (eventSignUp.getmNeeded() > 0)
            currentCount.setText(String.valueOf(eventSignUp.getmNeeded()));// Quantity - Total, Needed - Remaining, Collected - What we got
        else {
            currentCount.setText("0");
        }
        ofCount.setText(String.valueOf(eventSignUp.getItemQuantity()));
        signUpDetails.setText(eventSignUp.getSlotTitle() + (eventSignUp.getSlotDescription() == null ? "" : ", " + eventSignUp.getSlotDescription()));
    }

    private void prefillDatas(SignUpContributor signUpContributor) {

        int need = Integer.parseInt(signUpContributor.getNeeded());
        if (need > 0)
            currentCount.setText(signUpContributor.getNeeded());// Quantity - Total, Needed - Remaining, Collected - What we got
        else {
            currentCount.setText("0");
        }

        ofCount.setText(String.valueOf(signUpContributor.getItemQuantity()));
        signUpDetails.setText(signUpContributor.getSlotTitle() + (signUpContributor.getSlotDescription() == null ? "" : ", " + signUpContributor.getSlotDescription()));
        signUpCount.setText(String.valueOf(signUpContributor.getQuantityCollected()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = Utilities.getListener(this, OnSignupUpdatedListener.class);
        progressListener = Utilities.getListener(this, ProgressListener.class);
        if (mListener == null)
            throw new RuntimeException(context.toString() + " must implement OnSignupUpdatedListener");
        if (progressListener == null)
            throw new RuntimeException(context.toString() + " must implement ProgressListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick({R.id.signUpDetails, R.id.currentCount, R.id.ofCount, R.id.done})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.done:
                if (signUpCount.getText().toString().length() > 0) {
                    if (isUpdateMode) {
                        if (validate())
                            updateSignUpDetails();
                        else
                            Toast.makeText(getContext(), "Item quanitiy is greater", Toast.LENGTH_SHORT).show();
                    } else {
                        if (validate())
                            addSignUpDetails();
                        else
                            Toast.makeText(getContext(), "Item quanitiy is greater", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(getContext(), "Please enter count", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public boolean validate() {
        int count = Integer.parseInt(signUpCount.getText().toString());
        if (isUpdateMode) {
            return signUpContributor.getQuantityCollected().intValue() + Integer.parseInt(signUpContributor.getNeeded()) >= count;
        } else {
            return count <= eventSignUp.getItemQuantity();
        }
    }

    private void addSignUpDetails() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("contribute_items_id", String.valueOf(eventSignUp.getId()));
        jsonObject.addProperty("event_id", String.valueOf(eventSignUp.getEventId()));
        jsonObject.addProperty("contribute_user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("contribute_item_quantity", signUpCount.getText().toString());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.addSignUpDetails(jsonObject, null, this);
    }

    private void updateSignUpDetails() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", String.valueOf(signUpContributor.getContrId()));
        jsonObject.addProperty("event_id", String.valueOf(signUpContributor.getEventId()));
        jsonObject.addProperty("contribute_user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("contribute_item_quantity", signUpCount.getText().toString());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        apiServiceProvider.updateSignUpDetails(jsonObject, null, this);
    }

    private void applyStyle() {
        float radius = getResources().getDimension(R.dimen.card_top_radius);
        signUpContainer.setShapeAppearanceModel(
                signUpContainer.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                        .setTopRightCorner(CornerFamily.ROUNDED, radius)
                        .setBottomRightCorner(CornerFamily.ROUNDED, 0)
                        .setBottomLeftCornerSize(0)
                        .build());
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        switch (apiFlag) {
            case ADD_EVENTS_SIGNUP:
                mListener.onSignUpAdded(eventSignUp);
                dismiss();
                break;
            case UPDATE_EVENTS_SIGNUP:
                mListener.onSignUpUpdated(signUpContributor);
                dismiss();
                break;
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        Toast.makeText(getContext(), "Please try again", Toast.LENGTH_SHORT).show();
    }

    public interface OnSignupUpdatedListener {
        void onSignUpAdded(EventSignUp eventSignUp);

        void onSignUpUpdated(SignUpContributor signUpContributor);
    }
}

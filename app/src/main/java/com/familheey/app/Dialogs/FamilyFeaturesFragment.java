package com.familheey.app.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.familheey.app.Models.Response.Family;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;


public class FamilyFeaturesFragment extends DialogFragment {

    @BindView(R.id.blockedMembers)
    TextView blockedMembers;
    @BindView(R.id.pendingRequests)
    TextView pendingRequests;
    @BindView(R.id.close)
    TextView close;

    private OnFamilyFeaturesListener mListener;
    private Family family;

    public FamilyFeaturesFragment() {
        // Required empty public constructor
    }

    public static FamilyFeaturesFragment newInstance(Family family) {
        FamilyFeaturesFragment fragment = new FamilyFeaturesFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, family);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert);
        family = getArguments().getParcelable(DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_family_members_more, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = Utilities.getListener(this, OnFamilyFeaturesListener.class);
        if (mListener == null) {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick({R.id.blockedMembers, R.id.close, R.id.pendingRequests})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.blockedMembers:
                mListener.onBlockedMembersRequested(family);
                break;
            case R.id.close:
                break;
            case R.id.pendingRequests:
                mListener.onPendingRequestRequested(family);
                break;
        }
        dismiss();
    }

    public interface OnFamilyFeaturesListener {
        void onBlockedMembersRequested(Family family);

        void onPendingRequestRequested(Family family);
    }
}

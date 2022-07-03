package com.familheey.app.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.FamilyLinkingAdapter;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectFamiliesDialog extends BottomSheetDialogFragment {

    @BindView(R.id.familyList)
    RecyclerView familyList;
    @BindView(R.id.done)
    Button done;
    private ArrayList<Family> families;
    private FamilyLinkingAdapter familyLinkingAdapter;

    public SelectFamiliesDialog() {
        // Required empty public constructor
    }

    public static SelectFamiliesDialog newInstance(Fragment parentFragment, List<Family> families) {
        SelectFamiliesDialog fragment = new SelectFamiliesDialog();
        fragment.setTargetFragment(parentFragment, Constants.RequestCode.REQUEST_CODE);
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.Bundle.DATA, (ArrayList<? extends Parcelable>) families);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            families = getArguments().getParcelableArrayList(Constants.Bundle.DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_families_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeFamilyList();
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
    }

    private void initializeFamilyList() {
        familyLinkingAdapter = new FamilyLinkingAdapter(families);
        familyList.setLayoutManager(new LinearLayoutManager(getContext()));
        familyList.setAdapter(familyLinkingAdapter);
    }

    @OnClick(R.id.done)
    public void onViewClicked() {
        Bundle bundle = new Bundle();
        Intent mIntent = new Intent();
        bundle.putParcelableArrayList(Constants.Bundle.DATA, familyLinkingAdapter.getLinkedFamilies());
        mIntent.putExtras(bundle);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, mIntent);
        dismiss();
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
        try {
            Field behaviorField = bottomSheetDialog.getClass().getDeclaredField("behavior");
            behaviorField.setAccessible(true);
            final BottomSheetBehavior behavior = (BottomSheetBehavior) behaviorField.get(bottomSheetDialog);
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        } catch (NoSuchFieldException | NullPointerException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

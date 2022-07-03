package com.familheey.app.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.CornerFamily;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class GroupTypeDialog extends BottomSheetDialogFragment {
    public static final int REQUEST_CODE = 156;
    RadioGroup groupType;
    @BindView(R.id.groupTypeContainer)
    MaterialCardView groupTypeContainer;

    private ArrayList<String> groupTypes;

    public GroupTypeDialog() {
        //setStyle(STYLE_NORMAL, R.style.BaseBottomSheetDialog);
    }

    public static GroupTypeDialog newInstance(Fragment parentFragment, ArrayList<String> groupTypes) {
        GroupTypeDialog fragment = new GroupTypeDialog();
        fragment.setTargetFragment(parentFragment, REQUEST_CODE);
        Bundle args = new Bundle();
        args.putStringArrayList(Constants.Bundle.DATA, groupTypes);
        fragment.setArguments(args);
        return fragment;
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupTypes = getArguments().getStringArrayList(Constants.Bundle.DATA);
        }
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_type_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Nullable
    @Override
    public Dialog getDialog() {
        Dialog dialog = super.getDialog();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        applyStyle();
    }

    private void applyStyle() {
        float radius = getResources().getDimension(R.dimen.card_top_radius);
        groupTypeContainer.setShapeAppearanceModel(
                groupTypeContainer.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                        .setTopRightCorner(CornerFamily.ROUNDED, radius)
                        .setBottomRightCorner(CornerFamily.ROUNDED, 0)
                        .setBottomLeftCornerSize(0)
                        .build());
    }

    @OnCheckedChanged({R.id.regular, R.id.company, R.id.individualFirm, R.id.nonProfitOrganization, R.id.institute, R.id.relegious, R.id.community, R.id.assosiation, R.id.team, R.id.project, R.id.branchOrDivision, R.id.others})
    void onFamilyTypeSelected(CompoundButton radioButton, boolean isChecked) {
        if (!isChecked)
            return;
        switch (radioButton.getId()) {
            default:
                Bundle bundle = new Bundle();
                Intent mIntent = new Intent();
                bundle.putString(Constants.Bundle.DATA, radioButton.getText().toString());
                mIntent.putExtras(bundle);
                getTargetFragment().onActivityResult(REQUEST_CODE, Activity.RESULT_OK, mIntent);
                dismiss();
                break;
        }
    }
}

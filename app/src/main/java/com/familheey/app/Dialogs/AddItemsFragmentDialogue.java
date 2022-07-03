package com.familheey.app.Dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.familheey.app.Activities.CreateEventActivity;
import com.familheey.app.Post.CreatePostActivity;
import com.familheey.app.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddItemsFragmentDialogue extends BottomSheetDialogFragment {


    @BindView(R.id.createEvent)
    TextView createEvent;
    @BindView(R.id.makeAnnouncement)
    TextView makeAnnouncement;
    @BindView(R.id.createPost)
    TextView createPost;
    @BindView(R.id.optionsContainerCard)
    CardView optionsContainerCard;
    @BindView(R.id.close)
    TextView close;

    public static AddItemsFragmentDialogue newInstance() {
        AddItemsFragmentDialogue fragment = new AddItemsFragmentDialogue();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item_details, container, false);
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


    @OnClick({R.id.createEvent, R.id.makeAnnouncement, R.id.createPost, R.id.optionsContainerCard, R.id.close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.createEvent:
                startActivity(new Intent(getContext(), CreateEventActivity.class));
                break;
            case R.id.makeAnnouncement:
                break;
            case R.id.createPost:
                Intent intent = new Intent(getContext(), CreatePostActivity.class);
                getContext().startActivity(intent);
                break;
            case R.id.optionsContainerCard:
                break;
            case R.id.close:
                break;
        }
    }
}

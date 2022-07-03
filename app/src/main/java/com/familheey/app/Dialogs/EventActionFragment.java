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

import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EventActionFragment extends DialogFragment {

    @BindView(R.id.close)
    TextView close;
    @BindView(R.id.editEvent)
    TextView editEvent;
    @BindView(R.id.cancelEvent)
    TextView cancelEvent;
    @BindView(R.id.deleteEvent)
    TextView deleteEvent;

    private OnEventActionListener mListener;

    public EventActionFragment() {
        // Required empty public constructor
    }

    public static EventActionFragment newInstance() {
        EventActionFragment fragment = new EventActionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_action, container, false);
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
        mListener = Utilities.getListener(this, OnEventActionListener.class);
        if (mListener == null) {
            throw new RuntimeException(context.toString() + " must implement OnEventActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick({R.id.editEvent, R.id.close, R.id.cancelEvent, R.id.deleteEvent})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editEvent:
                mListener.onEventEditRequested();
                dismiss();
                break;
            case R.id.close:
                dismiss();
                break;
            case R.id.cancelEvent:
                mListener.onEventCancelRequested();
                dismiss();
                break;
            case R.id.deleteEvent:
                mListener.onEventDeleteRequested();
                dismiss();
                break;
        }
    }

    public interface OnEventActionListener {
        void onEventEditRequested();

        void onEventCancelRequested();

        void onEventDeleteRequested();
    }
}
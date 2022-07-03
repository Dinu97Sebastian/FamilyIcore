package com.familheey.app.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.familheey.app.Models.Response.Document;
import com.familheey.app.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.POSITION;

public class AlbumActionDialogFragment extends DialogFragment {

    private OnEventActionSelectedListener mListener;
    private int position;
    private Document document;

    public AlbumActionDialogFragment() {
        // Required empty public constructor
    }

    public static AlbumActionDialogFragment newInstance(Document document, int position) {
        AlbumActionDialogFragment fragment = new AlbumActionDialogFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putParcelable(DATA, document);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert);
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION);
            document = getArguments().getParcelable(DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album_action_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEventActionSelectedListener) {
            mListener = (OnEventActionSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEventActionSelectedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        return dialog;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick({R.id.blockedMembers, R.id.deleteImage, R.id.close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.blockedMembers:
                mListener.onCoverImageChanged(position);
                break;
            case R.id.deleteImage:
                mListener.onAlbumImageDeleted(position);
                break;
            case R.id.close:
                break;
        }
        dismiss();
    }

    public interface OnEventActionSelectedListener {
        void onCoverImageChanged(int position);

        void onAlbumImageDeleted(int position);
    }
}

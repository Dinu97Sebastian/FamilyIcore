package com.familheey.app.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.familheey.app.Interfaces.RegistrationInteractor;
import com.familheey.app.Models.Response.UserRegistrationResponse;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailsLevelThreeFragment extends Fragment {

    private Uri profilePictureUri;

    @BindView(R.id.profileImage)
    CircleImageView userProfile;
    @BindView(R.id.skip)
    TextView skip;
    @BindView(R.id.next)
    Button next;
    @BindView(R.id.back)
    Button back;
    @BindView(R.id.headerText)
    TextView headerText;

    private UserRegistrationResponse userRegistration;

    private RegistrationInteractor mListener;

    public UserDetailsLevelThreeFragment() {
        // Required empty public constructor
    }

    public static UserDetailsLevelThreeFragment newInstance(UserRegistrationResponse userRegistration) {
        UserDetailsLevelThreeFragment fragment = new UserDetailsLevelThreeFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, userRegistration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userRegistration = getArguments().getParcelable(Constants.Bundle.DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_details_level_three, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        headerText.setText("Hi, " + userRegistration.getFullName());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegistrationInteractor) {
            mListener = (RegistrationInteractor) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RegistrationInteractor");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick({R.id.profileImage, R.id.editProfileImage, R.id.skip, R.id.next, R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.profileImage:
            case R.id.editProfileImage:
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(getContext(), this);
                mListener.captureUserProfilePicture();
                break;
            case R.id.skip:
                userRegistration.setPropic("");
                userRegistration.setActive(true);
                mListener.registerUser(userRegistration);
                break;
            case R.id.next:
                if (profilePictureUri != null) {
                    userRegistration.setPropic(profilePictureUri.toString());
                } else {
                    userRegistration.setPropic("");
                }
                userRegistration.setActive(true);
                mListener.registerUser(userRegistration);
                break;
            case R.id.back:
                mListener.revertOneLevel();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                profilePictureUri = result.getUri();
                Glide.with(getContext())
                        .load(profilePictureUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(userProfile);
                userRegistration.setPropic(profilePictureUri.toString());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

package com.familheey.app.Fragments.ProfileDashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.familheey.app.Activities.TextEditActivity;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Models.Response.ProfileResponse.UserProfile;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.ID;

public class AboutMeFragment extends Fragment {
    public static final int EDIT_INTRO_REQUEST_CODE = 101;
    public static final int EDIT_WORK_REQUEST_CODE = 102;
    @BindView(R.id.editIntroduction)
    ImageView editIntroduction;
    @BindView(R.id.introduction)
    TextView introduction;
    @BindView(R.id.editWork)
    ImageView editWork;
    @BindView(R.id.work)
    TextView work;
    /*@BindView(R.id.introductionGroup)
    Group introductionGroup;
    @BindView(R.id.workGroup)
    Group workGroup;*/
    private FamilyMember familyMember;
    private UserProfile userProfile;
    HashTagHelper hashTagHelper;
    public static AboutMeFragment newInstance(FamilyMember familyMember) {
        AboutMeFragment fragment = new AboutMeFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, familyMember);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            familyMember = getArguments().getParcelable(Constants.Bundle.DATA);
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_me, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void addRestrictions() {
        if (SharedPref.getUserRegistration().getId().equalsIgnoreCase(String.valueOf(userProfile.getProfile().getId()))) {
            editIntroduction.setVisibility(View.VISIBLE);
            editWork.setVisibility(View.VISIBLE);
        } else {
            editIntroduction.setVisibility(View.INVISIBLE);
            editWork.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void fillDetails(UserProfile userProfile) {
        this.userProfile = userProfile;
        if (userProfile.getProfile().getAbout() != null&&!userProfile.getProfile().getAbout().trim().equals("null")) {
            String summary=userProfile.getProfile().getAbout().toString();
            summary = summary.replaceAll("\\<.*?\\>", "");
            introduction.setText( summary);
            if(introduction!=null){
                hashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(requireActivity(),R.color.buttoncolor), null);
                hashTagHelper.handle(introduction);
                Linkify.addLinks(introduction, Linkify.ALL); // linkify all links in text.
                introduction.setLinkTextColor(ContextCompat.getColor(requireActivity(),R.color.buttoncolor));
            }
        }
        else {
            introduction.setText( "" );
        }
        if (userProfile.getProfile().getWork() != null)
        {
            String profile=userProfile.getProfile().getWork().toString();
            profile = profile.replaceAll("\\<.*?\\>", "");
            work.setText(profile);

            if(work!=null){
                hashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(requireActivity(),R.color.buttoncolor), null);
                hashTagHelper.handle(work);
                Linkify.addLinks(work, Linkify.ALL); // linkify all links in text.
                work.setLinkTextColor(ContextCompat.getColor(requireActivity(),R.color.buttoncolor));
            }
        }
        else work.setText("");
        addRestrictions();
    }

    @OnClick({R.id.editIntroduction, R.id.editWork})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.editIntroduction:
                if (userProfile.getProfile().getAbout() != null)
                    familyMember.setDeveloperMessage(userProfile.getProfile().getAbout());
                startActivityForResult(new Intent(getActivity(), TextEditActivity.class).putExtra(ID,"").putExtra("type","about").putExtra("tittle","Update Introduction").putExtra(DATA, introduction.getText()+""), EDIT_INTRO_REQUEST_CODE);


                //ProfileEditDialogFragment.newInstance(ProfileEditDialogFragment.TYPE_INTRODUCTION, userProfile).show(getChildFragmentManager(), ProfileEditDialogFragment.TAG);
                break;
            case R.id.editWork:
                if (userProfile.getProfile().getWork() != null)
                    familyMember.setDeveloperMessage(userProfile.getProfile().getWork());
                startActivityForResult(new Intent(getActivity(), TextEditActivity.class).putExtra(ID,"").putExtra("type","work").putExtra("tittle","Update Work").putExtra(DATA, work.getText()+""), EDIT_WORK_REQUEST_CODE);

              //  ProfileEditDialogFragment.newInstance(ProfileEditDialogFragment.TYPE_WORK, userProfile).show(getChildFragmentManager(), ProfileEditDialogFragment.TAG);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == EDIT_INTRO_REQUEST_CODE) {
            introduction.setText(data.getExtras().getString(DATA));
            /**Links in summary**/
            if(introduction!=null){
                hashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(requireActivity(),R.color.buttoncolor), null);
                hashTagHelper.handle(introduction);
                Linkify.addLinks(introduction, Linkify.ALL); // linkify all links in text.
                introduction.setLinkTextColor(ContextCompat.getColor(requireActivity(),R.color.buttoncolor));
            }
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == EDIT_WORK_REQUEST_CODE) {
            work.setText(data.getExtras().getString(DATA));
            /**Links in profile**/
            if(work!=null){
                hashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(requireActivity(),R.color.buttoncolor), null);
                hashTagHelper.handle(work);
                Linkify.addLinks(work, Linkify.ALL); // linkify all links in text.
                work.setLinkTextColor(ContextCompat.getColor(requireActivity(),R.color.buttoncolor));
            }
        }

    }

}

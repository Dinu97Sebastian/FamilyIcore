package com.familheey.app.Discover;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Adapters.EventTabAdapter;
import com.familheey.app.Discover.ui.main.DiscoverEventFragment;
import com.familheey.app.Discover.ui.main.DiscoverFamilyFragment;
import com.familheey.app.Discover.ui.main.DiscoverMemberFragment;
import com.familheey.app.Interfaces.HomeInteractor;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.tabs.TabLayout;
import com.nex3z.notificationbadge.NotificationBadge;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static com.familheey.app.Utilities.Constants.Bundle.POSITION;
import static com.familheey.app.Utilities.Utilities.hideCircularReveal;
import static com.familheey.app.Utilities.Utilities.showCircularReveal;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverSearchFragment extends Fragment implements TabLayout.OnTabSelectedListener  {

    @BindView(R.id.toolBarTitle)
    TextView title;
    @BindView(R.id.profileImage)
    ImageView profileImage;
    @BindView(R.id.bellNotificationCount)
    NotificationBadge bellNotificationCount;
    @BindView(R.id.feedback)
    ImageView feedback;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.imageBack)
    ImageView imageBack;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;
    @BindView(R.id.search_post)
    EditText search_post;
    @BindView(R.id.constraintSearch)
    ConstraintLayout constraintSearch;


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EventTabAdapter eventTabAdapter;
    private HomeInteractor mListener;

    public DiscoverSearchFragment() {
        // Required empty public constructor
    }

    public static DiscoverSearchFragment newInstance(int pos) {
        DiscoverSearchFragment fragment = new DiscoverSearchFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover_search, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated( savedInstanceState );
        initializeToolbar();
        initializeSearchClearCallback();
        initListener();
        tabLayout = getActivity().findViewById(R.id.tabLayout);
        viewPager = getActivity().findViewById(R.id.eventPager);
        viewPager.setOffscreenPageLimit(0);
        eventTabAdapter = new EventTabAdapter(requireActivity().getSupportFragmentManager());
        //search_post, constraintSearch
        DiscoverFamilyFragment discoverFamilyFragment = new DiscoverFamilyFragment();
        eventTabAdapter.addFragment(discoverFamilyFragment.newInstance(0), "FAMILY");
        DiscoverMemberFragment discoverMemberFragment=new DiscoverMemberFragment();
        eventTabAdapter.addFragment(discoverMemberFragment.newInstance( 1 ),"PEOPLE");
        DiscoverEventFragment discoverEventFragment=new DiscoverEventFragment();
        eventTabAdapter.addFragment(discoverEventFragment.newInstance( 2 ),"EVENTS");
        viewPager.setAdapter(eventTabAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);


    }
    private void initializeSearchClearCallback() {
        search_post.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    clearSearch.setVisibility(View.INVISIBLE);
                else clearSearch.setVisibility(View.VISIBLE);
            }
        });
        clearSearch.setOnClickListener(v -> {
            search_post.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }
    private void initializeToolbar() {
         title.setText("Discover");
        profileImage.setOnClickListener(v -> {
            FamilyMember familyMember = new FamilyMember();
            familyMember.setId(Integer.parseInt( SharedPref.getUserRegistration().getId()));
            familyMember.setUserId(familyMember.getId());
            familyMember.setProPic(SharedPref.getUserRegistration().getPropic());
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            intent.putExtra( Constants.Bundle.DATA, familyMember);
            intent.putExtra(Constants.Bundle.FOR_EDITING, true);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), profileImage, "profile");
            startActivity(intent, options.toBundle());
        });
        setNotificationCount(mListener.getNotificationsCount());
        Utilities.loadProfilePicture(profileImage);
    }
    public void setNotificationCount(int count) {
        if (count > 99) {
            bellNotificationCount.setText("99+", true);
        }
        else if (count >0){
            bellNotificationCount.setText(String.valueOf(count), true);
        }
        else
            bellNotificationCount.clear(true);
    }

    @OnClick({R.id.imgMessage, R.id.bellIcon, R.id.bellNotificationCount, R.id.feedback})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgMessage:
                mListener.navigateMessageScreen();
                break;
            case R.id.bellIcon:
            case R.id.bellNotificationCount:
                mListener.loadNotifications();
                break;
            case R.id.feedback:
                Utilities.showMainMenuPopup(getContext(), feedback);
                break;
        }
    }


    private void initListener() {
        imgSearch.setOnClickListener(view -> {
            showCircularReveal(constraintSearch);
            profileImage.setEnabled(false);
            showKeyboard();

        });

        imageBack.setOnClickListener(view -> {
            hideCircularReveal(constraintSearch);
            search_post.setText("");
            profileImage.setEnabled(true);
            onSearchQueryListener( EditorInfo.IME_ACTION_SEARCH);

        });
    }

    @OnEditorAction(R.id.search_post)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            DiscoverFamilyFragment fragment1 = (DiscoverFamilyFragment) eventTabAdapter.getRegisteredFragment(0);
            if (fragment1 != null) {
                fragment1.onSearch(search_post.getText().toString());
            }
            DiscoverMemberFragment fragment2 = (DiscoverMemberFragment) eventTabAdapter.getRegisteredFragment(1);
            if (fragment2 != null) {
                fragment2.onSearch(search_post.getText().toString());
            }

            DiscoverEventFragment fragment3 = (DiscoverEventFragment) eventTabAdapter.getRegisteredFragment(2);
            if (fragment3 != null) {
                fragment3.onSearch(search_post.getText().toString());
            }
            try {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_post.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    private void showKeyboard() {
        if (search_post.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(search_post, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeInteractor) {
            mListener = (HomeInteractor) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement HomeInteractor");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
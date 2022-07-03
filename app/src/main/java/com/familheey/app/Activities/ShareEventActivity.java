package com.familheey.app.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.familheey.app.Adapters.ShareViewPagerAdapter;
import com.familheey.app.Fragments.FragmentFamilyShare;
import com.familheey.app.Fragments.FragmentOthersShare;
import com.familheey.app.Fragments.FragmentPeopleShare;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;

import static com.familheey.app.Utilities.Constants.Bundle.IS_INVITATION;

public class ShareEventActivity extends AppCompatActivity {
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.search_share)
    EditText search_share;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;

    private String type = "";
    private String eventId = "";
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ShareViewPagerAdapter adapter;
    private boolean isInvitation = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_event);
        ButterKnife.bind(this);

        type = getIntent().getStringExtra(Constants.Bundle.TYPE);

        assert type != null;
        if (type.equals("EVENT")) {
            eventId = getIntent().getStringExtra(Constants.Bundle.DATA);
            isInvitation = getIntent().getBooleanExtra(IS_INVITATION, false);
        } else {
            eventId = getIntent().getStringExtra("Post_id");
        }

        tabLayout = findViewById(R.id.share_tabLayout);
        viewPager = findViewById(R.id.share_eventPager);

        initializeToolbar();
        initializetabs();
        initializeSearchClearCallback();
    }

    private void initializeSearchClearCallback() {
        search_share.addTextChangedListener(new TextWatcher() {

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
            search_share.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }

    private void initializetabs() {
        adapter = new ShareViewPagerAdapter(getSupportFragmentManager());
        if (type.equals("EVENT")) {

            adapter.AddFragments(FragmentFamilyShare.newInstance(eventId, isInvitation, type), "FAMILY");
            adapter.AddFragments(FragmentPeopleShare.newInstance(eventId, isInvitation, type), "PEOPLE");
            adapter.AddFragments(FragmentOthersShare.newInstance(eventId, isInvitation), "OTHERS");
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(3);
        } else if (type.equals("ANNOUNCEMENT")) {
            toolBarTitle.setText("Share Announcement");
            tabLayout.setVisibility(View.GONE);
            adapter.AddFragments(FragmentFamilyShare.newInstance(eventId, isInvitation, type), "FAMILY");
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(1);
        } else {
            toolBarTitle.setText("Share Post");
            adapter.AddFragments(FragmentFamilyShare.newInstance(eventId, isInvitation, type), "FAMILY");
            adapter.AddFragments(FragmentPeopleShare.newInstance(eventId, isInvitation, type), "PEOPLE");
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(2);
        }
    }

    private void initializeToolbar() {
        if (isInvitation)
            toolBarTitle.setText("Invite Event");
        else
            toolBarTitle.setText("Share Event");
        goBack.setOnClickListener(v -> onBackPressed());
    }

    @OnEditorAction(R.id.search_share)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            FragmentFamilyShare fragmentFamilyShare = (FragmentFamilyShare) adapter.getRegisteredFragment(0);
            if (fragmentFamilyShare != null)
                fragmentFamilyShare.updateQuery(search_share.getText().toString().trim());
            FragmentPeopleShare fragmentPeopleShare = (FragmentPeopleShare) adapter.getRegisteredFragment(1);
            if (fragmentPeopleShare != null)
                fragmentPeopleShare.updateQuery(search_share.getText().toString().trim());
            /**@author Devika on 01-11-2021
             * to hide keyboard when clicking search key in soft keyboard
             * **/
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_share.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}

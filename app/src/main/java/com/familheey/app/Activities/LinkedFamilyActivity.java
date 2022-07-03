package com.familheey.app.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.familheey.app.Adapters.LinkedFamilypagerAdapter;
import com.familheey.app.Fragments.FamilyViewMembers.FamilyRequestsFragment;
import com.familheey.app.Fragments.LinkedFamilyFragment;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.R;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class LinkedFamilyActivity extends AppCompatActivity {

    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    private Family family;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linked_family);
        ButterKnife.bind(this);
        family = getIntent().getParcelableExtra(DATA);
        initializeToolbar();
        initializeAdapter();
    }

    private void initializeAdapter() {
        LinkedFamilypagerAdapter linkedFamilypagerAdapter = new LinkedFamilypagerAdapter(getSupportFragmentManager());
        linkedFamilypagerAdapter.addFragment(LinkedFamilyFragment.newInstance(family), "Families");
        if (family.isAdmin()) {
            linkedFamilypagerAdapter.addFragment(FamilyRequestsFragment.newInstance(family, FamilyRequestsFragment.LINK_REQUEST), "Request");
        } else {
            tabLayout.setVisibility(View.GONE);
        }
        viewPager.setAdapter(linkedFamilypagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        if (family.isAdmin()) {
            FamilyRequestsFragment familyRequestsFragment = (FamilyRequestsFragment) linkedFamilypagerAdapter.getRegisteredFragment(1);
            if (familyRequestsFragment != null)
                familyRequestsFragment.updateFamily(family);
        }
    }

    private void initializeToolbar() {
        toolBarTitle.setText("Linked Families");
        goBack.setOnClickListener(v -> finish());
    }
}
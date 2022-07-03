package com.familheey.app.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.familheey.app.Fragments.DiscoverFamilyFragment;
import com.familheey.app.Fragments.MyFamilyFragment;

public class FamilyPagerAdapter extends FragmentStatePagerAdapter {

    int tabsCount;

    public FamilyPagerAdapter(FragmentManager fm, int tabsCount) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.tabsCount = tabsCount;
    }

    @Override
    public int getCount() {
        return tabsCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MyFamilyFragment.newInstance();
            case 1:
                return DiscoverFamilyFragment.newInstance( 1 );
            default:
                return null;
        }
    }
}

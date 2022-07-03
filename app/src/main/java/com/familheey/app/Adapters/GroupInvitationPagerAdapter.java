package com.familheey.app.Adapters;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.familheey.app.Fragments.Events.FamilyGuestListingFragment;
import com.familheey.app.Fragments.Events.OtherGuestListingFragment;
import com.familheey.app.Fragments.Events.UserGuestListingFragment;

public class GroupInvitationPagerAdapter extends FragmentStatePagerAdapter {

    int tabsCount;
    Fragment fragment;
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public GroupInvitationPagerAdapter(@NonNull FragmentManager fm, int tabsCount) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.tabsCount = tabsCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FamilyGuestListingFragment.newInstance();
            case 1:
                return UserGuestListingFragment.newInstance();
            case 2:
                return OtherGuestListingFragment.newInstance();
            default:
                return null;
        }

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }


    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public int getCount() {
        return tabsCount;
    }


}

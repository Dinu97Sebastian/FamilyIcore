package com.familheey.app.Adapters;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.familheey.app.Fragments.FamilyDashboard.BlockedUsersFragment;
import com.familheey.app.Fragments.FamilyDashboard.PendingRequestFragment;
import com.familheey.app.Fragments.FamilyViewMembers.FamilyMembersFragment;
import com.familheey.app.Fragments.FamilyViewMembers.FamilyRequestsFragment;
import com.familheey.app.Fragments.FamilyViewMembers.FamilySubscriptionUpdatedFragment;
import com.familheey.app.Fragments.GroupInviteFragment;
import com.familheey.app.Models.Response.Family;

public class FamilySubscriptionAdapter extends FragmentStatePagerAdapter {
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private int tabsCount;
    private Family family;

    public FamilySubscriptionAdapter(FragmentManager fm, int tabsCount, Family family) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.tabsCount = tabsCount;
        this.family = family;
    }

    @Override
    public int getCount() {
        return tabsCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FamilyMembersFragment.newInstance(family);
            case 1:
                return FamilyRequestsFragment.newInstance(family, FamilyRequestsFragment.MEMBER_REQUEST);
            case 2:
                return GroupInviteFragment.newInstance(family);
            case 3:
                return BlockedUsersFragment.newInstance(family);
            case 4:
                return PendingRequestFragment.newInstance(family.getId().toString());
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
}
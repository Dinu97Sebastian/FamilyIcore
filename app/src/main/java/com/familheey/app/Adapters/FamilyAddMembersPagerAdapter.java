package com.familheey.app.Adapters;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.familheey.app.Fragments.GroupInviteFragment;
import com.familheey.app.Fragments.MemberAddToGroupFragment;
import com.familheey.app.Models.Response.Family;

import org.jetbrains.annotations.NotNull;

public class FamilyAddMembersPagerAdapter extends FragmentStatePagerAdapter {

    private int tabsCount;
    private Family family;
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public FamilyAddMembersPagerAdapter(Family family, FragmentManager fm, int tabsCount) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.tabsCount = tabsCount;
        this.family = family;
    }

    @Override
    public int getCount() {
        return tabsCount;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MemberAddToGroupFragment.newInstance(family.getId() + "");
            case 1:
                return GroupInviteFragment.newInstance(family);
            default:
                return null;
        }
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
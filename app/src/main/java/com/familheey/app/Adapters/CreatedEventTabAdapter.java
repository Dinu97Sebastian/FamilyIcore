package com.familheey.app.Adapters;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.familheey.app.Fragments.Events.SignupFragment;

import java.util.ArrayList;
import java.util.List;

public class CreatedEventTabAdapter extends FragmentStatePagerAdapter {

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public CreatedEventTabAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public boolean addSignUpEvent(Fragment fragment, String title) {
        boolean hasSignUpEvent = false;
        int position = 0;
        for (int i = 0; i < mFragmentList.size(); i++) {
            if (mFragmentList.get(i) instanceof SignupFragment) {
                hasSignUpEvent = true;
                position = i;
                break;
            }
        }
        if (hasSignUpEvent)
            return false;
        mFragmentList.add(position, fragment);
        mFragmentTitleList.add(position, title);
        notifyDataSetChanged();
        return true;
    }

    public boolean removeSignUpEvent() {
        boolean hasSignUpEvent = false;
        int position = 0;
        for (int i = 0; i < mFragmentList.size(); i++) {
            if (mFragmentList.get(i) instanceof SignupFragment) {
                hasSignUpEvent = true;
                position = i;
                break;
            }
        }
        if (!hasSignUpEvent)
            return false;
        mFragmentList.remove(position);
        mFragmentTitleList.remove(position);
        notifyDataSetChanged();
        return true;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
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

    public <T> T getInstance(@NonNull Class<T> neededFragment) {
        for (int i = 0; i < registeredFragments.size(); i++) {
            if (neededFragment.isInstance(registeredFragments.get(i))) {
                return (T) registeredFragments.get(i);
            }
        }
        return null;
    }
}

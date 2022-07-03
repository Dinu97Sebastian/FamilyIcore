package com.familheey.app.Adapters;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShareViewPagerAdapter extends FragmentPagerAdapter {

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private final List<Fragment> lstfragment = new ArrayList<>();
    private final List<String> lsttitles = new ArrayList<>();

    public ShareViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return lstfragment.get(position);
    }

    @Override
    public int getCount() {
        return lsttitles.size();
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return lsttitles.get(position);
    }


    public void AddFragments(Fragment fragment, String title) {
        lstfragment.add(fragment);
        lsttitles.add(title);
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

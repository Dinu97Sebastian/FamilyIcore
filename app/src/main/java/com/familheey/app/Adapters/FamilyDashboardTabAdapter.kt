package com.familheey.app.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.familheey.app.Fragments.FamilyDashboard.AboutUsFragment


class FamilyDashboardTabAdapter(manager: FragmentManager) :
        FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val registeredFragments: MutableList<Fragment> = ArrayList()
    private val titleList: MutableList<String> = ArrayList()
    override fun getItem(position: Int): Fragment {
        return registeredFragments[position]
    }

    override fun getCount(): Int {
        return registeredFragments.size
    }

    fun addFragment(title: String, fragment: Fragment) {
        registeredFragments.add(fragment)
        titleList.add(title)
    }


    fun replaceFragment(title: String, fragment: Fragment) {
        val manager = (fragment as Fragment).fragmentManager
        val trans = manager!!.beginTransaction()
        trans.remove((fragment as Fragment?)!!)
        trans.commit()
    }

    override fun getItemPosition(`object`: Any): Int {
        return super.getItemPosition(`object`)
        return PagerAdapter.POSITION_NONE;
    }



    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }

    fun <T> getInstance(neededFragment: Class<T>): T? {
        for (i in 0 until registeredFragments.size) {
            if (neededFragment.isInstance(registeredFragments[i])) {
                return registeredFragments[i] as T
            }
        }
        return null
    }

    fun getRegisteredFragment(position: Int): Fragment? {
        return registeredFragments[position]
    }
}
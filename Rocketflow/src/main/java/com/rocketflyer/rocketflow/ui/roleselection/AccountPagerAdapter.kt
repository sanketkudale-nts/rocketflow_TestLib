package com.rocketflyer.rocketflow.ui.roleselection

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.tracki.ui.tasklisting.ihaveassigned.TabDataClass

class AccountPagerAdapter(fragmentManager: FragmentManager?) : FragmentStatePagerAdapter(fragmentManager!!) {
    private var mTabCount = 0
    private var fragments: List<TabDataClass>? = null
    override fun getCount(): Int {
        return mTabCount
    }

    fun setFragments(fragments: List<TabDataClass>) {
        this.fragments = fragments
        mTabCount = fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments!![position].fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragments!![position].title
    }
}
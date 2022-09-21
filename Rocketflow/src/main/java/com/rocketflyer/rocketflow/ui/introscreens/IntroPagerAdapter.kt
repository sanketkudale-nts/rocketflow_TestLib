package com.rocketflyer.rocketflow.ui.introscreens


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by rahul on 22/2/19
 */
class IntroPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val fragmentList = ArrayList<Fragment>()
//    private val fragmentTitleList = ArrayList<String>()

    override fun getItem(p0: Int): Fragment {
        return fragmentList[p0]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

//    override fun getPageTitle(position: Int): CharSequence? {
//        return fragmentTitleList[position]
//    }

    fun addFragment(fragment: Fragment/*, title: String*/) {
        fragmentList.add(fragment)
//        fragmentTitleList.add(title)
    }
}
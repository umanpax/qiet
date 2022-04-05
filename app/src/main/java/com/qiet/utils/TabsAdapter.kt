package com.qiet.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.qiet.modules.fragments.mapalarm.MapAlarmFragment

class TabsAdapter(
    fm: FragmentManager?,
    var mNumOfTabs: Int,
    var fragment: Fragment,
    var fragmentTitle: String
) : FragmentPagerAdapter(fm!!,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    var mPos = 0


    override fun getItem(position: Int): Fragment {
        mPos = position
        if (mPos > 0) mPos -= 1
        else mPos = position
        return when (position) {
            0 -> fragment
            else -> fragment
        }
    }

    override fun getCount(): Int {
        return mNumOfTabs
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragmentTitle
    }

}

package com.stalmate.user.view.dashboard.funtime

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import fr.castorflex.android.verticalviewpager.VerticalViewPager


class ViewPagerStatAdapter(
    fm: FragmentManager,
     menuPager: VerticalViewPager,
     isFirstTime: Boolean,
      callBack: FragmentCallBack
) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()
    var callBack: FragmentCallBack
    fun refreshStateSet(isRefresh: Boolean) {
        if (isRefresh) {
            PAGE_REFRESH_STATE = POSITION_NONE
        } else {
            PAGE_REFRESH_STATE = POSITION_UNCHANGED
        }
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    fun removeFragment(position: Int) {
        mFragmentList.removeAt(position)
        mFragmentTitleList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        // refresh all fragments when data set changed
        return PAGE_REFRESH_STATE
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    companion object {
        private var PAGE_REFRESH_STATE = POSITION_UNCHANGED
    }

    init {
        this.callBack = callBack
     /*   if (isFirstTime) {
            if (Paper.book(Variables.PromoAds).contains(Variables.PromoAdsModel)) {
                val initItem: HomeModel =
                    Paper.book(Variables.PromoAds).read(Variables.PromoAdsModel)
                if (initItem != null) addFragment(
                    VideosListF(
                        true,
                        initItem,
                        menuPager,
                        callBack,
                        R.id.mainMenuFragment
                    ), ""
                )
            }
        }*/
    }

}


interface FragmentCallBack {
    fun onResponce(bundle: Bundle?)
}
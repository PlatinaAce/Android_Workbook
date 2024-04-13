// Week 3 Lecture 16

package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BannerVPAdapter(fragment: Fragment) :FragmentStateAdapter(fragment){

    private val fragmentList: ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int = fragmentList.size
//    this is same as
//    override fun getItemCount(): Int {
//        return fragmentList.size
//    }

    override fun createFragment(position: Int): Fragment = fragmentList[position]

    fun addFragment(fragment: Fragment){
        fragmentList.add(fragment)
        //Viewpager에 리스트에 새로운 값이 추가가 되었음을 알림
        notifyItemInserted(fragmentList.size - 1)
    }

}
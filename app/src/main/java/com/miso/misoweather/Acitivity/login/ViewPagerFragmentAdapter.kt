package com.miso.misoweather.Acitivity.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.miso.misoweather.R

class ViewPagerFragmentAdapter(fragmentActivity: FragmentActivity,val fragmentList:List<Fragment>): FragmentStateAdapter(fragmentActivity)
{
    override fun getItemCount(): Int
    {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}
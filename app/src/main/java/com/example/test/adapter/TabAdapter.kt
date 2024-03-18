package com.example.test.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.test.tabs.TabBorrowed
import com.example.test.tabs.TabRequests

class TabAdapter (fragmentManager: FragmentManager,
                  lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return 2;
    }

    override fun createFragment(position: Int): Fragment {
        return if (position==0)
            TabBorrowed()
        else
            TabRequests()
    }
}
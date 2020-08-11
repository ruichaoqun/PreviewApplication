package com.ruichaoqun.previewapplication.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ruichaoqun.previewapplication.ItemFragment
import com.ruichaoqun.previewapplication.R
import com.ruichaoqun.previewapplication.TabLayoutMediator
import com.ruichaoqun.previewapplication.VjiaTabLayout

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val tab: VjiaTabLayout = root.findViewById(R.id.tab_layout)
        val pager:ViewPager2 = root.findViewById(R.id.view_pager)
        var adapter: ScreenSlidePagerAdapter? = activity?.let { ScreenSlidePagerAdapter(it) }
        pager.adapter = adapter
        TabLayoutMediator(tab, pager) { tab, position ->
            tab.text = "OBJECT ${(position + 1)}"
        }.attach()
        return root
    }

    class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 20

        override fun createFragment(position: Int): Fragment = ItemFragment.newInstance(1)
    }
}
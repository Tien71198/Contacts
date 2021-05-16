package com.example.contact.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.contact.R
import com.example.contact.ui.contact.ContactsFragment
import com.example.contact.ui.diary.DiaryFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpTabs()
    }

    private fun setUpTabs() {
        val adapter = PageAdapter(supportFragmentManager)
        adapter.addFragment(ContactsFragment(), "DANH BA")
        adapter.addFragment(DiaryFragment(), "NHAT KY")
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 1
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_home_24)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_phone_callback_24)
    }
}
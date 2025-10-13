package com.example.whatsapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.whatsapp.databinding.ActivityMainBinding
import com.example.whatsapp.ui.calls.CallsFragment
import com.example.whatsapp.ui.chat.ChatsFragment
import com.example.whatsapp.ui.communities.CommunitiesFragment
import com.example.whatsapp.ui.status.StatusFragment
import com.example.whatsapp.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if user is authenticated
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setupViewPager()
        setupBottomNavigation()
    }

    private fun setupViewPager() {
        val adapter = MainPagerAdapter(this)
        binding.viewPager.adapter = adapter
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.example.whatsapp.R.id.nav_chats -> {
                    binding.viewPager.currentItem = 0
                    binding.toolbar.title = "WhatsApp"
                    true
                }
                com.example.whatsapp.R.id.nav_status -> {
                    binding.viewPager.currentItem = 1
                    binding.toolbar.title = "Status"
                    true
                }
                com.example.whatsapp.R.id.nav_calls -> {
                    binding.viewPager.currentItem = 2
                    binding.toolbar.title = "Calls"
                    true
                }
                com.example.whatsapp.R.id.nav_communities -> {
                    binding.viewPager.currentItem = 3
                    binding.toolbar.title = "Communities"
                    true
                }
                else -> false
            }
        }

        // Sync ViewPager with BottomNavigation
        binding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavigation.menu.getItem(position).isChecked = true
                when (position) {
                    0 -> binding.toolbar.title = "WhatsApp"
                    1 -> binding.toolbar.title = "Status"
                    2 -> binding.toolbar.title = "Calls"
                    3 -> binding.toolbar.title = "Communities"
                }
            }
        })
    }

    private class MainPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ChatsFragment()
                1 -> StatusFragment()
                2 -> CallsFragment()
                3 -> CommunitiesFragment()
                else -> ChatsFragment()
            }
        }
    }
}
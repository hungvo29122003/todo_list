package com.example.todo_list

import android.content.Intent
import android.icu.text.Transliterator.Position
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.todo_list.adapter.OnboardingPagerAdapter
import com.example.todo_list.databinding.ActivityOnboadingBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnboadingActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: OnboardingPagerAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var skipButton: TextView
    private lateinit var nextButton: Button
    private lateinit var backButton: TextView
    private lateinit var binding: ActivityOnboadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout
        skipButton = binding.skipButton
        nextButton = binding.nextButton
        backButton = binding.backButton

        val adapter = OnboardingPagerAdapter(this)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) {tab, position ->}.attach()
        
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int){
                super.onPageSelected(position)
                updateButtons(position)
            }
            
        })
        skipButton.setOnClickListener{
            navigateToMainScreen()
        }
        backButton.setOnClickListener{
            viewPager.currentItem = viewPager.currentItem-1
        }

        nextButton.setOnClickListener {
            val curerntPage = viewPager.currentItem
            if(curerntPage < adapter.itemCount -1) {
                viewPager.currentItem = curerntPage + 1
            }else{
                navigateToMainScreen()
            }
        }
    }
    private fun updateButtons(position: Int){
        val totalPages = viewPager.adapter?.itemCount?:0
        if (position == 0){
            backButton.visibility = View.GONE
        } else{
            backButton.visibility = View.VISIBLE
        }
        if (position == totalPages -1){
            nextButton.text= "GET STARTED"
        } else{
            nextButton.text = "NEXT"
        }
    }
    private fun navigateToMainScreen(){
        val intent = Intent(this, StartScreenActivity::class.java)
        startActivity(intent)
        finish()
    }
}
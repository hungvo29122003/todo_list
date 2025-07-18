package com.example.todo_list.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.todo_list.OnboardingFragment
import com.example.todo_list.R
import com.example.todo_list.model.OnboardingData

class OnboardingPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    
    private val onboardingPages = listOf(
        OnboardingData(
            R.drawable.logo_group,
            "Manage your tasks",
            "You can easily manage all of your daily \ntasks in DoMe for free"
        ),
        OnboardingData(
            R.drawable.logo_group2,
            "Create daily routine",
            "In Uptodo  you can create your \npersonalized routine to stay productive"
        ),
        OnboardingData(
            R.drawable.logo_group3,
            "Orgonaize your tasks",
            "You can organize your daily tasks by \nadding your tasks into separate categories"
        )
    )
    override fun getItemCount(): Int {
        return onboardingPages.size
    }

    @SuppressLint("SuspiciousIndentation")
    override fun createFragment(position: Int): Fragment {
        val pageData = onboardingPages[position]
            return OnboardingFragment.newInstance(
                pageData.imageResId,
                pageData.title,
                pageData.description
            )
    }
}
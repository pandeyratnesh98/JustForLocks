package com.example.justforlocks.ui.home

import android.os.Bundle
import android.view.DragEvent
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.viewpager.widget.ViewPager
import com.example.justforlocks.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*


class homeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        window.setBackgroundDrawableResource(R.drawable.main_gradient)
        val adapter = ViewPagerAdapter(supportFragmentManager)
       val homeFragment = home()
        val profileFragment = Profile()
        adapter.addFragment(homeFragment)
        adapter.addFragment(profileFragment)
        viewpager.setAdapter(adapter)
viewpager.isPagingEnabled=false
        bottomnav.setOnNavigationItemSelectedListener(
            object : BottomNavigationView.OnNavigationItemSelectedListener {
                override fun onNavigationItemSelected(item: MenuItem): Boolean {
                    when (item.getItemId()) {
                        R.id.home -> viewpager.setCurrentItem(0)
                        R.id.profile -> viewpager.setCurrentItem(1)
                    }
                    return true
                }
            })
    }




}



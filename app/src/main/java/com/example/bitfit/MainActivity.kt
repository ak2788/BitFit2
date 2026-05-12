package com.example.bitfit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    // Replaces the plain startActivity call
    private val addEntryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val navigateTo = result.data?.getStringExtra("navigate_to")
                if (navigateTo == "dashboard") {
                    bottomNavigation.selectedItemId = R.id.navigation_dashboard
                    replaceFragment(DashboardFragment())
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)

        replaceFragment(FoodLogFragment())

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_log -> replaceFragment(FoodLogFragment())
                R.id.navigation_dashboard -> replaceFragment(DashboardFragment())
            }
            true
        }
    }

    fun launchAddEntry() {
        addEntryLauncher.launch(Intent(this, AddEntryActivity::class.java))
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame_layout, fragment)
            .commit()
    }
}
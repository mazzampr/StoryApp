package com.mazzampr.storyapps.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.mazzampr.storyapps.R
import com.mazzampr.storyapps.ui.ViewModelFactory
import com.mazzampr.storyapps.ui.main.viewmodel.HomeViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<HomeViewModel> { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController

        viewModel.getSession().observe(this) {token ->
            if (token == null || token == "") {
                navController.popBackStack()
                navController.navigate(R.id.loginFragment)
            } else {
                navController.navigate(R.id.homeFragment)
            }
        }
    }
}
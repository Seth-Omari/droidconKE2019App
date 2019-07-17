package com.android254.droidconke19

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.android254.droidconke19.ui.authentication.AuthenticateUserActivity
import com.android254.droidconke19.ui.feedback.EventFeedbackActivity
import com.android254.droidconke19.utils.SharedPref.PREF_NAME
import com.android254.droidconke19.utils.SharedPref.TOKEN_SENT
import com.android254.droidconke19.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity() {
    private val sharedPreferences: SharedPreferences by lazy { getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) }
    private val firebaseRemoteConfig: FirebaseRemoteConfig by inject()
    private val firebaseMessaging: FirebaseMessaging by inject()
    private val firebaseAuth: FirebaseAuth by inject()
    private lateinit var params: AppBarLayout.LayoutParams
    private val homeViewModel: HomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)


        setupNavigation()
        setupActionBarWithNavController(findNavController(R.id.navHostFragment), drawer_layout)

        setupNotifications()

        //observe live data emitted by view model
        observeLiveData()

    }

    private fun setupNotifications() {
        firebaseMessaging.subscribeToTopic("all")

        if (BuildConfig.DEBUG) {
            firebaseMessaging.subscribeToTopic("debug")
        }
    }

    private fun setupNavigation() {
        val navController = Navigation.findNavController(this, R.id.navHostFragment)
        // Update action bar to reflect navigation
        NavigationUI.setupActionBarWithNavController(this, navController, drawer_layout)
        NavigationUI.setupWithNavController(nav_view, navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.sessionDetailsFragment -> {
                    toolbar.visibility = View.GONE
                }
                else -> toolbar.visibility = View.VISIBLE
            }
        }

    }

    private fun observeLiveData() {
        homeViewModel.getUpdateTokenResponse().observe(this, Observer {
            when {
                it -> {
                    sharedPreferences.edit().putInt(TOKEN_SENT, 1).apply()
                }
                else -> {
                    sharedPreferences.edit().putInt(TOKEN_SENT, 0).apply()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                //TODO add subcription logic after sign out
//                unsubscribeNotifications()
//                firebaseAuth.signOut()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun unsubscribeNotifications() = lifecycleScope.launch {
        firebaseMessaging.unsubscribeFromTopic("all").await()
        if (BuildConfig.DEBUG) {
            firebaseMessaging.unsubscribeFromTopic("debug").await()
        }

        // TODO Add unsubscription from favourites
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onSupportNavigateUp() =
            Navigation.findNavController(this, R.id.navHostFragment).navigateUp()

    companion object {
        var navItemIndex = 1 //controls toolbar titles and icons
        var fabVisible = true
    }
}

package net.chitholian.softphone

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationView
import net.chitholian.softphone.databinding.ActivityMainBinding
import net.chitholian.uilib.setupWithNavController

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var navControllerLiveData: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.mainToolbar)
        appBarConfiguration =
            AppBarConfiguration.Builder(R.id.dialerFragment, R.id.contactListFragment)
                .setOpenableLayout(binding.mainDrawer)
                .build()
        if (savedInstanceState == null) setUpNavigation()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setUpNavigation()
    }

    private fun setUpNavigation() {
        val bottomNavigationView = binding.mainBottomNavView
        val navigationView = binding.mainDrawerNavView
        navControllerLiveData = bottomNavigationView.setupWithNavController(
            listOf(R.navigation.dialer_nav_graph, R.navigation.contacts_nav_graph),
            supportFragmentManager,
            binding.mainFragmentContainer.id,
            intent
        )
        navigationView.setNavigationItemSelectedListener(this)
        navControllerLiveData?.observe(this) { navController ->
            setupActionBarWithNavController(navController, appBarConfiguration)
            NavigationUI.setupWithNavController(
                binding.mainToolbar,
                navController,
                appBarConfiguration
            )
            bottomNavigationView.setOnNavigationItemReselectedListener { }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navControllerLiveData?.value?.navigateUp() ?: super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

        }
        return true
    }

    override fun onBackPressed() {
        if (binding.mainDrawer.isOpen) binding.mainDrawer.close()
        else super.onBackPressed()
    }

    override fun onDestroy() {
        DialerFragment.dialPadShown = true
        super.onDestroy()
    }
}

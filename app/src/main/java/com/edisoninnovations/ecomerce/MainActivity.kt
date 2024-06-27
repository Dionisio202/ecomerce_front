package com.edisoninnovations.ecomerce

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.edisoninnovations.ecomerce.databinding.ActivityMainBinding
import com.edisoninnovations.ecomerce.request.RetrofitClient
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val EDIT_PROFILE_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow,
                R.id.nav_edit,
                R.id.nav_delete,
                R.id.nav_add
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Fetch and display user profile
        fetchUserProfile(navView)

        // Load user role and adjust menu
        adjustMenuForUserRole(navView)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, EditProfileActivity::class.java)
                startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Update user profile after edit
            val navView: NavigationView = binding.navView
            fetchUserProfile(navView)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun fetchUserProfile(navView: NavigationView) {
        val headerView = navView.getHeaderView(0)
        val textViewName: TextView = headerView.findViewById(R.id.textViewName)
        val textViewEmail: TextView = headerView.findViewById(R.id.textViewEmail)

        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val userProfile = RetrofitClient.instance.getProfile("Bearer $token")
                    textViewName.text = userProfile.name
                    textViewEmail.text = userProfile.email
                    with(sharedPreferences.edit()) {
                        putInt("user_id", userProfile.id)
                        apply()
                    }
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }

    private fun adjustMenuForUserRole(navView: NavigationView) {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val role = sharedPreferences.getString("role", "user")

        val menu = navView.menu
        menu.clear()

        if (role == "admin") {
            menu.add(0, R.id.nav_home, 0, "Home").setIcon(R.drawable.ic_menu_camera)
            menu.add(0, R.id.nav_edit, 1, "Editar").setIcon(R.drawable.user)
            menu.add(0, R.id.nav_delete, 2, "Eliminar").setIcon(R.drawable.user)
            menu.add(0, R.id.nav_add, 3, "Agregar").setIcon(R.drawable.user)
        } else {
            menu.add(0, R.id.nav_home, 0, "Home").setIcon(R.drawable.ic_menu_camera)
            menu.add(0, R.id.nav_gallery, 1, "Gallery").setIcon(R.drawable.ic_menu_gallery)
            menu.add(0, R.id.nav_slideshow, 2, "Slideshow").setIcon(R.drawable.ic_menu_slideshow)
        }
    }
}

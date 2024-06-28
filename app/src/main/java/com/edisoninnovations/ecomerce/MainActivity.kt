package com.edisoninnovations.ecomerce

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.edisoninnovations.ecomerce.databinding.ActivityMainBinding
import com.edisoninnovations.ecomerce.request.RetrofitClient
import com.google.android.material.navigation.NavigationView
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
            R.id.action_logout -> {
                logout()
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
                        println("################User ID: ${userProfile.id}")
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
            menu.add(0, R.id.nav_home, 0, "Home").setIcon(R.drawable.home)
            menu.add(0, R.id.nav_edit, 1, "Editar").setIcon(R.drawable.editar)
            menu.add(0, R.id.nav_delete, 2, "Eliminar").setIcon(R.drawable.eliminar)
            menu.add(0, R.id.nav_add, 3, "Agregar").setIcon(R.drawable.agregar)
        } else {
            menu.add(0, R.id.nav_home, 0, "Home").setIcon(R.drawable.home)
            menu.add(0, R.id.nav_gallery, 1, "Carrito").setIcon(R.drawable.carrito_large)
            menu.add(0, R.id.nav_slideshow, 2, "Compras").setIcon(R.drawable.compras)
        }
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("token")
            remove("user_id")
            remove("role")
            apply()
        }
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

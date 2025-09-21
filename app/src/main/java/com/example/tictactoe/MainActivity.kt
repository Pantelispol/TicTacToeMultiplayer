package com.example.tictactoe

//import Database
import Communicator
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.view.animation.AnimationUtils
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.room.Room
import com.example.tictactoe.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, Communicator {
    private lateinit var drawerLayout: DrawerLayout

    lateinit var binding: ActivityMainBinding

    private lateinit var auth : FirebaseAuth

    private lateinit var database : MyAppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            navigationView.setCheckedItem(R.id.nav_home)
        }
        auth = FirebaseAuth.getInstance()
        applyTheme()
        val switchEnabled = SwitchButtonManager.loadSwitchState(this)
        if (switchEnabled) {
            LanguageManager.setLanguage(this, "el") // Switch to Greek
            recreateWithAnimation() // Recreate activity with animation
        }

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }



    private fun applyTheme() {
        val darkModeEnabled = DarkModeManager.loadDarkModeState(this)
        if (darkModeEnabled) {
            setTheme(R.style.nightMode)
        } else {
            setTheme(R.style.Theme_TicTacToe)
        }
    }

    private fun recreateWithAnimation() {
        val fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        window.decorView.startAnimation(fadeAnimation)
        window.decorView.postDelayed({
            recreate()
        }, fadeAnimation.duration)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            R.id.nav_settting -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment()).commit()
            R.id.nav_play -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PlayFragment()).commit()
            R.id.nav_info -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, InfoFragment()).commit()
            R.id.nav_logout -> {
                showCustomDialogBox(null)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container,fragment)
        transaction.commit()
    }

    override fun passDataCom(editTextInput: String) {
        val bundle = Bundle()
        bundle.putString("message",editTextInput)

        val transaction = this.supportFragmentManager.beginTransaction()
        val settings = SettingsFragment()
        transaction.replace(R.id.fragment_container,settings)
        transaction.commit()

    }

    private fun showCustomDialogBox(message: String?){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_box)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnNo : MaterialButton = dialog.findViewById(R.id.btnDialogCancel)
        val btnYes : MaterialButton = dialog.findViewById(R.id.btnDialogLogout)
        btnYes.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this, SignInActivity::class.java))
        }
        btnNo.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }


}
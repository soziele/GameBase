package com.example.gamebase

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.custom_toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.dark_mode -> {
            // User chose the "Settings" item, show the app settings UI...
            true
        }

        R.id.about -> {
            // User chose the "Favorite" action, mark the current item
            // as a favorite...
            true
        }

        R.id.log_out -> {
            Log.d("Clicked", "Rozpoczecie proby wylogowania")
            signOut()
            Log.d("Response", "Wylogowano uzytkownika")
            Navigation.findNavController(this, R.id.fragment).navigate(R.id.action_gamesListFragment_to_loginFragment)
            true
        }
        else-> {
            false
        }
    }

    private fun signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
            .signOut(this)

        // [END auth_fui_signout]
    }

    companion object {

        const val RC_SIGN_IN = 123
    }
}
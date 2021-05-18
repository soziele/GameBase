package com.example.gamebase

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.setPadding
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.gamebase.model.Game
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.custom_toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        iconCheckboxControl()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.dark_mode -> {
            if (this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // dark mode disabled
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // dark mode enabled
            }
            iconCheckboxControl()
            true
        }

        R.id.about -> {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("About app")

            val noteInput = TextView(this)
            noteInput.text = "Game Base is an app created to help you organize games which you want to play and which you've already played. Thanks to Rawg API we can automatically tell you a little more about your favourite games if only the title is well-known and written properly."
            noteInput.setPadding(30)
            builder.setView(noteInput)
            builder.setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
            })
            builder.show()
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

    private fun iconCheckboxControl()
    {
        val b = (this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
        val c = (FirebaseAuth.getInstance().currentUser != null)
        Log.d("Dark mode checkbox", b.toString())
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.custom_toolbar).menu.getItem(0).isChecked = b
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.custom_toolbar).menu.getItem(2).isVisible = c
    }

    private fun signOut() {
        // [START auth_fui_signout]
        Firebase.auth.signOut()

        // [END auth_fui_signout]
    }

    companion object {

        const val RC_SIGN_IN = 123
        const val GOOGLE_SIGN_IN = 321
    }
}
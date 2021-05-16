package com.example.gamebase.viewModel

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.opengl.Visibility
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.gamebase.R
import com.example.gamebase.model.Game
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GamesListAdapter(var games: ArrayList<Game>, var databaseReference: DatabaseReference) : RecyclerView.Adapter<GamesListAdapter.GamesHolder>() {

    inner class GamesHolder(view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_row,parent,false)

        return GamesHolder(view)
    }

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: GamesHolder, position: Int) {
        val title = holder.itemView.findViewById<TextView>(R.id.game_title)
        val played = holder.itemView.findViewById<CheckBox>(R.id.played_box)
        val note = holder.itemView.findViewById<EditText>(R.id.user_note)
        val deleteButton = holder.itemView.findViewById<ImageButton>(R.id.delete_button)
        val expandableLayout = holder.itemView.findViewById<LinearLayout>(R.id.expandable_layout)

        title.text = games[position].title
        played.isChecked = games[position].played
        note.setText(games[position].note)

        deleteButton.setOnClickListener {
            databaseReference.child(FirebaseAuth.getInstance().currentUser.uid).child(games[position].key).removeValue()
        }
        note.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) databaseReference.child(FirebaseAuth.getInstance().currentUser.uid).child(games[position].key).child("note").setValue(note.text.toString())
        }
        played.setOnCheckedChangeListener { buttonView, isChecked ->
            databaseReference.child(FirebaseAuth.getInstance().currentUser.uid).child(games[position].key).child("played").setValue(isChecked)
        }

        expandableLayout.visibility = ConstraintLayout.GONE

        holder.itemView.setOnClickListener {
            if(expandableLayout.visibility == ConstraintLayout.GONE) expandableLayout.visibility = ConstraintLayout.VISIBLE
            else expandableLayout.visibility = ConstraintLayout.GONE
        }
    }

    override fun getItemCount(): Int {
        return games.size?:0
    }
}
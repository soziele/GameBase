package com.example.gamebase.viewModel

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.fromHtml
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.gamebase.R
import com.example.gamebase.model.Game
import com.example.gamebase.view.GamesListFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class GamesListAdapter(var games: ArrayList<Game>, var databaseReference: DatabaseReference, var context: GamesListFragment) : RecyclerView.Adapter<GamesListAdapter.GamesHolder>() {

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

        val releaseYear = holder.itemView.findViewById<TextView>(R.id.release_year)
        val genres = holder.itemView.findViewById<TextView>(R.id.genres)
        val description = holder.itemView.findViewById<TextView>(R.id.game_description)
        val releaseYearLabel = holder.itemView.findViewById<TextView>(R.id.release_year_label)
        val genresLabel = holder.itemView.findViewById<TextView>(R.id.genres_label)
        val descriptionLabel = holder.itemView.findViewById<TextView>(R.id.description_label)
        val gameImage = holder.itemView.findViewById<ImageView>(R.id.game_image)

        title.text = games[position].title.capitalize()
        played.isChecked = games[position].played
        note.setText(games[position].note)

        if(games[position].releaseYear == "" || games[position].releaseYear == null){
            releaseYearLabel.visibility = GONE
            releaseYear.visibility = GONE
        }
        else {
            releaseYear.text = games[position].releaseYear
        }
        if(games[position].genres?.isNullOrEmpty() == false && games[position].genres!![0]!!.name != null) {
            var genresList = ""
            for (genre in games[position].genres!!) {
                genresList += genre!!.name.toString() + "\n"
            }
            genres.text = genresList
        }
        else{
            genres.visibility = GONE
            genresLabel.visibility = GONE
        }

        if(games[position].imageUrl != "") {
            Picasso.with(holder.itemView.context).load(games[position].imageUrl).into(gameImage)
        }

        if(games[position].description == "" || games[position].description == null){
            descriptionLabel.visibility = GONE
            description.visibility = GONE
        }
        else{
            description.text = fromHtml(games[position].description, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        }


        if(context!!.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES){
            deleteButton.setImageResource(R.drawable.delete_white)
        }
        else{
            deleteButton.setImageResource(R.drawable.delete)
        }
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
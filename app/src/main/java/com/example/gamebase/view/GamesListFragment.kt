package com.example.gamebase.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.gamebase.R
import com.example.gamebase.model.Game
import com.example.gamebase.viewModel.GamesListAdapter
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.Console
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GamesListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GamesListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var myadapter: GamesListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var myLayoutManager: LinearLayoutManager
    private lateinit var databaseReference: DatabaseReference
    private lateinit var gamesList: ArrayList<Game>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        val fireBase = FirebaseDatabase.getInstance()
        databaseReference = fireBase.getReference("Game Base")

        databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Database error!", Toast.LENGTH_LONG)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                gamesList = ArrayList()
                for(row in snapshot.children){
                    if(row.key == FirebaseAuth.getInstance().currentUser.uid){
                    for(innerRow in row.children) {
                        val newRow = innerRow.getValue(Game::class.java)
                        gamesList.add(newRow!!)
                    }
                        break
                    }
                }
                setAdapter(gamesList)
            }
        })
    }

    private fun setAdapter(games: ArrayList<Game>){
        recyclerView.adapter = GamesListAdapter(games, databaseReference)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        myLayoutManager = LinearLayoutManager(context)
        gamesList = ArrayList()

        myadapter = GamesListAdapter(gamesList, databaseReference)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_games_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById<RecyclerView>(R.id.games_recyclerview).apply {
            this.layoutManager = myLayoutManager
            this.adapter = myadapter
        }

        view.findViewById<Button>(R.id.add_button).setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("What game do you want to add?")
            val titleInput = EditText(context)
            titleInput.setHint("Enter title")
            titleInput.width = 800
            val noteInput = EditText(context)
            noteInput.setHint("Enter note (Optional)")
            noteInput.width = 800
            val twoInputs = LinearLayout(context)
            twoInputs.addView(titleInput)
            twoInputs.addView(noteInput)
            twoInputs.orientation = LinearLayout.VERTICAL
            twoInputs.gravity = Gravity.CENTER
            builder.setView(twoInputs)

            builder.setPositiveButton("Add", DialogInterface.OnClickListener { dialog, which ->
                val title = titleInput.text.toString()
                val note = noteInput.text.toString()
                val played = false
                val key = Date().time
                val firebaseInput = Game(key.toString(), title, note, played)
                databaseReference.child(FirebaseAuth.getInstance().currentUser.uid).child("${key}").setValue(firebaseInput)
            })
            builder.show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GamesListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GamesListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
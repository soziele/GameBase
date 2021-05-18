package com.example.gamebase.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.*
import android.widget.LinearLayout.TEXT_ALIGNMENT_CENTER
import android.widget.LinearLayout.VERTICAL
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.core.view.marginTop
import androidx.core.view.setPadding
import androidx.core.view.size
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.gamebase.R
import com.example.gamebase.model.Game
import com.example.gamebase.repository.Repository
import com.example.gamebase.viewModel.GamesListAdapter
import com.example.gamebase.viewModel.UserViewModel
import com.example.gamebase.viewModel.UserViewModelFactory
import com.firebase.ui.auth.AuthUI
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
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
    private lateinit var userViewModel: UserViewModel
    private lateinit var newGame: MutableLiveData<Game>

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
                    if(FirebaseAuth.getInstance().currentUser != null) {
                        if (row.key == FirebaseAuth.getInstance().currentUser.uid) {
                            for (innerRow in row.children) {
                                if (innerRow.key != null && innerRow.value != "") {
                                    val newRow = innerRow.getValue(Game::class.java)
                                    gamesList.add(newRow!!)
                                }
                            }
                        }
                    }
                }
                setAdapter(gamesList)
            }
        })
    }

    private fun setAdapter(games: ArrayList<Game>){
        recyclerView.adapter = GamesListAdapter(games, databaseReference, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        myLayoutManager = LinearLayoutManager(context)
        gamesList = ArrayList()

        myadapter = GamesListAdapter(gamesList, databaseReference, this)

        val repository = Repository()
        val viewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this, viewModelFactory).get(UserViewModel::class.java)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_games_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var toolbar = requireActivity().findViewById<Toolbar>(R.id.custom_toolbar)
        toolbar.menu.getItem(2).isVisible = true


        recyclerView = view.findViewById<RecyclerView>(R.id.games_recyclerview).apply {
            this.layoutManager = myLayoutManager
            this.adapter = myadapter
        }

        view.findViewById<Button>(R.id.add_button).setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("What game do you want to add?")

            val relativeLayout = LinearLayout(requireContext())
            relativeLayout.minimumHeight = LinearLayout.LayoutParams.MATCH_PARENT
            relativeLayout.minimumWidth = LinearLayout.LayoutParams.MATCH_PARENT
            relativeLayout.orientation = VERTICAL

            val title = EditText(requireContext())
            title.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )

            title.layoutParams.height = 180
            title.minimumHeight = 180
            title.setPadding(0,50,0,0)

            val titleLayout = TextInputLayout(requireContext())
            titleLayout.hint = "Title"
            titleLayout.endIconMode = END_ICON_CLEAR_TEXT
            titleLayout.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            titleLayout.setPadding(40,0,40, 40)
            titleLayout.gravity = Gravity.CENTER_VERTICAL

            val editText = EditText(requireContext())
            editText.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )

            editText.layoutParams.height = 180
            editText.minimumHeight = 180
            editText.setPadding(0,50,0,0)


            val textInputLayout = TextInputLayout(requireContext())
            textInputLayout.hint = "Note (Optional)"
            textInputLayout.endIconMode = END_ICON_CLEAR_TEXT
            textInputLayout.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textInputLayout.setPadding(40,0,40, 40)
            textInputLayout.gravity = Gravity.CENTER_VERTICAL

            titleLayout.addView(title)
            relativeLayout.addView(titleLayout)
            textInputLayout.addView(editText)
            relativeLayout.addView(textInputLayout)
            builder.setView(relativeLayout)

            builder.setPositiveButton("Add", DialogInterface.OnClickListener { dialog, which ->
                val title = title.text.toString()
                val note = editText.text.toString()
                val played = false
                val key = Date().time

                newGame = MutableLiveData(Game(key = key.toString(), title = title, note = note, played = played))

                userViewModel.getGame(title.toLowerCase().replace(' ', '-'))
                userViewModel.gameResponse.observe(viewLifecycleOwner, Observer{ response->
                    if(response.isSuccessful){
                        Log.d("Response", response.body()!!.description.toString())
                        newGame.value = Game(key = key.toString(), title = title, releaseYear = response.body()!!.released.toString(), imageUrl = response.body()!!.background_image.toString(), description = response.body()!!.description.toString(), genres = response.body()!!.genres, note = note, played = played)
                    }
                    else{
                        Log.e("Adding response failed", response.errorBody().toString())
                        newGame.value = Game(key = key.toString(), title = title, note = note, played = played)
                    }
                })
                newGame.observe(viewLifecycleOwner, Observer { game ->
                    databaseReference.child(FirebaseAuth.getInstance().currentUser.uid).child("${key}").setValue(game)
                })
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


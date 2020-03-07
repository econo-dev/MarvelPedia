package com.gal.marvelpedia

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.gal.marvelpedia.LoginActivity.Companion.userId
import com.gal.marvelpedia.adapters.FavouritesRecyclerAdapter
import kotlinx.android.synthetic.main.activity_favourites.*

class FavouritesActivity : AppCompatActivity() {
    companion object FavList{
        var favouriteCharactersList = ArrayList<Character>()
    }
    var layoutManger: RecyclerView.LayoutManager? = null
    var adapter: RecyclerView.Adapter<FavouritesRecyclerAdapter.ViewHolder>? = null
    private var databaseHelper: DatabaseHelper? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)
        setPointer()
    }

    private fun setPointer() {
        databaseHelper = DatabaseHelper(this)
        setFavListAdapter()
    }

    private fun setFavListAdapter(){
        layoutManger = LinearLayoutManager(this)
        favourites_recycler_view.layoutManager = layoutManger

        if (favouriteCharactersList.isNotEmpty()){
            not_saved_text.visibility = View.GONE
        }

        if (LoginActivity.isLogged){
            getListFromDB()
            favourites_recycler_view.adapter = FavouritesRecyclerAdapter(favouriteCharactersList)
        } else {
            Toast.makeText(this, getString(R.string.please_login), Toast.LENGTH_LONG).show()
        }
    }

    private fun getListFromDB() {
        favouriteCharactersList = databaseHelper!!.getUserCharacters(userId!!)
    }
}

package com.gal.marvelpedia.adapters

import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.gal.marvelpedia.*
import com.gal.marvelpedia.FavouritesActivity.FavList.favouriteCharactersList
import com.gal.marvelpedia.LoginActivity.Companion.userId
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_favourites.*
import kotlinx.android.synthetic.main.activity_favourites.view.*


class FavouritesRecyclerAdapter(characters: ArrayList<Character>) :
    RecyclerView.Adapter<FavouritesRecyclerAdapter.ViewHolder>() {

    private val images = intArrayOf(R.drawable.example)
    //    var titles: ArrayList<String> = title
//    var details: ArrayList<String> = detail
//    var summaries: ArrayList<String> = summary
//    var thumbnails: ArrayList<String> = images

    var databaseHelper: DatabaseHelper? = null
    var charactersList: ArrayList<Character> = characters
    val THUMBNAIL_SIZE = "/landscape_medium.jpg"

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context
        var itemImage: ImageView
        var itemTitle: TextView
        var itemDetail: TextView
//        var detailedImage :ImageView
//        lateinit var searchBar:SearchView

        //connection to our card_View layout
        init {
            itemImage = itemView.findViewById(R.id.item_image) as ImageView
            itemTitle = itemView.findViewById(R.id.item_title) as TextView
            itemDetail = itemView.findViewById(R.id.item_detail) as TextView

            databaseHelper = DatabaseHelper(context)
//            detailedImage = itemView.findViewById(R.id.imgPreview)
//            searchBar = itemView.findViewById(R.id.searchBar)


            itemView.setOnClickListener { view: View ->
                val position: Int = adapterPosition
                /* this snackbar is for categories arrayList approach
                Snackbar.make(view, summaries[position],
                    Snackbar.LENGTH_LONG).setAction("Action $position",null).show() */
                // object-based snackbar
                Snackbar.make(
                    view,
                    charactersList[position].getDateModified(),
                    Snackbar.LENGTH_SHORT
                ).setAction("Action $position", null).show()
//                Picasso.get().load(thumbnails[position]+".jpg").into(detailedImage)
                val intent = Intent(
                    context,
                    CharacterDetailWebView::class.java
                ) //.apply{putExtra("key","value")}
                    .apply {
                        putExtra("url", charactersList[position].getWikiURL())
                        putExtra("titleName", charactersList[position].getName())
                        Log.e("itemView_onClick", charactersList[position].getWikiURL())
                    }
                context.startActivity(intent)
            }

            itemView.setOnLongClickListener { view: View ->
                val position: Int = adapterPosition
                val name = charactersList[position].getName()
                Log.e("FAV ", "$userId $name $position")
                databaseHelper!!.removeUserCharacter(userId!!, name)
                FavouritesActivity.favouriteCharactersList.remove(charactersList[position])
                Toast.makeText(
                    context,
                    "${itemTitle.text} was removed from favourites!",
                    Toast.LENGTH_SHORT
                ).show()
                notifyDataSetChanged()

                true
            }
        }
    }


    override fun getItemCount(): Int {
//        return titles.size
        return charactersList.size
    }

    //create only the template for the view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(view)
    }

    //change the data of our view holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemTitle.text =
            charactersList[position].getName() /*titles[position] */ // + mock -> mymock for testing
        holder.itemDetail.text = charactersList[position].getDescription() /* details[position] */
//        holder.itemImage.setImageResource(R.drawable.example)
//        holder.itemImage
//        Picasso.get().load(thumbnails[position]+"/landscape_medium.jpg").into(holder.itemImage)

        Picasso.get().load(charactersList[position].getThumbnail() + THUMBNAIL_SIZE)
            .into(holder.itemImage)
    }

//    fun getImagesFromURL(url: String, index: Int){
////        Picasso.get().load(thumbnails[index])
//        Picasso.get().load(charactersList[index].getThumbnail())
//    }

}

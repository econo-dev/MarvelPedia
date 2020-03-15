package com.gal.marvelpedia.adapters

import android.content.ActivityNotFoundException
import android.content.ContentProvider
import android.content.Context
import android.content.Intent
import android.content.Intent.createChooser
import android.net.Uri
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.FileProvider
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.gal.marvelpedia.*
import com.gal.marvelpedia.LoginActivity.Companion.isLogged
import com.gal.marvelpedia.LoginActivity.Companion.userId
import com.squareup.picasso.Picasso


// this inside RecyclerAdapter(...) -> title: ArrayList<String>, detail: ArrayList<String>, summary: ArrayList<String>, images: ArrayList<String>
class RecyclerAdapter(characters: ArrayList<Character>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var databaseHelper: DatabaseHelper? = null
    private val images = intArrayOf(R.drawable.example)

    //    var titles: ArrayList<String> = title
//    var details: ArrayList<String> = detail
//    var summaries: ArrayList<String> = summary
//    var thumbnails: ArrayList<String> = images
    var charactersList: ArrayList<Character> = characters
    val THUMBNAIL_SIZE = "/landscape_medium.jpg"

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context
        val itemImage: ImageView
        val itemTitle: TextView
        val itemDetail: TextView
        val itemShare: ImageView
//        var detailedImage :ImageView
//        lateinit var searchBar:SearchView

        //connection to our card_View layout
        init {
            itemImage = itemView.findViewById(R.id.item_image) as ImageView
            itemTitle = itemView.findViewById(R.id.item_title) as TextView
            itemDetail = itemView.findViewById(R.id.item_detail) as TextView
            itemShare = itemView.findViewById(R.id.item_share) as ImageView
//            detailedImage = itemView.findViewById(R.id.imgPreview)
//            searchBar = itemView.findViewById(R.id.searchBar)

            itemView.setOnClickListener { view: View ->
                val position: Int = adapterPosition
                /* this snackbar is for categories arrayList approach
                Snackbar.make(view, summaries[position],
                    Snackbar.LENGTH_LONG).setAction("Action $position",null).show() */
                // object-based snackbar
//                Snackbar.make(view, charactersList[position].getDateModified(), Snackbar.LENGTH_SHORT).setAction("Action $position", null).show()

                if (charactersList[position].getWikiURL().isNullOrBlank()) {
                    Snackbar.make(
                        view,
                        context.getString(R.string.no_extra_info),
                        Snackbar.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                if (charactersList[position].getDetailsURL().isNullOrBlank()) {
                    Snackbar.make(
                        view,
                        context.getString(R.string.no_extra_info),
                        Snackbar.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
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
                databaseHelper = DatabaseHelper(context)
                if (!isLogged) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.login_to_save),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnLongClickListener true
                }
                if (!FavouritesActivity.favouriteCharactersList.contains(charactersList[position])) {
                    FavouritesActivity.favouriteCharactersList.add(charactersList[position])
                    databaseHelper?.addUserCharacter(userId!!.toLong(), charactersList[position])
                    Toast.makeText(
                        context,
                        "${itemTitle.text}" + context.getString(R.string.added_to_favourites),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "${itemTitle.text}" + context.getString(R.string.already_added),
                        Toast.LENGTH_LONG
                    ).show()
                }

                true
            }

            itemShare.setOnClickListener { view: View ->
                val position = adapterPosition
                if (charactersList[position].getDetailsURL().isNullOrBlank()
                    || charactersList[position].getWikiURL().isNullOrBlank()
                ){
                    Snackbar.make(view, context.getString(R.string.nothing_to_share), Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                    openShareIntent(context, charactersList[position].getWikiURL())
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

    private fun openShareIntent(context: Context, text: String) {
        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.type = "text/plain"
//                whatsappIntent.setPackage("com.whatsapp")
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Check out this cool Marvel character: ")
//                whatsappIntent.putExtra(Intent.EXTRA_SUBJECT, " AWESOME")
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_text) + text)

//                val whatsappIntent = Intent().apply {
//                    action = Intent.ACTION_SEND
//                    val uri = Uri.parse(itemImage)
//                    putExtra(Intent.EXTRA_STREAM, uri)
//                    type = "*/*"
//                }
        try {
//                    val shareIntent = Intent.createChooser(whatsappIntent, "title")
            val shareIntent = Intent.createChooser(whatsappIntent, "MARVEL")
            context.startActivity(shareIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context, "WhatsApp have not been installed", Toast.LENGTH_SHORT).show()
        }
    }
//    fun getImagesFromURL(url: String, index: Int){
////        Picasso.get().load(thumbnails[index])
//        Picasso.get().load(charactersList[index].getThumbnail())
//    }

}




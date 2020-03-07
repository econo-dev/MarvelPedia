package com.gal.marvelpedia.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gal.marvelpedia.R


class NavRecyclerAdapter(list: Array<String>, val clickListener: (Int) -> Unit): RecyclerView.Adapter<NavRecyclerAdapter.ViewHolder>() {
    companion object{
        var selectionPosition:Int = 0
    }
    private var listOfMenuItems = list
    private val lettersAtoZ = arrayOf("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z")

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val context = itemView.context
        var itemMenu: TextView? = itemView.findViewById(R.id.item_menu)
//        var detailedImage :ImageView
//        lateinit var searchBar:SearchView

        //connection to our card_View layout
        init {

            itemView.setOnClickListener{
                    _:View ->
                var position:Int = adapterPosition
                Log.e("MENU", listOfMenuItems[position])
                selectionPosition = position

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.nav_menu_card, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfMenuItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Int = position

        holder.itemView?.setOnClickListener { clickListener(item) }
        holder.itemMenu?.text = listOfMenuItems[position]
    }
}


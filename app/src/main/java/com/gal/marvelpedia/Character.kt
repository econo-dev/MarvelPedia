package com.gal.marvelpedia

/* class for marvel comic character */

class Character {

    //properties
    private var name: String? = null
    private var description: String? = null
    private var thumbnail: String? = null
    private var dateModified: String? = null
    private var detailsURL: String? = null
    private var wikiURL: String? = null

//    constructor(charName: String, charDescription: String, charThumbnail: String, charDateModified: String) {
//        name = charName
//        description = charDescription
//        thumbnail = charThumbnail
//        dateModified = charDateModified
//    }
//
    constructor(
        name: String,
        description: String,
        thumbnail: String,
        dateModified: String,
        detailsURL: String,
        wikiURL: String
    ) {
        this.name = name
        this.description = description
        this.thumbnail = thumbnail
        this.dateModified = dateModified
        this.detailsURL = detailsURL
        this.wikiURL = wikiURL
    }

    /*
    constructor(charName: String, charDescription: String, charThumbnail: String, charDateModified: String) {
        name = charName
        description = charDescription
        thumbnail = charThumbnail
        dateModified = charDateModified
    }
     */
    //member func
    fun getName(): String{
        return name.toString()
    }

    fun getDescription(): String{
        return description.toString()
    }

    fun getThumbnail(): String{
        return thumbnail.toString()
    }

    fun getDateModified(): String{
        return dateModified.toString()
    }

    fun getDetailsURL(): String{
        return detailsURL.toString()
    }

    fun getWikiURL(): String{
        return wikiURL.toString()
    }


    fun setName(name: String){
        this.name = name
    }

    fun setDescription(description: String){
        this.description = description
    }

    fun setThumbnail(thumbnail: String){
        this.thumbnail = thumbnail
    }

    fun setDateModified(dateModified: String){
        this.dateModified = dateModified
    }

    fun setDetailsURL(detailsURL: String){
        this.detailsURL = detailsURL
    }

    fun setWikiURL(wikiURL: String){
        this.wikiURL = wikiURL
    }

}
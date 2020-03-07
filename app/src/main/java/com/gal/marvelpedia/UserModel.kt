package com.gal.marvelpedia

import java.io.Serializable

class UserModel : Serializable {

    var name: String? = null
    var password: String? = null
    var favourites: ArrayList<Character>? = null
    var id: Int = 0


    fun getIds(): Int {
        return id
    }

    fun setIds(id: Int) {
        this.id = id
    }

    fun getNames(): String {
        return name.toString()
    }

    fun setNames(name: String) {
        this.name = name
    }

    fun getPasswords(): String {
        return password.toString()
    }

    fun setPasswords(password: String){
        this.password = password
    }

    fun getUserFavourites(): ArrayList<Character>? {
        return favourites
    }

    fun setUserFavourites(favourites: ArrayList<Character>) {
        this.favourites = favourites
    }
}

//class UserModel(
//    val id: Int = 0, val name: String, val password: String, favouriteList: ArrayList<Character>? = null
//    )
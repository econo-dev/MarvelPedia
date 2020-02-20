package com.gal.marvelpedia

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLite_Users(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version), UserAble {

    val DB_NAME:String = "marvel_users.db"
    val TABLE_NAME = "users"
    var sqliteDB : SQLiteDatabase = this.writableDatabase


    override fun onCreate(p0: SQLiteDatabase?) {

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addNewUser(userName: String, password1: String, password2: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isUserExists(userName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteUser(userName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

interface UserAble{
    fun addNewUser(userName: String, password1: String, password2: String)
    fun isUserExists(userName: String)
    fun deleteUser(userName: String)
}
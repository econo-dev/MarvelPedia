package com.gal.marvelpedia


import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.lang.Exception
import kotlin.collections.ArrayList

/**
 * Created by Parsania Hardik on 11/01/2016.
 * Modified by Economical.Dev on 04/03/2020.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // looping through all rows and adding to list
    //getting user hobby where id = id from user_hobby table
    //SQLiteDatabase dbhobby = this.getReadableDatabase();
    //getting user city where id = id from user_city table
    //SQLiteDatabase dbCity = this.getReadableDatabase();
    // adding to Students list
    val allUsers: ArrayList<UserModel>
        get() {
            val userModelArrayList = ArrayList<UserModel>()

            val selectQuery = "SELECT  * FROM $TABLE_USER"
            val db = this.readableDatabase
            val c = db.rawQuery(selectQuery, null)
            if (c.moveToFirst()) {
                do {
                    val userModel = UserModel()
                    userModel.setIds(c.getInt(c.getColumnIndex(KEY_ID)))
                    userModel.setNames(c.getString(c.getColumnIndex(KEY_FIRSTNAME)))
                    userModel.setPasswords(c.getString(c.getColumnIndex(KEY_PASSWORD)))
                    userModelArrayList.add(userModel)
                } while (c.moveToNext())
            }
            return userModelArrayList
        }


    init {
        Log.d("table", CREATE_TABLE_USERS)
    }

    val allUserCharacters: ArrayList<Character>
    get() {
        val userCharactersArrayList = ArrayList<Character>()

        val selectQuery = "SELECT * FROM $TABLE_USER_CHARACTERS"
        val db = this.readableDatabase
        val c = db.rawQuery(selectQuery, null)

        if (c.moveToFirst()) {
            do {
                val userModel = UserModel()

                val selectCharacterQuery = "SELECT * FROM " + TABLE_USER_CHARACTERS + " WHERE " + KEY_ID + " = " + userModel.getIds()
                val cCharacter = db.rawQuery(selectCharacterQuery, null)

                if (cCharacter.moveToFirst()) {
                    do {
                        val character = Character(
                            cCharacter.getString(cCharacter.getColumnIndex(
                                KEY_NAME)),
                            cCharacter.getString(cCharacter.getColumnIndex(KEY_DESCRIPTION)),
                            cCharacter.getString(cCharacter.getColumnIndex(KEY_THUMBNAIL)),
                            cCharacter.getString(cCharacter.getColumnIndex(KEY_DATE_MODIFIED)),
                            cCharacter.getString(cCharacter.getColumnIndex(KEY_DETAILS_URL)),
                            cCharacter.getString(cCharacter.getColumnIndex(KEY_WIKI_URL))
                            )
                        userCharactersArrayList.add(character)
                    } while (cCharacter.moveToNext())
                }
            } while (c.moveToNext())
        }

        return userCharactersArrayList
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_USERS)
        db.execSQL(CREATE_TABLE_USER_CHARACTERS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS '$TABLE_USER'")
        db.execSQL("DROP TABLE IF EXISTS '$TABLE_USER_CHARACTERS'")
        onCreate(db)
    }

    fun getLoggedUsers(userName: String): ArrayList<UserModel> {
        val userModelArrayList = ArrayList<UserModel>()
//  $KEY_ID $KEY_FIRSTNAME, $KEY_PASSWORD
        val selectQuery = "SELECT * FROM $TABLE_USER WHERE $KEY_FIRSTNAME = '$userName'"
        val db = this.readableDatabase
        val c = db.rawQuery(selectQuery, null)
        if (c.moveToFirst()) {
            do {
                val userModel = UserModel()
                userModel.setIds(c.getInt(c.getColumnIndex(KEY_ID)))
                userModel.setNames(c.getString(c.getColumnIndex(KEY_FIRSTNAME)))
                userModel.setPasswords(c.getString(c.getColumnIndex(KEY_PASSWORD)))
                userModelArrayList.add(userModel)
                Log.e("USER ", "${userModel.name} ${userModel.password} ${userModel.id}")
            } while (c.moveToNext())
        }
        return userModelArrayList
    }

    fun getUserCharacters(userId: Int): ArrayList<Character> {
        val userCharactersArrayList = ArrayList<Character>()

        val selectQuery = "SELECT * FROM $TABLE_USER_CHARACTERS WHERE $KEY_ID = '$userId'"
        val db = this.readableDatabase
        val c = db.rawQuery(selectQuery, null)

        if (c.moveToFirst()) {
            do {
//                val selectCharacterQuery = "SELECT * FROM " + TABLE_USER_CHARACTERS + " WHERE " + KEY_ID + " = " + userModel.getIds()
//                val cCharacter = db.rawQuery(selectCharacterQuery, null)

                if (c.moveToFirst()) {
                    do {
                        val character = Character(
                            c.getString(c.getColumnIndex(KEY_NAME)),
                            c.getString(c.getColumnIndex(KEY_DESCRIPTION)),
                            c.getString(c.getColumnIndex(KEY_THUMBNAIL)),
                            c.getString(c.getColumnIndex(KEY_DATE_MODIFIED)),
                            c.getString(c.getColumnIndex(KEY_DETAILS_URL)),
                            c.getString(c.getColumnIndex(KEY_WIKI_URL))
                        )
                        userCharactersArrayList.add(character)
                    } while (c.moveToNext())
                }
            } while (c.moveToNext())
        }
        return userCharactersArrayList
    }

    fun addUser(name: String, password: String) {
        val db = this.writableDatabase
        //adding user name in users table
        val values = ContentValues()

        values.put(KEY_FIRSTNAME, name)
        values.put(KEY_PASSWORD, password)
//         db.insert(TABLE_USER, null, values);
        db.insertWithOnConflict(TABLE_USER, null, values, SQLiteDatabase.CONFLICT_IGNORE)
//        val id = db.insertWithOnConflict(TABLE_USER, null, values, SQLiteDatabase.CONFLICT_IGNORE)

    }

    fun addUserCharacter(id: Long, character: Character){
        val db = this.writableDatabase
        val valuesCharacters = ContentValues()

        valuesCharacters.put(KEY_ID, id)
        valuesCharacters.put(KEY_NAME, character.getName())
        valuesCharacters.put(KEY_DESCRIPTION, character.getDescription())
        valuesCharacters.put(KEY_DATE_MODIFIED, character.getDateModified())
        valuesCharacters.put(KEY_THUMBNAIL, character.getThumbnail())
        valuesCharacters.put(KEY_DETAILS_URL, character.getDetailsURL())
        valuesCharacters.put(KEY_WIKI_URL, character.getWikiURL())

        db.insert(TABLE_USER_CHARACTERS, null, valuesCharacters)
    }

    fun updateUser(id: Int, name: String, character: Character) {
        val db = this.writableDatabase

        // updating name in users table
//        val values = ContentValues()
//        values.put(KEY_FIRSTNAME, name)
//        db.update(TABLE_USER, values, "$KEY_ID = ?", arrayOf(id.toString()))

        // updating character in users_characters table
        val valuesCharacters = ContentValues()
        valuesCharacters.put(KEY_NAME, character.getName())
        valuesCharacters.put(KEY_DESCRIPTION, character.getDescription())
        valuesCharacters.put(KEY_DATE_MODIFIED, character.getDateModified())
        valuesCharacters.put(KEY_THUMBNAIL, character.getThumbnail())
        valuesCharacters.put(KEY_DETAILS_URL, character.getDetailsURL())
        valuesCharacters.put(KEY_WIKI_URL, character.getWikiURL())

        db.update(TABLE_USER_CHARACTERS, valuesCharacters, "$KEY_ID = ?", arrayOf(id.toString()))

    }

    fun deleteUser(id: Int) {

        // delete row in students table based on id
        val db = this.writableDatabase

        //deleting from users table
        db.delete(TABLE_USER, "$KEY_ID = ?", arrayOf(id.toString()))

        //deleting from table
        db.delete(TABLE_USER_CHARACTERS, "$KEY_ID = ?", arrayOf(id.toString()))

    }

    fun removeUserCharacter(userId: Int, character: Character){
        val db = this.writableDatabase
        val name = character.getName()
        val whereQuery = "${KEY_ID} = ? AND ${KEY_NAME} = ?"
        //deleting from table
        try {
            db.execSQL(" DELETE FROM "+ TABLE_USER_CHARACTERS + " WHERE " + KEY_ID + "=\"" + userId.toString() + "\" AND " + KEY_NAME + "=\"" + name +"\";")
        } catch (e: SQLException){
            Log.e("SQL ERR ", e.message)
        }
//        db.delete(TABLE_USER_CHARACTERS, whereQuery, arrayOf(userId.toString(), name))>0
    }

    companion object {

        var DATABASE_NAME = "user_database"
        private val DATABASE_VERSION = 1
        private val TABLE_USER = "users"
        private val TABLE_USER_CHARACTERS = "users_characters"
        private val KEY_ID = "id"
        private val KEY_FIRSTNAME = "name"
        private val KEY_PASSWORD = "password"
        private val KEY_NAME = "name"
        private val KEY_DESCRIPTION = "description"
        private val KEY_THUMBNAIL = "thumbnail"
        private val KEY_DATE_MODIFIED = "dateModified"
        private val KEY_DETAILS_URL= "detailsURL"
        private val KEY_WIKI_URL = "wikiURL"

        /*

        name: String,
        description: String,
        thumbnail: String,
        dateModified: String,
        detailsURL: String,
        wikiURL: String
        * */

        /*CREATE TABLE students ( id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone_number TEXT......);*/

        private val CREATE_TABLE_USERS = ("CREATE TABLE "
                + TABLE_USER + "(" + KEY_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_FIRSTNAME + " TEXT, " + KEY_PASSWORD + " TEXT )")

        private val CREATE_TABLE_USER_CHARACTERS = ("CREATE TABLE "
                + TABLE_USER_CHARACTERS + "(" + KEY_ID + " INTEGER,"
                + KEY_NAME + " TEXT, "
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_THUMBNAIL + " TEXT, "
                + KEY_DATE_MODIFIED + " TEXT, "
                + KEY_DETAILS_URL + " TEXT, "
                + KEY_WIKI_URL + " TEXT );")

    }

}
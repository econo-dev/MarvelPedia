package com.gal.marvelpedia

import android.provider.BaseColumns

object DBContract {
    // inner class to define the table content
    class UserEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "users"
            val COLUMN_USER_ID = "userid"
            val COLUMN_NAME = "name"
            val COLUMN_PASSWORD = "password"

            val TABLE_FVOURITES = "user_favourites"
            val COLUMN_FAVOURITES = "favourites"
        }
    }
}
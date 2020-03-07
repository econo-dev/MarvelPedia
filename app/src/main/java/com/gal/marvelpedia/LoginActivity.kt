package com.gal.marvelpedia

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card_layout.view.*
import kotlinx.android.synthetic.main.nav_menu_card.view.*


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        var isLogged = false
        var userId: Int? = null
        lateinit var userName: String
    }

    lateinit var SQLiteDatabaseHelper: DatabaseHelper



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setPointer()
    }

    private fun setPointer() {
        SQLiteDatabaseHelper = DatabaseHelper(this)
        btnLoginCancel.setOnClickListener(this)
        btnLogin.setOnClickListener(this)
        register_text.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id){
            R.id.btnLoginCancel -> finish()
            R.id.register_text -> { startActivity(Intent(this, RegisterActivity::class.java)) }
            R.id.btnLogin -> {
                //TODO mock data for user. Need to change readUser Method userid:66b25a27-a34d-4bb0-bfe8-7eecccf18703
//                val user: ArrayList<UserModel> = SQLiteDatabaseHelper.readUser(txtLoginUser.text.toString())
//                Log.e(" USER ", user[0].name+"@ "+user[0].password+" # "+user[0].userId)
                if ( txtLoginUser.text.isEmpty() || txtLoginPass.text.isEmpty() ){
                    Toast.makeText(this, "User name or password are empty", Toast.LENGTH_SHORT).show()
                    return}

//                val user: ArrayList<UserModel> = SQLiteDatabaseHelper.allUsers
                val user: ArrayList<UserModel> = SQLiteDatabaseHelper.getLoggedUsers(txtLoginUser.text.toString())
                if (user.isEmpty()) {
                    Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                    return
                }
                if (user[0].getNames() == txtLoginUser.text.toString() && user[0].getPasswords() == txtLoginPass.text.toString() ){
                    Toast.makeText(this, "User ${user[0].name} Logged-in Successfully", Toast.LENGTH_SHORT).show()
                    isLogged = true
                    userId = user[0].getIds()
                    userName = user[0].name!!
                    Log.e("LOGIN", " $userName $userId")
                    navView?.menu?.findItem(R.id.drawer_title)?.title = userName
                    finish()
                } else Toast.makeText(this, "Check user name or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

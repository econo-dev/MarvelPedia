package com.gal.marvelpedia

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_menu_card.*


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        var isLogged = false
        var userId: Int? = null
        lateinit var userName: String

        val SHARED_PREF = "logged_user"
        val LOGGED_USER_NAME_KEY: String = "user_name"
        val LOGGED_USER_ID_KEY: String = "user_id"
    }

    lateinit var SQLiteDatabaseHelper: DatabaseHelper

//    val stayLoggedCheckBox: CheckBox = findViewById(R.id.stayLogged_cbx)


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
        stayLogged_cbx.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnLoginCancel -> finish()
            R.id.register_text -> { startActivity(Intent(this, RegisterActivity::class.java)) }
            R.id.btnLogin -> {
//                val user: ArrayList<UserModel> = SQLiteDatabaseHelper.readUser(txtLoginUser.text.toString())
//                Log.e(" USER ", user[0].name+"@ "+user[0].password+" # "+user[0].userId)
                if (txtLoginUser.text.isEmpty() || txtLoginPass.text.isEmpty()) {
                    Toast.makeText(this, getString(R.string.credentials_empty), Toast.LENGTH_SHORT)
                        .show()
                    return
                }

//                val user: ArrayList<UserModel> = SQLiteDatabaseHelper.allUsers
                val user: ArrayList<UserModel> =
                    SQLiteDatabaseHelper.getLoggedUsers(txtLoginUser.text.toString())
                if (user.isEmpty()) {
                    Toast.makeText(this, getString(R.string.user_not_exist), Toast.LENGTH_SHORT).show()
                    return
                }
                if (user[0].getNames() == txtLoginUser.text.toString() && user[0].getPasswords() == txtLoginPass.text.toString()) {
                    Toast.makeText(
                        this, user[0].name + getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    isLogged = true
                    userId = user[0].getIds()
                    userName = user[0].name!!
                    Log.e("LOGIN", " $userName $userId")

                    if (stayLogged_cbx.isChecked){
                        setRemeberUser(userName, userId!!)
                        isLogged = true
                    }
                    navView?.menu?.findItem(R.id.drawer_title)?.title = userName
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                } else Toast.makeText(this, getString(R.string.check_credentials), Toast.LENGTH_SHORT).show()
            }
            R.id.stayLogged_cbx -> {

            }
        }
    }

    fun setRemeberUser(userName: String, userId: Int){
        val sp: SharedPreferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        val spe = sp.edit()

        spe.putString(LOGGED_USER_NAME_KEY, userName)
        spe.putInt(LOGGED_USER_ID_KEY, userId)
        spe.apply()
    }
}

package com.gal.marvelpedia

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.gal.marvelpedia.LoginActivity.Companion.LOGGED_USER_ID_KEY
import com.gal.marvelpedia.LoginActivity.Companion.LOGGED_USER_NAME_KEY
import com.gal.marvelpedia.LoginActivity.Companion.isLogged
import com.gal.marvelpedia.LoginActivity.Companion.userId
import com.gal.marvelpedia.LoginActivity.Companion.userName
import com.gal.marvelpedia.adapters.NavRecyclerAdapter
import com.gal.marvelpedia.adapters.RecyclerAdapter
import com.gal.marvelpedia.utilities.SpinnerLoader
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity(), View.OnClickListener,
    NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {
    var layoutManger: RecyclerView.LayoutManager? = null
    var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null

    var menuLayoutManager: RecyclerView.LayoutManager? = null
    var navMenuAdapter: RecyclerView.Adapter<NavRecyclerAdapter.ViewHolder>? = null

    // api connection
    val CONNECTION_TIMEOUT_MILLISECONDS = 60 * 1_000

    val API_KEY = "76bfded27255952b203b27148d1b71fd"
    val HASH_CODE = "bea98a0dcae226b8392394770170756a"
    val SP_DEFAULT_USER = "Guest"

    var searchNameStartsWith = ""
    var MARVEL_ALL_CHARACTERS =
        "https://gateway.marvel.com/v1/public/characters?nameStartsWith=${searchNameStartsWith}&limit=50&ts=1&apikey=${API_KEY}&hash=${HASH_CODE}"
    var MARVEL_URL =
        "https://gateway.marvel.com/v1/public/characters?name=Spider-man&orderBy=name&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"

    var titles = ArrayList<String>()
    var details = ArrayList<String>()
    var modifiedDate = ArrayList<String>()
    var images = ArrayList<String>()
    var detailImage = ArrayList<String>()

    var characterUrlRequest = ArrayList<Int>()
    //array of Character obj
    var charactersList = ArrayList<Character>()

    var MARVEL_CHARACTERS: String = ""

    var textLength = 0

    val lettersAtoZ = arrayOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z"
    )

    lateinit var sp: SharedPreferences
    // nav drawer layout objects
//    lateinit var toolbar: Toolbar
    lateinit var drawer: DrawerLayout
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //drawer
//        toolbar = findViewById(R.id.toolbar)
        drawer = findViewById(R.id.activity_main_drawer)
        navView = findViewById(R.id.navView)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        var toggle = object :ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close
        ){
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                animateDrawerImage()
            }
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                hideKeyboard()
            }
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
//                navView.menu?.findItem(R.id.drawer_title)?.title = userName
            }
        }

        drawer.addDrawerListener(toggle)
        toggle.syncState()



//        var anima: Animation = AnimationUtils.loadAnimation(this, R.anim.rotate)
//        anima.duration = 1000
//        drawer.startAnimation(anima)

        navView.setNavigationItemSelectedListener(this)
        navView.setOnClickListener(this)

        setPointer()
        //tool bar section
//        navView.menu.findItem(R.id.drawer_title).title = userName
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_favourites -> { startActivity(Intent(this, FavouritesActivity::class.java)) }
            R.id.nav_register -> { startActivity(Intent(this, RegisterActivity::class.java)) }
            R.id.nav_login -> { startActivity(Intent(this, LoginActivity::class.java)) }
            R.id.nav_signout -> {
                logOutUser()
                Toast.makeText(this, item.title, Toast.LENGTH_LONG).show()
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setNumberPicker(): Int {
        //sets numPicker for amount of data to fetch

        var selectedValue = numPicker.value
//        numPicker.displayedValues = lettersAtoZ
//            numPicker.setFormatter( )

        numPicker.setOnValueChangedListener { numberPicker, oldVal, newVal ->
            selectedValue = newVal
        }

        return selectedValue
    }

    private fun setPointer() {
        var loggedUser = getLoggedUser()
        if (loggedUser != SP_DEFAULT_USER){
            getLoggedUserId()
            isLogged = true
            navView.menu.findItem(R.id.drawer_title).title = getString(R.string.welcome)+loggedUser
        } else {
            navView.menu.findItem(R.id.drawer_title).title = getString(R.string.welcome)+getString(R.string.guest)
        }
//        setNumberPicker()
        Toast.makeText(this, getString(R.string.marvel_data_credit), Toast.LENGTH_LONG).show()
        numPicker.minValue = 1
        numPicker.maxValue = 100
        numPicker.value = 10
//        numPicker.minValue = 0
//        numPicker.maxValue = lettersAtoZ.size-1
//        numPicker.displayedValues = lettersAtoZ
////            numPicker.setFormatter( )
//
//        numPicker.setOnValueChangedListener { numberPicker, oldVal, newVal ->
////            Toast.makeText(applicationContext, lettersAtoZ[newVal], Toast.LENGTH_SHORT).show()
//            MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=${lettersAtoZ[newVal]}&limit=${setNumberPicker()}&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
//            var myData = GetDataAsyncTask()
//            myData.execute(MARVEL_CHARACTERS)
//            charactersList.clear()
//        }

        fabMain.setOnClickListener {
            showAlertMenu()
//
//            var profileImage: ImageView? = findViewById(R.id.account_img)
//            var anim = AnimationUtils.loadAnimation(this, R.anim.rotate)
        }
    //region
//        for (index in 0 until locations.size){
//            var WURL = "https://api.darksky.net/forecast/bf818f22c95ed844f2ca1827f1380154/"
//            WURL+=locations[index]
//            var myData = GetDataAsyncTask()
//            myData.execute(WURL)
//        }

        //buttons onClick listeners
//        this.btnA.setOnClickListener(this)
//        this.btnB.setOnClickListener(this)
//        this.btnC.setOnClickListener(this)
//        this.btnD.setOnClickListener(this)
//        this.btnE.setOnClickListener(this)
//        this.btnF.setOnClickListener(this)
//        this.btnG.setOnClickListener(this)

//        for (id in R.id.btnA .. R.id.btnG){
////            var id = R.id.btnA
//            characterUrlRequest.add(id)
//            Log.e(" ID BTN ", ""+id)
//            findViewById<Button>(characterUrlRequest.last()).setOnClickListener(this)
//        }


/*  toolbar buttons handler
        btnA.setOnClickListener {
            MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=a&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"

            charactersList.clear()
            var myData = GetDataAsyncTask()
            myData.execute(MARVEL_CHARACTERS)
        }

        btnB.setOnClickListener {
            MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=b&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"

            charactersList.clear()
            var myData = GetDataAsyncTask()
            myData.execute(MARVEL_CHARACTERS)
        }

        btnC.setOnClickListener {
            MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=c&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
//            titles.clear()
//            images.clear()
//            detailImage.clear()
//            modifiedDate.clear()
//            details.clear()
            charactersList.clear()
            var myData = GetDataAsyncTask()
            myData.execute(MARVEL_CHARACTERS)
        }

        btnD.setOnClickListener {
            MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=d&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
            charactersList.clear()
            var myData = GetDataAsyncTask()
            myData.execute(MARVEL_CHARACTERS)
        }

        btnE.setOnClickListener {
            MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=e&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
            charactersList.clear()
            var myData = GetDataAsyncTask()
            myData.execute(MARVEL_CHARACTERS)
        }

        btnF.setOnClickListener {
            MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=f&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
            charactersList.clear()
            var myData = GetDataAsyncTask()
            myData.execute(MARVEL_CHARACTERS)
        }

        btnG.setOnClickListener {
            MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=g&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
            charactersList.clear()
            var myData = GetDataAsyncTask()
            myData.execute(MARVEL_CHARACTERS)
        }   */
//        setSupportActionBar(toolbar)
        //endregion

        collapsing_toolbar.title = "TITLE"
        collapsing_toolbar.setContentScrimColor(Color.TRANSPARENT)

        //using our recycler view
        layoutManger = LinearLayoutManager(this)
        recycler_view.layoutManager = layoutManger

        menuLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        letters_nav_list.layoutManager = menuLayoutManager

        letters_nav_list?.smoothScrollBy(1500, 0)
//        letters_nav_list.adapter = NavRecyclerAdapter(lettersAtoZ)

        // handle recycler item onClick
        letters_nav_list?.adapter =
            NavRecyclerAdapter(lettersAtoZ) {
                charactersList.clear()
                MARVEL_CHARACTERS =
                    "https://gateway.marvel.com/v1/public/characters?nameStartsWith=${lettersAtoZ[it]}&limit=${setNumberPicker()}&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
                val myData = GetDataAsyncTask()
                myData.execute(MARVEL_CHARACTERS)
                searchBar.requestFocus()
                SpinnerLoader(drawer,this).startSpinner()
            }
//        navView.menu.findItem(R.id.drawer_title).title = userName
        }

    override fun onClick(view: View) {
//        when (view.id){
//            R.id.btnA->{
//                MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=a&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
//            }
//            R.id.btnB->{
//                MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=b&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
//            }
//            R.id.btnC->{
//                MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=c&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
//            }
//            R.id.btnD->{
//                MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=d&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
//            }
//            R.id.btnE->{
//                MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=e&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
//            }
//            R.id.btnF->{
//                MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=f&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
//            }
//            R.id.btnG->{
//                MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=g&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
//            }
//            else->{Toast.makeText(this,"press a letter to get characters", Toast.LENGTH_SHORT).show()}
//        }
        //pass the selection from menuAdapter to api request
        MARVEL_CHARACTERS =
            "https://gateway.marvel.com/v1/public/characters?nameStartsWith=${"m"}&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
        //
        charactersList.clear()
        searchBar.requestFocus() // move to search barafter click
        val myData = GetDataAsyncTask()
        myData.execute(MARVEL_CHARACTERS)

    }


    // populate list
    private fun populateList(): ArrayList<Character> {

        val list = ArrayList<Character>()

        for (i in 0 until charactersList.size) {
            val character = Character(
                charactersList[i].getName(),
                charactersList[i].getDescription(),
                charactersList[i].getThumbnail(),
                charactersList[i].getDateModified(),
                charactersList[i].getDetailsURL(),
                charactersList[i].getWikiURL()
            )
            list.add(character)
        }

        return list
    }

    companion object {
        lateinit var charNamesArrayList: ArrayList<Character>
        lateinit var array_sort: ArrayList<Character>
    }

    // async data retrieval with url connection
    inner class GetDataAsyncTask : AsyncTask<String, String, String>() {
        val CONNECTION_TIMEOUT_MILLISECONDS = 60 * 1000
        // background connection for seamless feel to get input stream of string
        override fun doInBackground(vararg args: String?): String {
            lateinit var urlConnection: HttpsURLConnection
            var inString = ""
            try {
                var url = URL(args[0])

                urlConnection = url.openConnection() as HttpsURLConnection
                urlConnection.connectTimeout = CONNECTION_TIMEOUT_MILLISECONDS
                urlConnection.readTimeout = CONNECTION_TIMEOUT_MILLISECONDS

                //a function to handle our stream
                inString = streamToString(urlConnection.inputStream)

            } catch (e: Exception) {
                Log.e("error", "error in URL connection ! ${e.message}")
            } finally {
                urlConnection.disconnect()
            }
            return inString
        }

        // after doInBackground task finished, build the data and pass to adapter
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
//            getResultFromAPI(result)
            SpinnerLoader(drawer,this@MainActivity).stopSpinner()
            getAllResultsFromAPI(result)
//            recycler_view.adapter = RecyclerAdapter(titles,details,modifiedDate,images)
            recycler_view.adapter =
                RecyclerAdapter(charactersList) //this sets the list to adapter in order to display it
            adapter?.notifyDataSetChanged()

            charNamesArrayList = populateList()
            array_sort = ArrayList<Character>()
            array_sort = populateList()

            searchBar!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
//                    if (s.isEmpty()){return}
//                    MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=${s}&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
//                    charactersList.clear()
//                    var myData = GetDataAsyncTask()
//                    myData.execute(MARVEL_CHARACTERS)
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                    if(searchBar.text.toString().equals("")){return}
//
//                    Log.e("onTextChanged ", s.toString())
//                    searchNameStartsWith = searchBar!!.text.toString()
//                    Log.e("EditText ", searchNameStartsWith)
//                    MARVEL_ALL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=${searchNameStartsWith}&limit=50&ts=1&apikey=${API_KEY}&hash=${HASH_CODE}"
//                    var myData = GetDataAsyncTask()
//                    myData.execute(MARVEL_ALL_CHARACTERS)

                    // #####  this is beta for search request - only work after button request  ####
//                    MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=${s}&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
//                    charactersList.clear()
//                    var myData = GetDataAsyncTask()
//                    myData.execute(MARVEL_CHARACTERS)

                    filterFromResult()

                    recycler_view.adapter =
                        RecyclerAdapter(array_sort)
//                    recycler_view!!.adapter = adapter
                    adapter?.notifyDataSetChanged()
//                    recycler_view!!.layoutManager =
//                        LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

                }
            })
        }

        //filter the json result to specific text search
        private fun filterFromResult() {
            textLength = searchBar!!.text.length
            array_sort.clear()
            for (i in charNamesArrayList.indices) {
                if (textLength <= charNamesArrayList[i].getName().length) {
                    if (charNamesArrayList[i].getName().toLowerCase().trim().contains(
                            searchBar!!.text.toString().toLowerCase().trim { it <= ' ' })
                    ) {
                        array_sort.add(charNamesArrayList[i])
                    }
                }
            }
        }

        private fun getResultFromAPI(result: String?) {
            var json = JSONObject(result)

            val data = json.getJSONObject("data")
            var results = data.getJSONArray("results")
            var resultsInner = results.getJSONObject(0)
            var name = resultsInner.getString("name")
            var description = resultsInner.getString("description").toString()
            var modified = resultsInner.getString("modified").toString()
//            mock=summary
//            var sList: ArrayList<String> = ArrayList()

//            var myMap = mutableMapOf<String, String>()
//
//            cur.keys().forEach { keyStr ->
//                var value = cur.get(keyStr)
//                myMap.put()
//                sList.add()
//            }
            titles.add(name)
            details.add(description)
            modifiedDate.add(modified)
        }

        // after connection to our api's url we use the inputStream we have (String type) to create JSON object result
        private fun getAllResultsFromAPI(result: String?) {
            var json = JSONObject(result)

            val data = json.getJSONObject("data")
            val results = data.getJSONArray("results")

            for (item in 0 until results.length()) {

                var innerRes = results.getJSONObject(item)
                var name = innerRes.getString("name")
                var description = innerRes.getString("description").toString()
                var modified = innerRes.getString("modified").toString()
                var thumbnail = innerRes.getJSONObject("thumbnail").getString("path")

                var urlsDetail =
                    innerRes.getJSONArray("urls").getJSONObject(0).getString("type").toString()
                if (urlsDetail == "detail") {
                    urlsDetail =
                        innerRes.getJSONArray("urls").getJSONObject(0).getString("url").toString()
                    Log.e("inJSON_URL", urlsDetail)
                } else{urlsDetail = ""}
                var urlsWiki =
                    innerRes.getJSONArray("urls").getJSONObject(1).getString("type").toString()
                if (urlsWiki == "wiki") {
                    urlsWiki =
                        innerRes.getJSONArray("urls").getJSONObject(1).getString("url").toString()
                    Log.e("inJSON_URLWIKI", urlsWiki)
                } else {urlsWiki = ""}
//                var urlsDetail = innerRes.getJSONArray("urls").getJSONObject(0).getString("url")
//                var urlsWiki = innerRes.getJSONArray("urls").getJSONObject(1).getString("url")
                //
                val charachter =
                    Character(name, description, thumbnail, modified, urlsDetail, urlsWiki)
//                charachter.setName(name)
//                charachter.setDescription(description)
//                charachter.setThumbnail(thumbnail)
//                charachter.setDateModified(modified)
//                charachter.setDetailsURL(urlsDetail)
//                charachter.setWikiURL(urlsWiki)

/* arrayLists for each category of a character
                titles.add(name)
                details.add(description)
                modifiedDate.add(modified)
                images.add(thumbnail)
*/
                //object-based arrayList
                charactersList.add(charachter)
            }


        }

        private fun streamToString(inputStream: InputStream): String {
            val bufferReader = BufferedReader(InputStreamReader(inputStream))
            lateinit var line: String
            var result = StringBuilder()

            try {
                do {
                    line = bufferReader.readLine()
                    if (line != null) {
                        result.append(line)
                    }
                } while (!line.isNullOrEmpty())
            } catch (e: Exception) {
                Log.e("error", e.message)
            } finally {
                inputStream.close()
            }
            return result.toString()
        }
    }

    private fun showAlertMenu() {
        val builder = AlertDialog.Builder(this@MainActivity)

        var myView: View =
            LayoutInflater.from(this@MainActivity).inflate(R.layout.list_fab_main, null)
        var listView: ListView = myView.findViewById(R.id.lstFabMain)
        var infoTxt: TextView = myView.findViewById(R.id.information_txt)
        var attribText1: TextView = myView.findViewById(R.id.attrib_txt_1)
        var attribText2: TextView = myView.findViewById(R.id.attrib_txt_2)
        var attribText3: TextView = myView.findViewById(R.id.attrib_txt_3)
        var attributeLayout: LinearLayout = myView.findViewById(R.id.attribute_layout)

        var fabMenuItems = ArrayList<String>()
        fabMenuItems.add("Register")
        fabMenuItems.add("Login")
        fabMenuItems.add("Logout")
        fabMenuItems.add("Information")
        fabMenuItems.add("Share")

        listView.adapter = ListAdapter(this@MainActivity, fabMenuItems)
//        lstFabMain.adapter = ListAdapter(this@MainActivity, mockList)

        builder.setView(myView)
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        dialog.show()
            /* set margins for alert dialog */
//        val btn = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
//        val btnParams: ViewGroup.MarginLayoutParams = btn.layoutParams as ViewGroup.MarginLayoutParams
//        val viewParams: ViewGroup.MarginLayoutParams = myView.layoutParams as ViewGroup.MarginLayoutParams
//        viewParams.setMargins(150,800,0,0)
//        btnParams.marginEnd = 0

        //onItemClick to handle list item click
        listView.setOnItemClickListener { adapterView, view, i, l ->
            when (i) {
                0 -> {
                    dialog.dismiss()
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
                1 -> {
                    dialog.dismiss()
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                2 -> {
                    if (isLogged) {
                        Toast.makeText(this, userName + getString(R.string.sign_out), Toast.LENGTH_SHORT).show()
                        logOutUser()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, getString(R.string.no_user_logged), Toast.LENGTH_SHORT).show()
                    }
                }
                3 -> {
                    listView.visibility = View.GONE
                    attributeLayout.visibility = View.VISIBLE
                    attribText2.movementMethod = LinkMovementMethod.getInstance()
                }
                4 -> {
//                    val sendIntent: Intent = Intent().apply {
//                        action = Intent.ACTION_SEND
//                        putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
//                        type = "text/plain"
//                    }
//
//                    val shareIntent = Intent.createChooser(sendIntent, null)
//                    startActivity(shareIntent)

                    val whatsappIntent = Intent(Intent.ACTION_SEND)
                    whatsappIntent.type = "text/plain"
                    whatsappIntent.setPackage("com.whatsapp")
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, "TEXT TO SHARE")
                    try {
                        startActivity(whatsappIntent)
                    } catch (ex: ActivityNotFoundException) {
                        Toast.makeText(this, "Whatsapp have not been installed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menuItem = menu?.findItem(R.id.drawer_title)
        return super.onPrepareOptionsMenu(menu)
    }

    private class ListAdapter(context: Context, data: List<String>) : BaseAdapter() {
        var context: Context
        var data: List<String>

        init {
            this.context = context
            this.data = data
        }


        override fun getView(index: Int, view: View?, viewGroup: ViewGroup?): View {
//            val singleRow:TextView = TextView(context)
            val myView: View = LayoutInflater.from(context).inflate(R.layout.list_item_fab, null)
            val icon = myView.findViewById<ImageView>(R.id.newRowIcon)
            val rowText: TextView = myView.findViewById(R.id.newRow)
            rowText.text = data[index]

            icon.setImageResource(
                when (index) {
                    0 -> R.drawable.ic_register
                    1 -> R.drawable.ic_login
                    2 -> R.drawable.ic_signout
                    3 -> R.drawable.ic_information
                    else -> R.drawable.marvellogo
                }
            )
//            singleRow.textSize = 28F
//            singleRow.setTextColor(Color.BLACK)
//            singleRow.text = data[index]
//            return singleRow

            return myView
        }

        override fun getItem(index: Int): Any {
            return data[index]
        }

        override fun getItemId(index: Int): Long {
            return index.toLong()
        }

        override fun getCount(): Int {
            return data.size
        }

    }

    fun getLoggedUser(): String{
        sp = getSharedPreferences("logged_user", Context.MODE_PRIVATE)
        val user = sp.getString(LOGGED_USER_NAME_KEY, SP_DEFAULT_USER)
        userName = user!!
        return user
    }

    fun getLoggedUserId(): Int{
        sp = getSharedPreferences("logged_user", Context.MODE_PRIVATE)
        val id = sp.getInt(LOGGED_USER_ID_KEY, -1)
        userId = id

        return id
    }

    fun logOutUser(){
        sp.edit()
            .remove(LOGGED_USER_NAME_KEY)
            .remove(LOGGED_USER_ID_KEY)
            .apply()
        isLogged = false
        userName = getLoggedUser()
        navView.menu?.findItem(R.id.drawer_title)?.title = userName
    }

    fun hideKeyboard(){
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this@MainActivity.currentFocus.windowToken,0)
    }

    fun animateDrawerImage(){
        var profileImage: ImageView? = findViewById(R.id.account_img)
        var anim = AnimationUtils.loadAnimation(this@MainActivity, R.anim.rotate)

        profileImage?.startAnimation(anim)
    }

    override fun onDrawerStateChanged(newState: Int) {
        TODO("Not yet implemented")
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

    }

    override fun onDrawerClosed(drawerView: View) {
        TODO("Not yet implemented")
    }

    override fun onDrawerOpened(drawerView: View) {
        TODO("Not yet implemented")
    }


}
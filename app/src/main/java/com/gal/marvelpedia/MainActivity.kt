package com.gal.marvelpedia

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
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
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card_layout.view.*
import kotlinx.android.synthetic.main.list_fab_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity(), View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private var layoutManger: RecyclerView.LayoutManager? = null
    private var adapter:RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null
    lateinit var mock:String
    // api connection
    val CONNECTION_TIMEOUT_MILLISECONDS = 60 * 1000

    val API_KEY = "76bfded27255952b203b27148d1b71fd"
    val HASH_CODE = "bea98a0dcae226b8392394770170756a"

    var searchNameStartsWith = ""
    var MARVEL_ALL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=${searchNameStartsWith}&limit=50&ts=1&apikey=${API_KEY}&hash=${HASH_CODE}"
    var MARVEL_URL = "https://gateway.marvel.com/v1/public/characters?name=Spider-man&orderBy=name&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
    val locations = arrayOf("40.6974034,-73.8395988", "34.0201613,-118.1315522", "31.3867115,37.3255463", "51.089965,14.9387907", "52.7536099,2.1562505")
    var titles = ArrayList<String>()
    var details = ArrayList<String>()
    var modifiedDate = ArrayList<String>()
    var images = ArrayList<String>()
    var detailImage = ArrayList<String>()

    var characterUrlRequest = ArrayList<Int>()
    //array of Character obj
    var charactersList = ArrayList<Character>()

    var MARVEL_CHARACTERS:String = ""

    internal var textLength = 0

    val lettersAtoZ = arrayOf("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z")

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
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, 0, 0
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        navView.setOnClickListener(this)
        setPointer()
        //tool bar section

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_register -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_login -> { startActivity(Intent(this, LoginActivity::class.java)) }
            R.id.nav_signout -> { Toast.makeText(this, item.title, Toast.LENGTH_LONG).show() }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setPointer() {
        numPicker.minValue = 0
        numPicker.maxValue = lettersAtoZ.size-1
        numPicker.displayedValues = lettersAtoZ
//            numPicker.setFormatter( )

        numPicker.setOnValueChangedListener { numberPicker, oldVal, newVal ->
//            Toast.makeText(applicationContext, lettersAtoZ[newVal], Toast.LENGTH_SHORT).show()

            MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=${lettersAtoZ[newVal]}&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
            charactersList.clear()
            var myData = GetDataAsyncTask()
            myData.execute(MARVEL_CHARACTERS)
        }



        fabMain.setOnClickListener {
            showAlertMenu()
        }

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

        for (id in R.id.btnA .. R.id.btnG){
//            var id = R.id.btnA
            characterUrlRequest.add(id)
            Log.e(" ID BTN ", ""+id)
            findViewById<Button>(characterUrlRequest.last()).setOnClickListener(this)
        }

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

        collapsing_toolbar.title="TITLE"
        collapsing_toolbar.setContentScrimColor(Color.TRANSPARENT)
        //using our recycler view
        layoutManger = LinearLayoutManager(this)
        recycler_view.layoutManager = layoutManger
    }


    override fun onClick(view: View) {
        when (view.id){
            R.id.btnA->{
                MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=a&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
            }
            R.id.btnB->{
                MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=b&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
            }
            R.id.btnC->{
                MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=c&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
            }
            R.id.btnD->{
                MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=d&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
            }
            R.id.btnE->{
                MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=e&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
            }
            R.id.btnF->{
                MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=f&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
            }
            R.id.btnG->{
                MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=g&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
            }
            else->{Toast.makeText(this,"press a letter to get characters", Toast.LENGTH_SHORT).show()}
        }
        charactersList.clear()
        searchBar.requestFocus() // move to search barafter click
        var myData = GetDataAsyncTask()
        myData.execute(MARVEL_CHARACTERS)
    }


    // populate list
    private fun populateList(): ArrayList<Character> {

        val list = ArrayList<Character>()

        for (i in 0..charactersList!!.size-1) {
            var character = Character(charactersList!![i].getName(),charactersList!![i].getDescription(),charactersList!![i].getThumbnail(),charactersList!![i].getDateModified(), charactersList!![i].getDetailsURL(), charactersList!![i].getWikiURL())
            list.add(character)
        }

        return list
    }

    companion object {
        lateinit var charNamesArrayList: ArrayList<Character>
        lateinit var array_sort: ArrayList<Character>
    }

    // async data retrieval with url connection
    inner class GetDataAsyncTask:AsyncTask<String,String,String>() {
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
            getAllResultsFromAPI(result)
//            recycler_view.adapter = RecyclerAdapter(titles,details,modifiedDate,images)
            recycler_view.adapter = RecyclerAdapter(charactersList) //this sets the list to adapter in order to display it
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

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

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

                    // TODO this is beta for search request - only work after button request
//                    MARVEL_CHARACTERS = "https://gateway.marvel.com/v1/public/characters?nameStartsWith=${s}&limit=50&ts=1&apikey=76bfded27255952b203b27148d1b71fd&hash=bea98a0dcae226b8392394770170756a"
//                    charactersList.clear()
//                    var myData = GetDataAsyncTask()
//                    myData.execute(MARVEL_CHARACTERS)

                    filterFromResult()

                    recycler_view.adapter = RecyclerAdapter(array_sort)
//                    recycler_view!!.adapter = adapter
                    adapter?.notifyDataSetChanged()
//                    recycler_view!!.layoutManager =
//                        LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

                }
            })
        }
        //filter the json result to specific text search
        private fun filterFromResult(){
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
            var  resultsInner = results.getJSONObject(0)
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
        private fun getAllResultsFromAPI(result: String?){
            var json = JSONObject(result)

            val data = json.getJSONObject("data")
            val results = data.getJSONArray("results")

            for (item in 0 until results.length()){

                var innerRes = results.getJSONObject(item)
                var name = innerRes.getString("name")
                var description = innerRes.getString("description").toString()
                var modified = innerRes.getString("modified").toString()
                var thumbnail = innerRes.getJSONObject("thumbnail").getString("path")

                var urlsDetail = innerRes.getJSONArray("urls").getJSONObject(0).getString("type").toString()
                if (urlsDetail == "detail" ){
                    urlsDetail = innerRes.getJSONArray("urls").getJSONObject(0).getString("url").toString()
                    Log.e("inJSON_URL", urlsDetail)
                }
                var urlsWiki = innerRes.getJSONArray("urls").getJSONObject(1).getString("type").toString()
                if (urlsWiki == "wiki" ){
                    urlsWiki = innerRes.getJSONArray("urls").getJSONObject(1).getString("url").toString()
                    Log.e("inJSON_URLWIKI", urlsWiki)
                }
//                var urlsDetail = innerRes.getJSONArray("urls").getJSONObject(0).getString("url")
//                var urlsWiki = innerRes.getJSONArray("urls").getJSONObject(1).getString("url")
                //
                val charachter = Character(name, description, thumbnail, modified, urlsDetail, urlsWiki)
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
                } while (line != null)
            } catch (e: Exception) {
                Log.e("error", e.message)
            } finally {
                inputStream.close()
            }
            return result.toString()
        }
    }

    private fun showAlertMenu(){
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("")
        val myView: View = LayoutInflater.from(this@MainActivity).inflate(R.layout.list_fab_main, null)
        var listView: ListView = myView.findViewById(R.id.lstFabMain)

        var mockList = ArrayList<String>()
        mockList.add("Register")
        mockList.add("Login")
        mockList.add("Logout")
        mockList.add("Information")
        listView.adapter = ListAdapter(this@MainActivity, mockList)
//        lstFabMain.adapter = ListAdapter(this@MainActivity, mockList)

        builder.setView(myView)
        builder.setNegativeButton("Cancel"){dialog, _ ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()

        //onItemClick to handle list item click
        listView.setOnItemClickListener { adapterView, view, i, l ->
            when (i) {
                0->{ // register
                    val intent = Intent(this,RegisterActivity::class.java)
                    startActivity(intent)
                }
                1->{
                    // login
                }
                2->{

                }
            }
        }
    }

    private class ListAdapter(context: Context, data: List<String>): BaseAdapter(){
        var context:Context
        var data:List<String>

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
}
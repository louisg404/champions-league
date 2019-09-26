package com.bitbuildr.bicloo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.properties.Delegates

class OkHttpRequest(client: OkHttpClient) { // Small class to use okHttp quicker
    private var client = OkHttpClient()

    init {
        this.client = client
    }

    fun GET(url: String, callback: Callback): Call {
        val request = Request.Builder()
            .header("X-Auth-Token", "d7927f1e4cd0415ba278af835989341c")
            .url(url)
            .build()

        val call = client.newCall(request)
        call.enqueue(callback)
        return call
    }

    companion object {
        val JSON = MediaType.parse("application/json; charset=utf-8")
    }
}

fun JSONArray.toJSONObjectList(): List<JSONObject> { // Usage of extension
    var buffer = emptyList<JSONObject>()
    for(i in 0 until this.length()) {
        buffer += this.getJSONObject(i)
    }
    return buffer
}

class MainActivity : AppCompatActivity() {
    // enum usage
    enum class DisplayMode { NAMES, STANDS }

    // Delegate usage, we observe changes to mode
    var mode: DisplayMode by Delegates.observable(DisplayMode.NAMES) { _, _, _ ->
        refreshDisplay()
    }

    // Delegate usage, we observe changes to stations
    var matchesScheduled: List<Matche> by Delegates.observable(emptyList()) { _, _, _ ->
        refreshDisplay()
    }

    // Delegate usage, we observe changes to stations
    var matchesFinished: List<Matche> by Delegates.observable(emptyList()) { _, _, _ ->
        refreshDisplay()
    }

    // build http client
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggleButton.setOnCheckedChangeListener { _, b -> // Adding a action on button
            mode = if(b) DisplayMode.STANDS else DisplayMode.NAMES
        }

        // Send our request
        //refreshData("https://api.football-data.org/v2/competitions/CL/matches?status=SCHEDULED")
        refreshData("https://api.football-data.org/v2/competitions/CL/matches?status=FINISHED")
    }

    private fun refreshData(url : String) {
        OkHttpRequest(client).GET(url, object: Callback {
            override fun onResponse(call: Call?, response: Response) {
                val responseData = response.body()?.string()
                runOnUiThread { // Important, we want to refresh our data on main thread (crash otherwise)
                    try {
                        println("Request 1Successful!!")
                        val jsonO = JSONObject(responseData)
                        println(jsonO)
                        val json = jsonO.getJSONArray("matches")
                        println("Request 2Successful!!")
                        println(json)

                        // mapping from json to list of Stations
                        val matches = json.toJSONObjectList().map {
                            Matche(
                                it.getInt("id"),
                                it.getString("utcDate"),
                                it.getJSONObject("score").optString("winner"),
                                it.getJSONObject("score").getJSONObject("fullTime").getInt("homeTeam"),
                                it.getJSONObject("score").getJSONObject("fullTime").getInt("awayTeam"),
                                it.getJSONObject("homeTeam").getString("name"),
                                it.getJSONObject("awayTeam").getString("name")
                            )
                        }
                        println(matches)

                        this@MainActivity.matchesFinished = matches
                    } catch (e: JSONException) {
                        println("Request 2Failure.")
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                println("Request Failure.")
            }
        })
    }

    private fun refreshDisplay() {
        // Complexe usage of map and when combined to show that kotlin is fucking awesome
        val cellContentTexts = matchesFinished.map {
            when(mode) {
                DisplayMode.NAMES -> "${it.homeTeam} vs ${it.awayTeam}"
                DisplayMode.STANDS -> "${it.scoreHomeTeam} vs ${it.scoreAwayTeam}" // parameters in strings
            }
        }

        stationsListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cellContentTexts) // Adapter usage, we refresh the list with fresh data
    }
}

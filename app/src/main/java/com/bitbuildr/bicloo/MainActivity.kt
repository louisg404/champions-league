package com.bitbuildr.bicloo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
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
    var mode: DisplayMode by Delegates.observable(DisplayMode.NAMES) { property, old, new ->
        refreshDisplay()
    }

    // Delegate usage, we observe changes to stations
    var stations: List<Station> by Delegates.observable(emptyList()) { property, old, new ->
        refreshDisplay()
    }

    // build http client
    private val client = OkHttpClient()
    private val url = "https://api.jcdecaux.com/vls/v1/stations?contract=nantes&apiKey=cb6b72d2aca14d4c46b60bcd5d69e2c342b00a76"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggleButton.setOnCheckedChangeListener { _, b -> // Adding a action on button
            mode = if(b) DisplayMode.STANDS else DisplayMode.NAMES
        }

        // Send our request
        refreshData()
    }

    private fun refreshData() {
        OkHttpRequest(client).GET(url, object: Callback {
            override fun onResponse(call: Call?, response: Response) {
                val responseData = response.body()?.string()
                runOnUiThread { // Important, we want to refresh our data on main thread (crash otherwise)
                    try {
                        val json = JSONArray(responseData)
                        println("Request Successful!!")
                        println(json)

                        // mapping from json to list of Stations
                        val stations = json.toJSONObjectList().map {
                            Station(
                                it.getInt("number"),
                                it.getString("name"),
                                Station.StandsCount(
                                    it.getInt("available_bike_stands"),
                                    it.getInt("available_bikes"),
                                    it.getInt("bike_stands")
                                )
                            )
                        }
                        println(stations)

                        this@MainActivity.stations = stations
                    } catch (e: JSONException) {
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
        val cellContentTexts = stations.map {
            when(mode) {
                DisplayMode.NAMES -> it.name
                DisplayMode.STANDS -> "${it.stands.free} / ${it.stands.total}" // parameters in strings
            }
        }

        stationsListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cellContentTexts) // Adapter usage, we refresh the list with fresh data
    }
}

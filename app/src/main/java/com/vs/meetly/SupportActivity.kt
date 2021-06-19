package com.vs.meetly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.vs.meetly.adapters.SupportAdapter
import com.vs.meetly.modals.Support
import kotlinx.android.synthetic.main.activity_meeting_filter.topAppBar
import kotlinx.android.synthetic.main.activity_support.*

class SupportActivity : AppCompatActivity() {

    lateinit var adapter: SupportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

//        hideDefaultUI()

        supportProgressBar.visibility = View.VISIBLE

        fetchData()

        swipeRefresh.setOnRefreshListener {
            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            fetchData()
        }

        topAppBar.setNavigationOnClickListener {
            finish()
        }

    }

    private fun hideDefaultUI() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun fetchData() {
        val url = "https://meetlyapiv2.herokuapp.com/api/v2/"
        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { val newsJsonArray = it.getJSONArray("notifications")
                val supportArray = ArrayList<Support>()

                for (i in 0 until newsJsonArray.length()){
                    val newsJsonObject =  newsJsonArray.getJSONObject(i)
                    val news = Support(
                        newsJsonObject.getString("user_name"),
                        newsJsonObject.getString("user_avatar"),
                        newsJsonObject.getString("date"),
                        newsJsonObject.getString("content"),

                        )
                    supportArray.add(news)
                }

                //call recycle view
                supportProgressBar.visibility = View.GONE
                adapter = SupportAdapter(this, supportArray)
                SupportRecyclerView.layoutManager = LinearLayoutManager(this)
                SupportRecyclerView.adapter = adapter
                swipeRefresh.setRefreshing(false)

            },
            {
                Toast.makeText(this, "Something Went Wrong!!", Toast.LENGTH_LONG).show()
            })

// Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }
}
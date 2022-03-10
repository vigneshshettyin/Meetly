package com.vs.meetly

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.vs.meetly.adapters.TeamAdapter
import com.vs.meetly.adapters.itemclicked
import com.vs.meetly.retrofit.TeamAPI
import kotlinx.android.synthetic.main.activity_devs.*
import kotlinx.android.synthetic.main.activity_meeting_filter.topAppBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://githubcapi.herokuapp.com/"
class DevsActivity : AppCompatActivity() , itemclicked {
    lateinit var mAdapter:TeamAdapter
    lateinit var linearlayoutmanager:LinearLayoutManager
    lateinit var shimmerframelayout:ShimmerFrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devs)
        shimmerframelayout = findViewById(R.id.shimmer)
        shimmerframelayout.startShimmer()
        recyclerview.setHasFixedSize(true)
        linearlayoutmanager = LinearLayoutManager(this)
        recyclerview.layoutManager=linearlayoutmanager
        getMyData()
        topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun getMyData() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(TeamAPI::class.java)
        val retrofitData = retrofitBuilder.getData()
        retrofitData.enqueue(object : Callback<HashMap<String,Map<String,String>>?> {
            override fun onResponse(call: Call<HashMap<String,Map<String,String>>?>,
                                    response: Response<HashMap<String,Map<String,String>>?>) {
                shimmerframelayout.stopShimmer()
                shimmerframelayout.visibility= View.GONE
                recyclerview.visibility=View.VISIBLE
                val responseBody = response.body()!!
                val valuelist = ArrayList(responseBody.values)
                mAdapter= TeamAdapter(this@DevsActivity,baseContext, valuelist)
                mAdapter.notifyDataSetChanged()
                recyclerview.adapter=mAdapter
            }
            override fun onFailure(call: Call<HashMap<String,Map<String,String>>?>, t: Throwable) {
                Log.d("DevsActivity","onFAILURE"+t.message)
            }
        })
    }

    override fun onitemclicked(datamap: Map<String, String>) {
        val url = datamap.get("profile_url")
        val builder = CustomTabsIntent.Builder()
        val CustomTabsIntent = builder.build()
        CustomTabsIntent.launchUrl(this, Uri.parse(url))
    }
}

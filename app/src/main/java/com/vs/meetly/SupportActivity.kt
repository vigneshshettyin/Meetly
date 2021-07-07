package com.vs.meetly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.vs.meetly.adapters.SupportAdapter
import com.vs.meetly.modals.Support
import com.vs.meetly.modals.SupportAPI
import com.vs.meetly.retrofit.SupportAPIService
import kotlinx.android.synthetic.main.activity_meeting_filter.topAppBar
import kotlinx.android.synthetic.main.activity_support.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupportActivity : AppCompatActivity() {

    lateinit var adapter: SupportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        supportPreloader.visibility = View.VISIBLE

        getSupportResponses()

        swipeRefresh.setOnRefreshListener {
            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            getSupportResponses()
        }

        topAppBar.setNavigationOnClickListener {
            finish()
        }

    }

    private fun getSupportResponses() {
        val support = SupportAPIService.SupportAPIInstance.getAllUpdates()
        support.enqueue(object : Callback<SupportAPI> {
            override fun onResponse(call: Call<SupportAPI>, response: Response<SupportAPI>) {
                val supportGetResponse = response.body()
                if (supportGetResponse != null) {
                    setUpRecycleView(supportGetResponse!!.notifications as MutableList<Support>)
                }
            }

            override fun onFailure(call: Call<SupportAPI>, t: Throwable) {
                Toast.makeText(this@SupportActivity, "${t.toString()}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setUpRecycleView(supportArray: MutableList<Support>) {
        val revSupportArrayList: ArrayList<Support> =
            supportArray.reversed().toMutableList() as ArrayList<Support>;
        //call recycle view
        adapter = SupportAdapter(this, revSupportArrayList)
        SupportRecyclerView.layoutManager = LinearLayoutManager(this)
        SupportRecyclerView.adapter = adapter
        supportPreloader.visibility = View.GONE
        swipeRefresh.setRefreshing(false)
    }
}
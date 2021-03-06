package com.example.Capstone.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Capstone.R
import com.example.Capstone.adapter.FolderRecyclerViewAdapter
import com.example.Capstone.adapter.StorageRecyclerViewAdapter
import com.example.Capstone.db.SharedPreferenceController
import com.example.Capstone.model.Storage
import com.example.Capstone.network.ApplicationController
import com.example.Capstone.network.NetworkService
import com.example.Capstone.network.data.FolderData
import com.example.Capstone.network.get.GetAllStorageListResponse
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_storage_manage.*
import kotlinx.android.synthetic.main.toolbar_with_onlyback.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StorageManageActivity : AppCompatActivity() {

    private val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    lateinit var storageRecyclerViewAdapter : StorageRecyclerViewAdapter
    private var storageList : ArrayList<Storage> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage_manage)

        btn_back.setOnClickListener {
            finish()
        }

        btn_add_share_storage.setOnClickListener {
            val intent = Intent(this, AddStorageActivity::class.java)
            startActivity(intent)
        }

        txt_my_storage.text = SharedPreferenceController.getUserNickname(this) + "의 저장소"

        storageRecyclerViewAdapter = StorageRecyclerViewAdapter(this, storageList)
        rv_storage_container.adapter = storageRecyclerViewAdapter
        rv_storage_container.layoutManager = LinearLayoutManager(this)

        getAllStorageListResponse(SharedPreferenceController.getUserId(this)!!)

    }

    private fun getAllStorageListResponse(id: Int){
        val getAllStorageListResponse = networkService.getAllStorageListResponse("application/json", id)

        getAllStorageListResponse.enqueue(object : Callback<ArrayList<GetAllStorageListResponse>> {

            override fun onFailure(call: Call<ArrayList<GetAllStorageListResponse>>, t: Throwable) {
                Log.e("get List failed", t.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<GetAllStorageListResponse>>,
                response: Response<ArrayList<GetAllStorageListResponse>>
            ) {
                if(response.isSuccessful){
                    storageList.clear()
                    Log.d("babo", response.body().toString())
                    val data: ArrayList<GetAllStorageListResponse>? = response.body()
                    if (data != null) {
                        for(storage in data) {
                            storageList.add(Storage(storage.id, storage.sharing_name))
                        }
                    }
                    storageRecyclerViewAdapter.notifyDataSetChanged()
                }
                else{
                    Log.e("error", "fail")
                }
            }
        })
    }

}

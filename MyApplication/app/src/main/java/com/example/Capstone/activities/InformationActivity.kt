package com.example.Capstone.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.Capstone.R
import kotlinx.android.synthetic.main.activity_information.*
import kotlinx.android.synthetic.main.toolbar_with_trashbin.*

class InformationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        Glide.with(this).load("http://blogfiles.naver.net/MjAyMDAxMTJfMjc3/MDAxNTc4ODMxNzA4MDA0.bMTZ6laF3F-YGvE7l5_B_" +
                "LI8O0a6GhTL3B0h94KPzxcg.Oi46FpElgjlXTKAUpXe5ued8iSZ9pspER7eyZb6zeScg.JPEG.cherrysomu/01.jpg").into(info_img)


        btn_back.setOnClickListener {
            finish()
        }


    }
}

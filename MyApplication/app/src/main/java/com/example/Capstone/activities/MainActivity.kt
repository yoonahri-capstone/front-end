package com.example.Capstone.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GravityCompat
import androidx.viewpager.widget.ViewPager
import com.example.Capstone.R
import com.example.Capstone.adapter.MainFragmentAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_activity_main.*
import kotlinx.android.synthetic.main.nav_drawer.*
import org.jetbrains.anko.inputMethodManager
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity(){

    lateinit var pager : ViewPager
    lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pager = findViewById(R.id.vp_main)
        val pagerAdapter = MainFragmentAdapter(supportFragmentManager)
        pager.adapter = pagerAdapter

        val tabLayout : TabLayout = findViewById(R.id.tl_main)

        tabLayout.addTab(tabLayout!!.newTab().setIcon(R.drawable.ic_feed))
        tabLayout.addTab(tabLayout!!.newTab().setIcon(R.drawable.ic_album))

        pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        btn_hamburger.setOnClickListener {
            if (!ly_drawer.isDrawerOpen(GravityCompat.END)) {
                ly_drawer.openDrawer(GravityCompat.END)
            }
//            if ((SharedPreferenceController.getUserImg(this))!!.isNotEmpty()) {
//                Glide.with(this@MainActivity)
//                    .load(SharedPreferenceController.getUserImg(this))
//                    .apply(RequestOptions.circleCropTransform())?.into(iv_drawer_profileimg)
//            } else {
//                Glide.with(this@MainActivity)
//                    .load(R.drawable.profile)
//                    .apply(RequestOptions.circleCropTransform())?.into(iv_drawer_profileimg)
//            }
        }

        switch_noti.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                switch_noti.setTrackResource(R.drawable.switch_track_on)
            }
            else{
                switch_noti.setTrackResource(R.drawable.switch_track_off)
            }
        }

        btn_search.setOnClickListener {
            pagerAdapter.getEditText(search_item.text)
        }

        btn_personal_storage.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            ly_drawer.closeDrawer(GravityCompat.END)
            startActivity(intent)
        }

        btn_share_storage.setOnClickListener {
            val intent = Intent(this, StorageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            ly_drawer.closeDrawer(GravityCompat.END)
            startActivity(intent)
        }

        btn_manage_folder.setOnClickListener {
            val intent = Intent(this, FolderManageActivity::class.java)
            ly_drawer.closeDrawer(GravityCompat.END)
            startActivity(intent)
        }

        btn_manage_storage.setOnClickListener {
            val intent = Intent(this, StorageManageActivity::class.java)
            ly_drawer.closeDrawer(GravityCompat.END)
            startActivity(intent)
        }

        btn_cancel.setOnClickListener {
            if (ly_drawer.isDrawerOpen(GravityCompat.END)) {
                ly_drawer.closeDrawer(GravityCompat.END)
            }
        }

        var spinnerList = arrayOf("전체", "맛집", "화장품", "꿀팁")

        val spinner : Spinner = findViewById(R.id.spinner_menu)

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinnerList)
        spinner.adapter = spinnerAdapter

        //myNotification()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun myNotification(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("memmem", name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }


        var builder = NotificationCompat.Builder(this, "memmem")
            .setSmallIcon(R.drawable.ic_noti)
            .setContentTitle("memmem notification")
            .setContentText("Much longer text that cannot fit one line...")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("오늘 저녁식사 메뉴를 정하지 못했다면 ?."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }
    }

}

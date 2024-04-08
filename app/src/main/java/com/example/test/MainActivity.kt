package com.example.test

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.test.adapter.TabAdapter
import com.example.test.globalContexts.Constants
import com.google.android.material.tabs.TabLayout
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabAdapter: TabAdapter

    private fun sendTokenToServer(generatedToken: String, queue: RequestQueue) {
        Log.d("FCM", "SENDING TO SERVER");

        val sharedPreferences = this.getSharedPreferences("Booky", Context.MODE_PRIVATE)
        Constants.USER_FCM = sharedPreferences.getString("USER_FCM", "").toString()
        Constants.USER_ID = sharedPreferences.getInt("USER_ID", -1)
        Constants.USER_NAME = sharedPreferences.getString("USER_NAME", "").toString()
        if (generatedToken == Constants.USER_FCM) {
            Log.d("FCM", "UPLOAD NOT REQUIRED")
            return
        }
        val url = "${Constants.BASE_URL}/v0/users/token/${Constants.USER_ID}/${generatedToken}"

        Log.d("SENDING TO SERVER", Constants.BASE_URL)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val sharedPreferences = this.getSharedPreferences("Booky", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                val userId = response.getInt("userId")
                val userName = response.getString("userName")
                val fcmToken = response.getString("fcmToken")

                editor.putInt("USER_ID", userId)
                editor.putString("USER_NAME", userName)
                editor.putString("USER_FCM", fcmToken)
                Constants.USER_ID = userId
                Constants.USER_NAME = userName
                Constants.USER_FCM = fcmToken
            },
            { error ->
                Log.e("FCM UPLOAD ERROR", error.toString())
                Log.e("FCM UPLOAD ERROR", "Error: $error")
            }
        )
        queue.add(jsonObjectRequest)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("FCM", "STARTING")
        var token: String
        val queue = Volley.newRequestQueue(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                token = task.result
                Log.d("FCM ABC", token)
                sendTokenToServer(token, queue);
            } else {
                token = "Token not generated"
                Log.d("FCM Error: ", token)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(this.getResources().getColor(R.color.zolo_bg_main));
        }


        tabLayout = findViewById(R.id.tabLayout)
        viewPager2 = findViewById(R.id.viewPager2)


        tabAdapter= TabAdapter( supportFragmentManager,lifecycle)


        tabLayout.addTab(tabLayout.newTab().setText("Borrowed"))
        tabLayout.addTab(tabLayout.newTab().setText("Your Books"))


        viewPager2.adapter = tabAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }
}
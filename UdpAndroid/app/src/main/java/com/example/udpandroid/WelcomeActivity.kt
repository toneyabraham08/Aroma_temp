package com.example.udpandroid

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.udpandroid.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    var centerImg: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        getSupportActionBar()?.hide()

        centerImg = findViewById(R.id.imageView_top_icon)

        findViewById<Button>(R.id.button_signin).setOnClickListener {
            //finish()
            startActivity(Intent(this,SignInActivity::class.java),ActivityOptions
                .makeSceneTransitionAnimation(this, centerImg, "splash_image")
                .toBundle())
        }

        findViewById<TextView>(R.id.textview_sign).setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java),ActivityOptions
                .makeSceneTransitionAnimation(this, centerImg, "splash_image")
                .toBundle())
        }
    }

}
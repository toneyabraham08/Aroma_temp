package com.example.udpandroid

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.udpandroid.db.AppDatabase
import com.example.udpandroid.db.UserDaoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInActivity : AppCompatActivity() {
    var centerImg: ImageView? = null
    var userEmail:EditText? = null
    var userPass:EditText? = null
    var email = ""
    var pass = ""
    var ndb: AppDatabase? = null
    var userDao: UserDaoDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("sharedPrefFile",
            Context.MODE_PRIVATE)


        ndb = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "devices_data"
        ).build()
        userDao = ndb!!.userDaoDao()

        centerImg = findViewById(R.id.imageView2)
        userEmail = findViewById(R.id.user_email_et)
        userPass = findViewById(R.id.user_password_et)
        findViewById<View>(R.id.imageView_back).setOnClickListener { this@SignInActivity.finish() }

        findViewById<Button>(R.id.button_signup).setOnClickListener {


                GlobalScope.launch {
                    var userData = userDao!!.findByEmail(userEmail?.text.toString())
                    withContext(Dispatchers.Main){
                        if(userData!=null) {
                            if (userData.email.equals(userEmail?.text.toString())) {
                                if (userData.password.equals(userPass?.text.toString())) {
                                    val editor:SharedPreferences.Editor =  sharedPreferences.edit()
                                    editor.putBoolean("user_logged",true)
                                    editor.putInt("currentuserid",userData.uid)
                                    editor.putString("user_name",userData.username)
                                    editor.putString("user_password",userData.password)
                                    editor.putString("user_mobile",userData.mobileNumber)
                                    editor.apply()
                                    editor.commit()
                                    startActivity(Intent(baseContext, PropertiesActivity::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(baseContext, "Password incorrect", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(baseContext, "Email incorrect", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(baseContext, "Not registered", Toast.LENGTH_SHORT).show()
                        }
                    }
                }





        }
    }
}
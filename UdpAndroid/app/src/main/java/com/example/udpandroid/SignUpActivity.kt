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
import com.example.udpandroid.db.UserData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {
    var centerImg: ImageView? = null
    var userName: EditText? = null
    var mobileet: EditText? = null
    var passet: EditText? = null
    var emailet: EditText? = null
    var ndb: AppDatabase? = null
    var userDao: UserDaoDao? = null
    var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        ndb = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "devices_data"
        ).build()
        userDao = ndb!!.userDaoDao()

        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            "sharedPrefFile",
            Context.MODE_PRIVATE
        )

        centerImg = findViewById(R.id.imageView2)
        userName = findViewById(R.id.userName)
        mobileet = findViewById(R.id.usermobile)
        passet = findViewById(R.id.userpass)
        emailet = findViewById(R.id.useremail)

        findViewById<View>(R.id.imageView_back).setOnClickListener { this@SignUpActivity.finish() }


        findViewById<Button>(R.id.button_cancel).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.button_signup).setOnClickListener {
            var userModel = UserData()
            userModel.email = emailet?.text.toString()
            userModel.username = userName?.text.toString()
            userModel.password = passet?.text.toString()
            userModel.mobileNumber = mobileet?.text.toString()

            val UserMap = hashMapOf(
                "name" to userModel.username,
                "email" to userModel.email,
                "mobile" to userModel.mobileNumber
            )


            if (userModel.email.isEmpty() || userModel.username.isEmpty() || userModel.password.isEmpty() || userModel.mobileNumber.isEmpty()) {
                // Show an alert indicating that the data can't be empty
                Toast.makeText(baseContext, "Please Fill in the fields", Toast.LENGTH_SHORT).show()
            } else {
                GlobalScope.launch {
                    userDao!!.insertAll(userModel)
                    var userData = userDao!!.findByEmail(userModel?.email)
                    withContext(Dispatchers.Main) {
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("user_email", userModel.email)
                        editor.putString("user_password", userModel.password)
                        editor.putString("user_mobile", userModel.mobileNumber)
                        editor.putString("user_name", userModel.username)
                        editor.putBoolean("user_logged", true)
                        editor.putInt("currentuserid", userData.uid)
                        editor.apply()
                        editor.commit()
                        db.collection("user").document(userModel.mobileNumber).set(UserMap)
                            .addOnSuccessListener {
//                                Toast.makeText(this,"Logged In Successfully",Toast.LENGTH_SHORT).show()
                                System.out.println("Successfully Logged In")
                            }
                        Toast.makeText(this@SignUpActivity,"Logged In Successfully",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(baseContext, PropertiesActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}

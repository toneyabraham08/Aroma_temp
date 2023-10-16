package com.example.udpandroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.udpandroid.databinding.ActivityAddPropBinding

class AddPropActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAddPropBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_prop)
        var busave  = findViewById<Button>(R.id.button_save)

        findViewById<View>(R.id.imageView_back).setOnClickListener { this@AddPropActivity.finish() }


        busave.setOnClickListener { view ->
            var inte = Intent()
            var edt = findViewById<EditText>(R.id.group_name)
            //var b = Bundle()
            //b.putString("PropName",edt.text.toString())
            inte.putExtra("PropName",edt.text.toString())
            setResult(23,inte)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_add_prop)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
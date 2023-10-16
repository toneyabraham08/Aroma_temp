package com.example.udpandroid

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.udpandroid.db.AppDatabase
import com.example.udpandroid.db.DeviceData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DeviceManage : AppCompatActivity() {

    var model: DeviceData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_device_man)

        findViewById<View>(R.id.imageView_back).setOnClickListener { this@DeviceManage.finish() }


        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "devices_data"
        ).build()
        if (intent.extras != null) {
            val b = intent.extras
            model = b!!.getSerializable("deviceData") as DeviceData?
            if (model != null) {
                var button = findViewById<Button>(R.id.button_schedule_device)


                var deviceName = findViewById<EditText>(R.id.group_name)
                var deviceTv = findViewById<TextView>(R.id.group_name_tv)
                //var nameDev:String = model!!.device_name
                deviceTv.setText(model!!.device_name)
                deviceTv.setOnClickListener {
                    deviceName.visibility = View.VISIBLE
                    deviceTv.visibility = View.GONE
                }
                deviceName.setText(model!!.device_name)

                deviceName.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        model!!.device_name = deviceName.getText().toString()
                        deviceName.visibility = View.GONE
                        deviceTv.visibility = View.VISIBLE
                        deviceTv.text = deviceName.getText().toString()
                        GlobalScope.launch {
                            val available: DeviceData = db!!.deviceDao().findById(model!!.unique_id)
                            if (available != null) {
                                db!!.deviceDao().updateDevice(model)
                            }
                        }
                        return@OnEditorActionListener true
                    }
                    false
                })

                button.setOnClickListener {
                    val i = Intent(this@DeviceManage, DetailsActivity::class.java)
                    val b = Bundle()
                    b.putSerializable("deviceData", model)
                    i.putExtras(b)
                    startActivity(i)
                }
            }
        }



    }


    var db: AppDatabase? = null
}
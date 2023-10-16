package com.example.udpandroid

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import com.example.udpandroid.db.AppDatabase
import com.example.udpandroid.db.PropertyDaoDao
import com.example.udpandroid.db.PropertyData
import com.example.udpandroid.db.UserDaoDao
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis


class PropertiesActivity : AppCompatActivity() {

    var colors = intArrayOf(
        Color.parseColor("#ffd54f"),
        Color.parseColor("#ffca28"),
        Color.parseColor("#ffc107"),
        Color.parseColor("#ffb300"),
        Color.parseColor("#ffa000"),
        Color.parseColor("#ff8f00"),
        Color.parseColor("#ff6f00"),
        Color.parseColor("#c43e00")
    )
    var props:List<PropertyData>? = null

    var flipper: ViewFlipper? = null
    var prev_Button: Button? = null
    var next_Button:Button? = null
    var recyclerView:RecyclerView? = null
    var adapter:CustomAdapter? = null
    var fabb:LinearLayout? = null
    var ndb: AppDatabase? = null
    var propDao:PropertyDaoDao? = null
    var userDao:UserDaoDao? = null
    var sharedPreferences:SharedPreferences? = null
    var currentUserId = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_properties)

        sharedPreferences = this.getSharedPreferences("sharedPrefFile",
            Context.MODE_PRIVATE)

        currentUserId = this.sharedPreferences!!.getInt("currentuserid",-1)

        ndb = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "devices_data"
        ).build()
        propDao = ndb!!.propertyDao()
        userDao = ndb!!.userDaoDao()

        findViewById<View>(R.id.imageView_back).setOnClickListener {
            this@PropertiesActivity.finish()
        }

        findViewById<TextView>(R.id.textview_welcome).setText("Welcome "+this.sharedPreferences!!.getString("user_name","Name"))


        recyclerView = findViewById<View>(
            R.id.recyclerView
        ) as RecyclerView

        fabb = findViewById<LinearLayout>(R.id.button_add_props)
        fabb?.setOnClickListener {
            resultLauncher.launch(Intent(this,AddPropActivity::class.java))
            //showdialog()
        }
        var logout = findViewById<ImageView>(R.id.imageView_right_logout)
        logout?.setOnClickListener {
            val editor:SharedPreferences.Editor =  this.sharedPreferences!!.edit()
            editor.putBoolean("user_logged",false)
            editor.apply()
            editor.commit()
            finish()
            startActivity(Intent(this,SignInActivity::class.java))
        }

        getAllProps()
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data: Intent? = result.data
        if (result.resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!
            val propertyData2 = PropertyData()
            propertyData2.property_name = m_Text
            propertyData2.property_image = uri?.path
            propertyData2.current_user = currentUserId
            insertProps(propertyData2)
        } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
            val propertyData2 = PropertyData()
            propertyData2.property_name = m_Text
            propertyData2.property_image = ""
            propertyData2.current_user = currentUserId
            //insertProps(propertyData2)
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else if (result.resultCode == 23) {
            if (data != null && data.hasExtra("PropName")) {
                m_Text = data.getStringExtra("PropName")!!
                //showdialog()
                val propertyData2 = PropertyData()
                propertyData2.property_name = m_Text
                propertyData2.property_image = ""
                propertyData2.current_user = currentUserId
                insertProps(propertyData2)
            }
        } else {
            val propertyData2 = PropertyData()
            propertyData2.property_name = m_Text
            propertyData2.property_image = ""
            propertyData2.current_user = currentUserId
            insertProps(propertyData2)
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAdapterProps() {
        adapter = props?.let { CustomAdapter(it) }
        recyclerView!!.adapter = adapter
        recyclerView!!.layoutManager =
            LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, true)
    }

    private fun insertProps(prop:PropertyData) {
        Toast.makeText(this, "ujser "+prop.current_user, Toast.LENGTH_SHORT).show()
        GlobalScope.launch {
            propDao!!.insertAll(prop)
            props = propDao!!.getAllCuser(currentUserId)
            withContext(Dispatchers.Main){
                setAdapterProps()
            }
        }
    }

    private fun getAllProps() {
        GlobalScope.launch {
            props = propDao!!.getAllCuser(currentUserId)
            withContext(Dispatchers.Main){
                setAdapterProps()
            }
        }
    }

    class CustomAdapter(private val dataSet: List<PropertyData>) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView
            val imageView: ImageView

            init {
                textView = view.findViewById(R.id.textView)
                imageView = view.findViewById(R.id.image_view_item)
            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.text_row_item, viewGroup, false)

            return ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            viewHolder.textView.text = dataSet[position].property_name
            viewHolder.textView.setOnClickListener {
                viewHolder.textView.context.startActivity(Intent(viewHolder.textView.context,SenderActivity::class.java))
            }
            if(dataSet[position].property_image.length>0) {
                viewHolder.imageView.setImageURI(Uri.parse(dataSet[position].property_image))
            } else {
                viewHolder.imageView.setImageResource(R.drawable.icon_home)
            }
            viewHolder.imageView.setOnClickListener {
                var bun = Bundle()
                bun.putSerializable("selectedProp",dataSet[position])
                var inten = Intent(viewHolder.textView.context,SenderActivity::class.java)
                inten.putExtras(bun)
                viewHolder.textView.context.startActivity(inten)
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size

    }

    var m_Text = ""

    fun showdialog(){
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Add Image?")

// Set up the buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                ImagePicker.with(this)
                    .crop()                    //Crop image(Optional), Check Customization for more option
                    .compress(1024)            //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080,
                        1080
                    )    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start()
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.cancel()
            val propertyData2 = PropertyData()
            propertyData2.property_name = m_Text
            propertyData2.property_image = ""
            propertyData2.current_user = currentUserId
            insertProps(propertyData2)
        })

        builder.show()
    }


    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!
            val propertyData2 = PropertyData()
            propertyData2.property_name = m_Text
            propertyData2.property_image = uri?.path
            insertProps(propertyData2)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            val propertyData2 = PropertyData()
            propertyData2.property_name = m_Text
            propertyData2.property_image = ""
            //insertProps(propertyData2)
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else if (resultCode == 23) {
            val propertyData2 = PropertyData()
            if (data != null && data.hasExtra("PropName")) {
                m_Text = data.getStringExtra("PropName")!!
                propertyData2.property_name = m_Text
                propertyData2.property_image = ""
                insertProps(propertyData2)
            }
        } else {
            val propertyData2 = PropertyData()
            propertyData2.property_name = m_Text
            propertyData2.property_image = ""
            insertProps(propertyData2)
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
*/

}
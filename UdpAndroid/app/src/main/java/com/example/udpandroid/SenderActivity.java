package com.example.udpandroid;

//import static kotlinx.coroutines.CoroutineScopeKt.CoroutineScope;

//import android.app.Activity;

import android.app.AlertDialog;
//import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
//import android.net.DhcpInfo;
//import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
//import android.text.Spannable;
//import android.text.SpannableString;
//import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
//import android.widget.Toast;

//import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.udpandroid.db.AppDatabase;
import com.example.udpandroid.db.DeviceData;
import com.example.udpandroid.db.PropertyData;
//import com.google.gson.Gson;

//import org.apache.commons.io.IOUtils;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
//import java.net.UnknownHostException;
//import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

//import kotlinx.coroutines.GlobalScope;

public class SenderActivity extends AppCompatActivity {
    //    InetAddress receiverAddress;
    DatagramSocket datagramSocket;

    ArrayList<DeviceModel> deviceList;
    RecyclerView recycler_view;
//    TcpClient mTcpClient;

    String password = "";
//    String wifiName = "";

//    private LinearLayout msgList;

    private ServerSocket serverSocket;
    //    private Socket tempClientSocket;
    Thread serverThread = null;
    public static final int SERVER_PORT = 8787;
    private Handler handler;
    //    private int greenColor = Color.BLACK;
    CourseAdapter courseAdapter;
    //    Toolbar toolbar;
    List<DeviceData> dbData;
    AppDatabase db;
    PropertyData prop;
    List<String> uniqueIds;

    private class GetDataAll extends AsyncTask<String, String, String> {

        DeviceData obj;

        public GetDataAll(DeviceData ob) {
            obj = ob;
        }

        @Override
        protected String doInBackground(String... strings) {
            if (strings[0].equals("add")) {
                DeviceData available = db.deviceDao().findById(obj.unique_id);
                if (available != null) {
                    obj.device_name = available.device_name;
                    db.deviceDao().delete(available);
                    db.deviceDao().insertAll(obj);
                } else {
                    db.deviceDao().insertAll(obj);
                }
            }
            dbData = db.deviceDao().getAll();
            return "Done";
        }

        @Override
        protected void onPostExecute(String bitmap) {
            super.onPostExecute(bitmap);
            //Toast.makeText(getBaseContext(), bitmap, Toast.LENGTH_LONG).show();
            courseAdapter = new CourseAdapter(SenderActivity.this, dbData, new OnItemClickListener() {
                @Override
                public void onItemClick(DeviceData item) {
                    Intent i = new Intent(SenderActivity.this, DeviceManage.class);
                    Bundle b = new Bundle();
                    b.putSerializable("deviceData", item);
                    i.putExtras(b);
                    startActivity(i);
                }
            }, uniqueIds);
            recycler_view.setAdapter(courseAdapter);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DeviceData item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sender_activity);
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "devices_data").build();
        uniqueIds = new ArrayList<String>();


        handler = new Handler();
        findViewById(R.id.imageView_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SenderActivity.this.finish();
            }
        });

        recycler_view = findViewById(R.id.recycler_view);

        this.serverThread = new Thread(new ServerThread());
        SenderActivity.this.serverThread.start();

        deviceList = new ArrayList<DeviceModel>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler_view.setLayoutManager(linearLayoutManager);

        Bundle bund = getIntent().getExtras();
        if (bund != null) {
            prop = (PropertyData) bund.getSerializable("selectedProp");
            if (bund != null) {
                TextView welcome = findViewById(R.id.textview_welcome);
                welcome.setText("Welcome " + prop.property_name);
            }
        }


        findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTaskExample asyncTask = new AsyncTaskExample();
                asyncTask.execute("Hello rajeev");
            }
        });

        findViewById(R.id.button_add_device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SenderActivity.this);
                alertDialog.setTitle("Wifi Details");
                alertDialog.setMessage("Enter Wifi router name and Password");

                final EditText input = new EditText(SenderActivity.this);
                final EditText inputWifi = new EditText(SenderActivity.this);
                final LinearLayout lin = new LinearLayout(SenderActivity.this);
                lin.setOrientation(LinearLayout.VERTICAL);
                inputWifi.setHint("Router Name");
                input.setHint("Password");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                inputWifi.setLayoutParams(lp);
                lin.addView(inputWifi);
                lin.addView(input);
                alertDialog.setView(lin);
                alertDialog.setIcon(android.R.drawable.btn_plus);

                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                password = input.getText().toString();
                                String wifiR = inputWifi.getText().toString();

                                if ((!wifiR.equals("")) && (!password.equals(""))) {
                                    // Create an instance of AsyncTask and execute it
                                    UDPSenderTask senderTask = new UDPSenderTask();
                                    senderTask.execute("Rou" + wifiR + "#Pwd" + password);
                                }

//                                if (password.compareTo("") == 0) {
//
//                                    AsyncTaskExample asyncTask = new AsyncTaskExample();
//                                    asyncTask.execute("RouPwd-"+wifiR+"*#*"+password);
//                                        Toast.makeText(getApplicationContext(),
//                                                "Details Sent", Toast.LENGTH_SHORT).show();
//
//                                }
                            }
                        });

                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        });

        AsyncTaskExample asyncTask = new AsyncTaskExample();
        asyncTask.execute("");

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            datagramSocket = new DatagramSocket(8787);
        } catch (IOException e) {
            e.printStackTrace();
            //showMessage("Error Starting Server : " + e.getMessage(), Color.RED);
        }
        GetDataAll asyncTask = new GetDataAll(null);
        asyncTask.execute("");
    }

    private void doOperation(String data) {
        try {
            datagramSocket = new DatagramSocket(8787);
        } catch (SocketException e1) {
            e1.printStackTrace();
        }

        byte[] buffer = "Hello rajeev".getBytes();
//        byte[] buffer = data.getBytes();

        DatagramPacket packet = null;
        try {
            packet = new DatagramPacket(
                    buffer, buffer.length, new Util().getBroadcastAddress(getBaseContext()), 8787);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            datagramSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class AsyncTaskExample extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            doOperation(strings[0]);
            return "Done";
        }

        @Override
        protected void onPostExecute(String bitmap) {
            super.onPostExecute(bitmap);
            //Toast.makeText(getBaseContext(), bitmap, Toast.LENGTH_LONG).show();
        }
    }

//    public void showMessage(final String message, String ip) {
//        //Log.d("rajeev",message);
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                    if(message.length()>29) {
//                        StringBuilder id = new StringBuilder();
//                        for (int ij = 4; ij < 12; ij++) {
//                            //hex.append(Integer.toHexString(message.charAt(ij)));
//                            Integer i = Integer.parseInt(Integer.toHexString(message.charAt(ij)), 16);
//                            System.out.println("" + i);
//                            id.append(i);
//                        }
//                        System.out.println("" + id.toString());
//
//                        StringBuilder liquid = new StringBuilder();
//                        Integer i = Integer.parseInt(Integer.toHexString(message.charAt(12)), 16);
//                        liquid.append(i);
//
//                        DeviceData ob = new DeviceData();
//                        ob.ip = ip;
//                        ob.liquid_level = Integer.parseInt(liquid.toString());
//                        ob.unique_id = id.toString();
//                        uniqueIds.add(id.toString());
//                        ob.zone1_start = Integer.parseInt(Integer.toHexString(message.charAt(13)), 16);
//                        ob.zone1_start_m = Integer.parseInt(Integer.toHexString(message.charAt(14)), 16);
//                        ob.zone1_end = Integer.parseInt(Integer.toHexString(message.charAt(15)), 16);
//                        ob.zone1_end_m = Integer.parseInt(Integer.toHexString(message.charAt(16)), 16);
//                        ob.zone2_start = Integer.parseInt(Integer.toHexString(message.charAt(17)), 16);
//                        ob.zone2_start_m = Integer.parseInt(Integer.toHexString(message.charAt(18)), 16);
//                        ob.zone2_end = Integer.parseInt(Integer.toHexString(message.charAt(19)), 16);
//                        ob.zone2_end_m = Integer.parseInt(Integer.toHexString(message.charAt(20)), 16);
//                        ob.zone3_start = Integer.parseInt(Integer.toHexString(message.charAt(21)), 16);
//                        ob.zone3_start_m = Integer.parseInt(Integer.toHexString(message.charAt(22)), 16);
//                        ob.zone3_end = Integer.parseInt(Integer.toHexString(message.charAt(23)), 16);
//                        ob.zone3_end_m = Integer.parseInt(Integer.toHexString(message.charAt(24)), 16);
//                        ob.zone4_start = Integer.parseInt(Integer.toHexString(message.charAt(25)), 16);
//                        ob.zone4_start_m = Integer.parseInt(Integer.toHexString(message.charAt(26)), 16);
//                        ob.zone4_end = Integer.parseInt(Integer.toHexString(message.charAt(27)), 16);
//                        ob.zone4_end_m = Integer.parseInt(Integer.toHexString(message.charAt(28)), 16);
//                        ob.intensity_level = Integer.parseInt(Integer.toHexString(message.charAt(29)), 16);
//
//                        GetDataAll asyncTask = new GetDataAll(ob);
//                        asyncTask.execute("add");
//                    } else {
//                        Log.d("rajeev","length not sufficient");
//                    }
//
//            }
//        });
//    }

    class ServerThread implements Runnable {

        public void run() {
            Log.d("rajeev", "server thread method called");
            Socket socket;
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
                //showMessage("Error Starting Server : " + e.getMessage(), Color.RED);
            }
            if (null != serverSocket) {
                Log.d("rajeev", "serverSocket not null method called");
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        socket = serverSocket.accept();
                        String ip = "";
                        String dummy = "";
                        String uid = "";
                        String liquid = "";
                        String time_Zone = "";
                        String intensity_level = "";
                        String status_switch = "";
                        ip = ip + socket.getInputStream().read();
                        ip = ip + "." + socket.getInputStream().read();
                        ip = ip + "." + socket.getInputStream().read();
                        ip = ip + "." + socket.getInputStream().read();
                        System.out.println("Ip values from the packet");
                        System.out.println(ip);

                        for (int i = 0; i < 4; i++) {
                            dummy = dummy + socket.getInputStream().read();
                        }
                        System.out.println("dummy values from the packet");
                        System.out.println(dummy);

                        for (int i = 0; i < 8; i++) {
                            uid = uid + socket.getInputStream().read();
                        }
                        System.out.println("UID values from the packet");
                        System.out.println(uid);

                        liquid = liquid + socket.getInputStream().read();

                        System.out.println("liquid value from the packet");
                        System.out.println(liquid);

                        for (int i = 0; i < 16; i++) {
                            time_Zone = time_Zone + "." + socket.getInputStream().read();
                        }
                        System.out.println("time_Zone value from the packet");
                        System.out.println(time_Zone);

                        intensity_level = intensity_level + socket.getInputStream().read();
                        System.out.println("intensity_level value from the packet");
                        System.out.println(intensity_level);

                        status_switch = status_switch + socket.getInputStream().read();
                        System.out.println("status_switch value from the packet");
                        System.out.println(status_switch);

                        String[] ipb = time_Zone.split("\\.");
                        if ((ip.length() + uid.length() + time_Zone.length() > 25)) {
                            System.out.println("ipb value from the packet");
                            System.out.println(ipb[1]);
                            DeviceData ob = new DeviceData();
                            ob.ip = ip;
                            ob.liquid_level = Integer.parseInt(liquid);
                            ob.intensity_level = Integer.parseInt(intensity_level);
                            ob.status_switch = Boolean.valueOf(status_switch);
                            ob.unique_id = uid;
                            ob.zone1_start = Integer.parseInt(ipb[1]);
                            ob.zone1_start_m = Integer.parseInt(ipb[2]);
                            ob.zone1_end = Integer.parseInt(ipb[3]);
                            ob.zone1_end_m = Integer.parseInt(ipb[4]);
                            ob.zone2_start = Integer.parseInt(ipb[5]);
                            ob.zone2_start_m = Integer.parseInt(ipb[6]);
                            ob.zone2_end = Integer.parseInt(ipb[7]);
                            ob.zone2_end_m = Integer.parseInt(ipb[8]);
                            ob.zone3_start = Integer.parseInt(ipb[9]);
                            ob.zone3_start_m = Integer.parseInt(ipb[10]);
                            ob.zone3_end = Integer.parseInt(ipb[11]);
                            ob.zone3_end_m = Integer.parseInt(ipb[12]);
                            ob.zone4_start = Integer.parseInt(ipb[13]);
                            ob.zone4_start_m = Integer.parseInt(ipb[14]);
                            ob.zone4_end = Integer.parseInt(ipb[15]);
                            ob.zone4_end_m = Integer.parseInt(ipb[16]);


                            GetDataAll asyncTask = new GetDataAll(ob);
                            asyncTask.execute("add");
                        } else {
                            Log.d("rajeev", "length not sufficient");
                        }

//                        CommunicationThread commThread = new CommunicationThread(socket,ip);
//                        new Thread(commThread).start();
                    } catch (IOException e) {
//                        e.printStackTrace();
                        //showMessage("Error Communicating to Client :" + e.getMessage(), Color.RED);
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            datagramSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    class CommunicationThread implements Runnable {
//
//        private Socket clientSocket;
//
//        private BufferedReader input;
//        private String ip;
//
//        public CommunicationThread(Socket clientSocket,String ip) {
//            Log.d("rajeev","CommunicationThread method called");
//            this.clientSocket = clientSocket;
//            this.ip = ip;
//            tempClientSocket = clientSocket;
//            try {
//                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
//            } catch (IOException e) {
//                e.printStackTrace();
//                //showMessage("Error Connecting to Client!!", Color.RED);
//            }
//            //showMessage("Connected to Client!!", greenColor);
//        }
//
//        public void run() {
//
//            Log.d("rajeev","CommunicationThread method run");
//            while (!Thread.currentThread().isInterrupted()) {
//                try {
//                    String read = input.readLine();
//                    System.out.println("Data Recived from the module");
//                    System.out.println(read);
//
//                    if (null == read || "Disconnect".contentEquals(read)) {
//                        Thread.interrupted();
//                        read = "Client Disconnected";
//                        //showMessage("Client : " + read, greenColor);
//                        break;
//                    }
//                    showMessage(read, ip);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            datagramSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != serverThread) {
            serverThread.interrupt();
            serverThread = null;
        }
    }

    public class UDPSenderTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                // Define the UDP server address and port
                InetAddress serverAddress = InetAddress.getByName("192.168.4.1"); // Replace with your server's IP
                int serverPort = 8787; // Replace with your server's port

                // Create a DatagramSocket
                try (DatagramSocket socket = new DatagramSocket()) {
                    // Message to be sent
                    String message = (params != null && params.length > 0) ? params[0] : "";
                    byte[] sendData = message.getBytes();

                    // Create a DatagramPacket
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);

                    // Send the packet
                    socket.send(sendPacket);

                    System.out.println("Message sent: " + message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

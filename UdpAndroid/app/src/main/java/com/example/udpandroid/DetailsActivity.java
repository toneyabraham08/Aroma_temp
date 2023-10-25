package com.example.udpandroid;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.udpandroid.db.AppDatabase;
import com.example.udpandroid.db.DeviceData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.time.LocalTime;

public class DetailsActivity extends AppCompatActivity {

    Button z1, z2, z3, z4, z1_t, z2_t, z3_t, z4_t, save;
    ImageView liquidLevel;
    TextView details;
    DeviceData model;
    DatagramSocket datagramSocket;
    AppDatabase db;
    SeekBar seekIntensity;

    Switch statusSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        db = Room.databaseBuilder(this,
                AppDatabase.class, "devices_data").build();
        z1 = findViewById(R.id.button_z1_from);
        z2 = findViewById(R.id.button_z2_from);
        z3 = findViewById(R.id.button_z3_from);
        z4 = findViewById(R.id.button_z4_from);
        z1_t = findViewById(R.id.button_z1_to);
        z2_t = findViewById(R.id.button_z2_to);
        z3_t = findViewById(R.id.button_z3_to);
        z4_t = findViewById(R.id.button_z4_to);
        seekIntensity = findViewById(R.id.seekBar_intencity);
        statusSwitch = findViewById(R.id.status_switch);
//        statusSwitch.setChecked(model.status_switch);


        findViewById(R.id.imageView_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailsActivity.this.finish();
            }
        });

        save = findViewById(R.id.button_save_details);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_request(true);
            }
        });

        save.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                send_request(false);
                return true;
            }
        });


        details = findViewById(R.id.device_details);
        liquidLevel = findViewById(R.id.imageView_liquid_level);
        if (getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();
            model = (DeviceData) b.getSerializable("deviceData");
            if (model != null) {
                setDeviceDetails();
                details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailsActivity.this);
                        alertDialog.setTitle("Device Name");
                        alertDialog.setMessage("Enter device name");

                        final EditText input = new EditText(DetailsActivity.this);
                        final LinearLayout lin = new LinearLayout(DetailsActivity.this);
                        lin.setOrientation(LinearLayout.VERTICAL);
                        input.setHint("Device Name");
                        if (model.device_name != null && model.device_name.length() > 0) {
                            input.setText(model.device_name);
                        }
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        input.setLayoutParams(lp);
                        lin.addView(input);
                        alertDialog.setView(lin);
                        alertDialog.setIcon(android.R.drawable.btn_plus);

                        alertDialog.setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String devName = input.getText().toString();
                                        model.device_name = devName;
                                        UpdateData upd = new UpdateData(model);
                                        upd.execute("");
                                        setDeviceDetails();
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

                if (model.liquid_level == 1) {
                    liquidLevel.setImageResource(R.drawable.liquid_empty_2);
                } else if (model.liquid_level == 3) {
                    liquidLevel.setImageResource(R.drawable.liquid_full2);
                } else {
                    liquidLevel.setImageResource(R.drawable.liquid_low_2);
                }
            }
        }
        z1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(z1, model.zone1_start, model.zone1_start_m);
            }
        });
        z2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(z2, model.zone2_start, model.zone2_start_m);
            }
        });
        z3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(z3, model.zone3_start, model.zone3_start_m);
            }
        });
        z4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(z4, model.zone4_start, model.zone4_start_m);
            }
        });

        z1_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(z1_t, model.zone1_end, model.zone1_end_m);
            }
        });
        z2_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(z2_t, model.zone2_end, model.zone2_end_m);
            }
        });
        z3_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(z3_t, model.zone3_end, model.zone3_end_m);
            }
        });
        z4_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(z4_t, model.zone4_end, model.zone4_end_m);
            }
        });
    }

    private void setDeviceDetails() {
        if (model.device_name != null && model.device_name.length() > 0) {
            details.setText(model.device_name + " - " + model.ip);
        } else {
            details.setText("No Name - " + model.ip);
        }
        setButtonText(z1, model.zone1_start, model.zone1_start_m);
        setButtonText(z1_t, model.zone1_end, model.zone1_end_m);
        setButtonText(z2, model.zone2_start, model.zone2_start_m);
        setButtonText(z2_t, model.zone2_end, model.zone2_end_m);
        setButtonText(z3, model.zone3_start, model.zone3_start_m);
        setButtonText(z3_t, model.zone3_end, model.zone3_end_m);
        setButtonText(z4, model.zone4_start, model.zone4_start_m);
        setButtonText(z4_t, model.zone4_end, model.zone4_end_m);
        seekIntensity.setProgress(model.intensity_level);
        statusSwitch.setChecked(model.status_switch);
        seekIntensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                model.intensity_level = progress;

            }
        });

        statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("SwitchListener", "Switch state changed: " + isChecked);
                model.status_switch = isChecked;
                System.out.println(model.status_switch);
//                statusSwitch.setChecked(isChecked);
            }
        });

//        String deviceId = model.unique_id;
//        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.udpandroid", Context.MODE_PRIVATE);
//        final SharedPreferences.Editor editor = sharedPreferences.edit();
////        statusSwitch.setChecked(sharedPreferences.getBoolean(deviceId, model.status_switch));
//
//        statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                editor.putBoolean(deviceId, b);
//                editor.apply();
//                model.status_switch = b;
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        statusSwitch.setChecked(model.status_switch);
    }


    public void send_request(boolean isTcp) {
        AsyncTaskExample asyncTask = new AsyncTaskExample();
        asyncTask.execute(isTcp);
    }

    private void doOperationTcp() throws IOException {
        Log.d("rajeev", "ip finding ");
//        String datatoSend = "";
        String[] ipb = model.ip.split("\\.");
        Log.d("rajeev", "ip " + ipb.length);

        byte dummyByte = 0;
        byte[] bytes = new byte[37];
        int[] digits = new int[model.unique_id.length()];

        for (int i = 0; i <= ipb.length + model.unique_id.length(); i++) {
            if (i < ipb.length) {
                bytes[i] = (byte) Integer.parseInt(ipb[i]);
            } else if (i < ipb.length + 4) {
                bytes[i] = (byte) dummyByte;
            } else {
                for (int j = 0; j < model.unique_id.length(); j++) {
                    digits[j] = Integer.parseInt(model.unique_id.substring(j, j + 1));
                    bytes[i] = (byte) digits[j];
                    i++;
                }
            }
        }
        bytes[16] = (byte) model.liquid_level;
        bytes[17] = (byte) model.zone1_start;
        bytes[18] = (byte) model.zone1_start_m;
        bytes[19] = (byte) model.zone1_end;
        bytes[20] = (byte) model.zone1_end_m;
        bytes[21] = (byte) model.zone2_start;
        bytes[22] = (byte) model.zone2_start_m;
        bytes[23] = (byte) model.zone2_end;
        bytes[24] = (byte) model.zone2_end_m;
        bytes[25] = (byte) model.zone3_start;
        bytes[26] = (byte) model.zone3_start_m;
        bytes[27] = (byte) model.zone3_end;
        bytes[28] = (byte) model.zone3_end_m;
        bytes[29] = (byte) model.zone4_start;
        bytes[30] = (byte) model.zone4_start_m;
        bytes[31] = (byte) model.zone4_end;
        bytes[32] = (byte) model.zone4_end_m;
//        Changing intensity level to bottom for match with the recieving data from the Hardware
        bytes[33] = (byte) model.intensity_level;

        byte byteValue = (byte) (model.status_switch ? 1 : 0);
        bytes[34] = (byte) byteValue;

        LocalTime currenttime = LocalTime.now();
        int hour = currenttime.getHour();
        int minute = currenttime.getMinute();
        bytes[35] = (byte) hour;
        bytes[36] = (byte) minute;

        byte[] buffer = bytes;
        Socket socket = new Socket(model.ip, 8786);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(buffer);
        outputStream.flush();
        outputStream.close();
        socket.close();
        System.out.println("Data sent successfully.");
    }


    private void doOperation() throws IOException {
        /*Socket socket = new Socket(model.ip, 8786);
        OutputStream out = socket.getOutputStream();
        PrintWriter output = new PrintWriter(out);
        output.println("Hello from Android");
        out.flush();
        out.close();
        socket.close();*/

        try {
            datagramSocket = new DatagramSocket(8787);
        } catch (SocketException e1) {
            Log.e("testing", "socket exception creating socket" + e1.getMessage());
            e1.printStackTrace();
        }
//        String datatoSend = "";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String[] ipb = model.ip.split("\\.");
        Log.d("rajeev", "ip " + ipb.length);
        byte dummyByte = 0;
        byte[] bytes = new byte[37];
        int[] digits = new int[model.unique_id.length()];

        for (int i = 0; i <= ipb.length + model.unique_id.length(); i++) {
            if (i < ipb.length) {
                bytes[i] = (byte) Integer.parseInt(ipb[i]);
            } else if (i < ipb.length + 4) {
                bytes[i] = (byte) dummyByte;
            } else {
                for (int j = 0; j < model.unique_id.length(); j++) {
                    digits[j] = Integer.parseInt(model.unique_id.substring(j, j + 1));
                    bytes[i] = (byte) digits[j];
                    i++;
                }
            }
        }
        bytes[16] = (byte) model.liquid_level;
        bytes[17] = (byte) model.zone1_start;
        bytes[18] = (byte) model.zone1_start_m;
        bytes[19] = (byte) model.zone1_end;
        bytes[20] = (byte) model.zone1_end_m;
        bytes[21] = (byte) model.zone2_start;
        bytes[22] = (byte) model.zone2_start_m;
        bytes[23] = (byte) model.zone2_end;
        bytes[24] = (byte) model.zone2_end_m;
        bytes[25] = (byte) model.zone3_start;
        bytes[26] = (byte) model.zone3_start_m;
        bytes[27] = (byte) model.zone3_end;
        bytes[28] = (byte) model.zone3_end_m;
        bytes[29] = (byte) model.zone4_start;
        bytes[30] = (byte) model.zone4_start_m;
        bytes[31] = (byte) model.zone4_end;
        bytes[32] = (byte) model.zone4_end_m;
//        Changing intensity level to bottom for match with the recieving data from the Hardware
        bytes[33] = (byte) model.intensity_level;

        byte byteValue = (byte) (model.status_switch ? 1 : 0);
        bytes[34] = (byte) byteValue;

        LocalTime currenttime = LocalTime.now();
        int hour = currenttime.getHour();
        int minute = currenttime.getMinute();
        bytes[35] = (byte) hour;
        bytes[36] = (byte) minute;

        byte[] buffer = bytes;

        DatagramPacket packet = null;
        try {
            packet = new DatagramPacket(
                    buffer, buffer.length, new Util().getBroadcastAddress(getBaseContext()), 8787);
        } catch (IOException e) {
            Log.e("testing", "packet exception creating packet" + e.getMessage());
            e.printStackTrace();
        }

        try {
            datagramSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("testing", "socket send exception sending to socket" + e.getMessage());
        }
    }

    private class AsyncTaskExample extends AsyncTask<Boolean, String, String> {

        @Override
        protected String doInBackground(Boolean... isTcp) {
            try {
                Log.d("rajeev", "isTcp " + isTcp[0]);
                if (!isTcp[0]) {
                    doOperation();
                } else {
                    doOperationTcp();
                }
                return "Done";
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("testing", "operation exception " + e.getMessage());
                Log.d("rajeev", "isTcp " + isTcp[0]);
                return e.getLocalizedMessage();
            }
        }

        @Override
        protected void onPostExecute(String bitmap) {
            super.onPostExecute(bitmap);
            try {
                Toast.makeText(DetailsActivity.this, bitmap, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            datagramSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int mHour = 0;
    int mMinute = 0;

    public void showTimePicker(Button buto, int hours, int min) {
        mHour = hours;
        mMinute = min;

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        switch (buto.getId()) {
                            case R.id.button_z1_from:
                                if (hourOfDay < model.zone2_start) {
                                    if (hourOfDay <= model.zone1_end) {
                                        if (hourOfDay == model.zone1_end) {
                                            if (minute < model.zone1_end_m) {
                                                model.zone1_start = hourOfDay;
                                                model.zone1_start_m = minute;
                                                setButtonText(buto, model.zone1_start, model.zone1_start_m);
                                            } else {
                                                Toast.makeText(getBaseContext(), "Time cannot be greater than end time", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            model.zone1_start = hourOfDay;
                                            model.zone1_start_m = minute;
                                            setButtonText(buto, model.zone1_start, model.zone1_start_m);
                                        }
                                    } else {
                                        Toast.makeText(getBaseContext(), "Time cannot be greater than end time", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), "Time cannot be greater next zone", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case R.id.button_z1_to:
                                if (hourOfDay < model.zone2_start) {
                                    if (hourOfDay >= model.zone1_start) {
                                        if (hourOfDay == model.zone1_start) {
                                            if (minute > model.zone1_start_m) {
                                                model.zone1_end = hourOfDay;
                                                model.zone1_end_m = minute;
                                                setButtonText(buto, model.zone1_end, model.zone1_end_m);
                                            } else {
                                                Toast.makeText(getBaseContext(), "Time cannot be less than start time", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            model.zone1_end = hourOfDay;
                                            model.zone1_end_m = minute;
                                            setButtonText(buto, model.zone1_end, model.zone1_end_m);
                                        }
                                    } else {
                                        Toast.makeText(getBaseContext(), "Time cannot be less than start time", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), "Time cannot be greater next zone", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case R.id.button_z2_from:
                                if (hourOfDay < model.zone3_start) {
                                    if (hourOfDay <= model.zone2_end) {
                                        if (hourOfDay == model.zone2_end) {
                                            if (minute < model.zone2_end_m) {
                                                model.zone2_start = hourOfDay;
                                                model.zone2_start_m = minute;
                                                setButtonText(buto, model.zone2_start, model.zone2_start_m);
                                            } else {
                                                Toast.makeText(getBaseContext(), "Time cannot be greater than end time", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            model.zone2_start = hourOfDay;
                                            model.zone2_start_m = minute;
                                            setButtonText(buto, model.zone2_start, model.zone2_start_m);
                                        }
                                    } else {
                                        Toast.makeText(getBaseContext(), "Time cannot be greater than end time", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), "Time cannot be greater next zone", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case R.id.button_z2_to:
                                if (hourOfDay < model.zone3_start) {
                                    if (hourOfDay >= model.zone2_start) {
                                        if (hourOfDay == model.zone2_start) {
                                            if (minute > model.zone2_start_m) {
                                                model.zone2_end = hourOfDay;
                                                model.zone2_end_m = minute;
                                                setButtonText(buto, model.zone2_end, model.zone2_end_m);
                                            } else {
                                                Toast.makeText(getBaseContext(), "Time cannot be less than start time", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            model.zone2_end = hourOfDay;
                                            model.zone2_end_m = minute;
                                            setButtonText(buto, model.zone2_end, model.zone2_end_m);
                                        }
                                    } else {
                                        Toast.makeText(getBaseContext(), "Time cannot be less than start time", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), "Time cannot be greater next zone", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case R.id.button_z3_from:
                                if (hourOfDay < model.zone4_start) {
                                    if (hourOfDay <= model.zone3_end) {
                                        if (hourOfDay == model.zone3_end) {
                                            if (minute < model.zone3_end_m) {
                                                model.zone3_start = hourOfDay;
                                                model.zone3_start_m = minute;
                                                setButtonText(buto, model.zone3_start, model.zone3_start_m);
                                            } else {
                                                Toast.makeText(getBaseContext(), "Time cannot be greater than end time", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            model.zone3_start = hourOfDay;
                                            model.zone3_start_m = minute;
                                            setButtonText(buto, model.zone3_start, model.zone3_start_m);
                                        }
                                    } else {
                                        Toast.makeText(getBaseContext(), "Time cannot be greater than end time", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), "Time cannot be greater next zone", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case R.id.button_z3_to:
                                if (hourOfDay < model.zone4_start) {
                                    if (hourOfDay >= model.zone3_start) {
                                        if (hourOfDay == model.zone3_start) {
                                            if (minute > model.zone3_start_m) {
                                                model.zone3_end = hourOfDay;
                                                model.zone3_end_m = minute;
                                                setButtonText(buto, model.zone3_end, model.zone3_end_m);
                                            } else {
                                                Toast.makeText(getBaseContext(), "Time cannot be less than start time", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            model.zone3_end = hourOfDay;
                                            model.zone3_end_m = minute;
                                            setButtonText(buto, model.zone3_end, model.zone3_end_m);
                                        }
                                    } else {
                                        Toast.makeText(getBaseContext(), "Time cannot be less than start time", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), "Time cannot be greater next zone", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case R.id.button_z4_from:
                                if (hourOfDay > model.zone3_start) {
                                    if (hourOfDay <= model.zone4_end) {
                                        if (hourOfDay == model.zone4_end) {
                                            if (minute < model.zone4_end_m) {
                                                model.zone4_start = hourOfDay;
                                                model.zone4_start_m = minute;
                                                setButtonText(buto, model.zone4_start, model.zone4_start_m);
                                            } else {
                                                Toast.makeText(getBaseContext(), "Time cannot be greater than end time", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            model.zone4_start = hourOfDay;
                                            model.zone4_start_m = minute;
                                            setButtonText(buto, model.zone4_start, model.zone4_start_m);
                                        }
                                    } else {
                                        Toast.makeText(getBaseContext(), "Time cannot be greater than end time", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), "Time cannot be less than previous zone", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case R.id.button_z4_to:
                                if (hourOfDay > model.zone3_start) {
                                    if (hourOfDay >= model.zone4_start) {
                                        if (hourOfDay == model.zone4_start) {
                                            if (minute > model.zone4_start_m) {
                                                model.zone4_end = hourOfDay;
                                                model.zone4_end_m = minute;
                                                setButtonText(buto, model.zone4_end, model.zone4_end_m);
                                            } else {
                                                Toast.makeText(getBaseContext(), "Time cannot be less than start time", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            model.zone4_end = hourOfDay;
                                            model.zone4_end_m = minute;
                                            setButtonText(buto, model.zone4_end, model.zone4_end_m);
                                        }
                                    } else {
                                        Toast.makeText(getBaseContext(), "Time cannot be less than start time", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), "Time cannot be less than previous zone", Toast.LENGTH_SHORT).show();
                                }
                                break;


                            default:
                                break;
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void setButtonText(Button buto, int model, int model1) {
        String txt = "";
        if (model <= 0) {
            model = 5;
        }
        if (model < 10) {
            txt = txt + "0";
        }
        txt = txt + model + ":";
        if (model1 < 10) {
            txt = txt + "0";
        }
        txt = txt + model1;
        buto.setText(txt);
    }


    private class UpdateData extends AsyncTask<String, String, String> {

        DeviceData obj;

        public UpdateData(DeviceData ob) {
            obj = ob;
        }

        @Override
        protected String doInBackground(String... strings) {
            DeviceData available = db.deviceDao().findById(obj.unique_id);
            if (available != null) {
                db.deviceDao().updateDevice(obj);
            }
            return "Done";
        }

        @Override
        protected void onPostExecute(String bitmap) {
            super.onPostExecute(bitmap);
            Toast.makeText(getBaseContext(), bitmap, Toast.LENGTH_LONG).show();
        }
    }

}
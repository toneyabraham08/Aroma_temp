package com.example.udpandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.udpandroid.db.DeviceData;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.Viewholder> {

    private Context context;
    private List<DeviceData> courseModelArrayList;
    private final SenderActivity.OnItemClickListener listener;
    private List<String> uniqueIds;

    DeviceData model;

    // Constructor
    public CourseAdapter(Context context, List<DeviceData> courseModelArrayList, SenderActivity.OnItemClickListener listener, List<String> uniqueIds) {
        this.context = context;
        this.courseModelArrayList = courseModelArrayList;
        this.listener = listener;
        this.uniqueIds = uniqueIds;
    }

    @NonNull
    @Override
    public CourseAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        DeviceData model = courseModelArrayList.get(position);
        if (model.device_name != null && model.device_name.length() > 0) {
            holder.courseRatingTV.setText(model.device_name);
        } else {
            holder.courseRatingTV.setText("No Name");
        }
        holder.courseNameTV.setText("");
        if (uniqueIds.contains(model.unique_id)) {
            holder.courseRatingTV.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(android.R.drawable.star_on), null, null, null);
        } else {
            holder.courseRatingTV.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(android.R.drawable.star_off), null, null, null);
        }
        holder.courseIV.setImageResource(R.drawable.aroma_dev_2);
        if (model.liquid_level == 1) {
            holder.liquidLevel.setImageResource(R.drawable.liquid_empty_2);
        } else if (model.liquid_level == 3) {
            holder.liquidLevel.setImageResource(R.drawable.liquid_full2);
        } else {
            holder.liquidLevel.setImageResource(R.drawable.liquid_low_2);
        }
//		holder.statusSwitch.setChecked(true);

        String deviceId = model.unique_id;

        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.udpandroid", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        holder.statusSwitch.setChecked(sharedPreferences.getBoolean(deviceId, model.status_switch));

        holder.statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean(deviceId, b);
                editor.apply();
                model.status_switch = b;

                if (b) {
                    holder.courseRatingTV.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(android.R.drawable.star_on), null, null, null);
                } else {
                    holder.courseRatingTV.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(android.R.drawable.star_off), null, null, null);
                }
            }
        });

//        if (model.status_switch) {
//            editor.putBoolean(deviceId, true);
//            editor.apply();
//        } else {
//            editor.putBoolean(deviceId, false);
//            editor.apply();
//        }
//
        holder.courseRatingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(model);
            }
        });
        holder.courseNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(model);
            }
        });
        holder.courseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(model);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return courseModelArrayList.size();
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView courseIV, liquidLevel;
        private TextView courseNameTV, courseRatingTV;
        Switch statusSwitch;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            courseIV = itemView.findViewById(R.id.idIVDeviceImage);
            courseNameTV = itemView.findViewById(R.id.device_name);
            courseRatingTV = itemView.findViewById(R.id.device_details);
            statusSwitch = itemView.findViewById(R.id.status_switch);
            liquidLevel = itemView.findViewById(R.id.device_liquid_level);
        }
    }
}

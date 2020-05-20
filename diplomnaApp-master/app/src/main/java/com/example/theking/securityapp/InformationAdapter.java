package com.example.theking.securityapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.ViewHolder> {
    private List<String> data;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private List<String> latitude;
    private List<String> longitude;
    private List<String> IMU;
    private List<String> shock;
    private List<String> date;
    private List<String> rfid;

    // data is passed into the constructor
    InformationAdapter(Context context, List<String> id, List<String> latitude, List<String> longitude,
                       List<String> IMU, List<String> shock, List<String> date, List<String> rfid) {
        this.mInflater = LayoutInflater.from(context);
        this.data = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.IMU = IMU;
        this.shock = shock;
        this.date = date;
        this.rfid = rfid;
    }



    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String id = "id: " + data.get(position) + " date: " + date.get(position);
        holder.myTextView.setText(id);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return data.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.moduleId);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    ArrayList<String> getItem(int id) {
        ArrayList<String> information = new ArrayList<>();
        information.add(data.get(id));
        information.add(date.get(id));
        information.add(latitude.get(id));
        information.add(longitude.get(id));
        information.add(IMU.get(id));
        information.add(shock.get(id));
        information.add(rfid.get(id));
        return information;
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity implements this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

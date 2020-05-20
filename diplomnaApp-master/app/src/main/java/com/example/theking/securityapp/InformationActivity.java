package com.example.theking.securityapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class InformationActivity extends AppCompatActivity implements InformationAdapter.ItemClickListener {
    private EndlessRecyclerViewScrollListener scrollListener;
    private RecyclerView rvItems;
    private InformationAdapter adapter;
    private ArrayList<String> ids;
    private ArrayList<String> latitude;
    private ArrayList<String> longitude;
    private ArrayList<String> IMU;
    private ArrayList<String> shock;
    private ArrayList<String> date;
    private ArrayList<String> rfid;

    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ids = new ArrayList<>();
        latitude = new ArrayList<>();
        longitude = new ArrayList<>();
        IMU = new ArrayList<>();
        shock = new ArrayList<>();
        date = new ArrayList<>();
        rfid = new ArrayList<>();
        loadNextDataFromApi(counter);
        counter++;


        rvItems = (RecyclerView) findViewById(R.id.rvItems);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvItems.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvItems.getContext(),
                linearLayoutManager.getOrientation());
        rvItems.addItemDecoration(dividerItemDecoration);

        adapter = new InformationAdapter(this, ids, latitude, longitude, IMU, shock, date, rfid);
        adapter.setClickListener(this);
        rvItems.setAdapter(adapter);



        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadNextDataFromApi(counter);
                counter++;
            }
        };
        // Adds the scroll listener to RecyclerView
        rvItems.addOnScrollListener(scrollListener);

    }

    private String getUsername(){
        SharedPreferences settings = getApplicationContext().getSharedPreferences("userName", 0);
        return settings.getString("name", "No name defined");
    }



    // Append the next page of data into the adapter
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyDataSetChanged()`
        String username = getUsername();
        if (username == null || username.equals("")){
            //error
        }else {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://78.130.176.59/server/devices.php?apicall=1&username=" + username + "&page=" + offset*10;

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Configure the RecyclerView
                            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                            if(!response.isEmpty()) {
                                String[] splitedModules = response.split("/");
                                ArrayList<String> data = new ArrayList<>(Arrays.asList(splitedModules));

                                for(int i = 0; i < splitedModules.length; i++){
                                    Toast.makeText(getApplicationContext(), splitedModules[i], Toast.LENGTH_LONG).show();

                                    String[] splitedModuleData = splitedModules[i].split(";");

                                    ids.addAll(Arrays.asList(splitedModuleData[0]));
                                    date.addAll(Arrays.asList(splitedModuleData[1]));
                                    latitude.addAll(Arrays.asList(splitedModuleData[2]));
                                    longitude.addAll(Arrays.asList(splitedModuleData[3]));
                                    IMU.addAll(Arrays.asList(splitedModuleData[4]));
                                    shock.addAll(Arrays.asList(splitedModuleData[5]));
                                    rfid.addAll(Arrays.asList(splitedModuleData[6]));

                                }

                                adapter.notifyDataSetChanged();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        Intent gpsActivity = new Intent(InformationActivity.this, GPSActivity.class);
        gpsActivity.putStringArrayListExtra("info", (ArrayList<String>) adapter.getItem(position));
        InformationActivity.this.startActivity(gpsActivity);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

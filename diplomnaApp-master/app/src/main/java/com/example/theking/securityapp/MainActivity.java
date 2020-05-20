package com.example.theking.securityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Button currentStateButton;
    private Button logInfoButton;
    private Button manualControl;

    private ArrayList<String> ids;
    private ArrayList<String> latitude;
    private ArrayList<String> longitude;
    private ArrayList<String> IMU;
    private ArrayList<String> shock;
    private ArrayList<String> date;
    private ArrayList<String> rfid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stopService(new Intent(this, NotifyService.class));
        Toast.makeText(getApplicationContext(), getUsername(), Toast.LENGTH_LONG).show();
        currentStateButton = findViewById(R.id.currentStateButton);
        logInfoButton = findViewById(R.id.infoLogButton);
        manualControl = findViewById(R.id.manualControlButton);

        ids = new ArrayList<>();
        latitude = new ArrayList<>();
        longitude = new ArrayList<>();
        IMU = new ArrayList<>();
        shock = new ArrayList<>();
        date = new ArrayList<>();
        rfid = new ArrayList<>();

        if(getUsername().equals("No name defined")){
            Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(loginActivity);
            MainActivity.this.finish();
        }else{
            startService(new Intent(this, NotifyService.class));

            currentStateButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadCurrentState(0);

                }
            });
            logInfoButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent informationActivity = new Intent(MainActivity.this, InformationActivity.class);
                    MainActivity.this.startActivity(informationActivity);
                }
            });

            manualControl.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent manualActivity = new Intent(MainActivity.this, ManualControl.class);
                    MainActivity.this.startActivity(manualActivity);
                }
            });


        }

    }

    private String getUsername(){
        SharedPreferences settings = getApplicationContext().getSharedPreferences("userName", 0);
        return settings.getString("name", "No name defined");
    }

    // Get the first page of data
    public void loadCurrentState(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Load new activity with the first data from the page
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

                                for(int i = 0; i < splitedModules.length; i++){
                                    Toast.makeText(getApplicationContext(), splitedModules[i], Toast.LENGTH_LONG).show();

                                    String[] splitedModuleData = splitedModules[i].split(";");
                                    DateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                                    Date currentDate = null;
                                    try {
                                        currentDate = df.parse(splitedModuleData[1]);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    Date previousDate = null;
                                    if(!date.isEmpty()){
                                        try {
                                            previousDate = df.parse(date.get(date.size()-1));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        assert currentDate != null;
                                        if(currentDate.compareTo(previousDate) > 0){
                                            ids.set(0,splitedModuleData[0]);
                                            date.set(0,splitedModuleData[1]);
                                            latitude.set(0,splitedModuleData[2]);
                                            longitude.set(0,splitedModuleData[3]);
                                            IMU.set(0,splitedModuleData[4]);
                                            shock.set(0,splitedModuleData[5]);
                                            rfid.set(0,splitedModuleData[6]);
                                        }

                                    }else {
                                        ids.add(splitedModuleData[0]);
                                        date.add(splitedModuleData[1]);
                                        latitude.add(splitedModuleData[2]);
                                        longitude.add(splitedModuleData[3]);
                                        IMU.add(splitedModuleData[4]);
                                        shock.add(splitedModuleData[5]);
                                        rfid.add(splitedModuleData[6]);
                                    }
                                }
                                ArrayList<String> information = new ArrayList<>();
                                information.add(ids.get(0));
                                information.add(date.get(0));
                                information.add(latitude.get(0));
                                information.add(longitude.get(0));
                                information.add(IMU.get(0));
                                information.add(shock.get(0));
                                information.add(rfid.get(0));

                                Intent gpsActivity = new Intent(MainActivity.this, GPSActivity.class);
                                gpsActivity.putStringArrayListExtra("info", information);
                                MainActivity.this.startActivity(gpsActivity);                            }

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

}

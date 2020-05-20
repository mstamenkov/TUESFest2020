package com.example.theking.securityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.nio.charset.Charset;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends AppCompatActivity {

    private String passwordValue;
    private String emailValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        final Button Login = findViewById(R.id.loginButton);
        Login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText email = (EditText) findViewById(R.id.emailLogField);
                emailValue = email.getText().toString();

                EditText password = (EditText) findViewById(R.id.passwordLogField);
                passwordValue = password.getText().toString();

                passwordValue = encrypt(passwordValue, "2a925de8ca0248d7");
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url =" http://78.130.176.59/server/login.php?apicall=1&username="+ emailValue+ "&password="+ passwordValue;

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                                Log.println(Log.VERBOSE, "", response + "/");
                                if(response.contains("OK")){
                                    saveUserNameOnDevice(emailValue);
                                    Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                                    LoginActivity.this.startActivity(mainActivity);
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
        });
        final Button Register = findViewById(R.id.goRegisterButton);
        Register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent registerActivity = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerActivity);
            }
        });


    }

    public static String encrypt(String strToEncrypt, String secret) // secret is always 2a925de8ca0248d7
    {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secret.getBytes(), "AES"));
            return Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes(Charset.forName("UTF-8"))), Base64.DEFAULT);
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    private void saveUserNameOnDevice(String userName){
        SharedPreferences settings = getApplicationContext().getSharedPreferences("userName", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("name", userName);
        editor.apply();
    }
}

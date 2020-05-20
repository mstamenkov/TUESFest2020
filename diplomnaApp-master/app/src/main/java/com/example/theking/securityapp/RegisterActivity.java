package com.example.theking.securityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class RegisterActivity extends AppCompatActivity {

    private String emailValue;
    private String passwordValue;
    private String passwordConfirmationValue;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText email = findViewById(R.id.emailField);
        final EditText password = findViewById(R.id.passwordField);
        final EditText passwordConfirmation = findViewById(R.id.passwordConfirmationField);


        final Button Register = findViewById(R.id.registerButton);
        Register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                emailValue = email.getText().toString();
                passwordValue = password.getText().toString();
                passwordConfirmationValue = passwordConfirmation.getText().toString();

                passwordValue = encrypt(passwordValue, "2a925de8ca0248d7");
                passwordConfirmationValue = encrypt(passwordConfirmationValue, "2a925de8ca0248d7");


                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url =" http://78.130.176.59/server/register.php?apicall=1&username="+ emailValue+ "&password="+ passwordValue + "&conf_password=" + passwordConfirmationValue;

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                                Log.println(Log.VERBOSE, "", response + "/");
                                if(response.contains("OK")){
                                    Intent loginActivity = new Intent(RegisterActivity.this, LoginActivity.class);
                                   RegisterActivity.this.startActivity(loginActivity);
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"error" ,Toast.LENGTH_LONG).show();

                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
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
}

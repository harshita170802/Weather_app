package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    EditText etCity,etCountry;
    TextView tvResults;
    private final String url="http://api.openweathermap.org/data/2.5/weather";
    private final String appid = "52929d3fc23fc28abed9aeffbeb04a08";
    DecimalFormat df = new DecimalFormat("##.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etCity= findViewById(R.id.etCity);
        etCountry= findViewById(R.id.etCountry);
        tvResults= findViewById(R.id.tvResults);
    }

    public void getWeatherDetails(View view) {
        String tempUrl="";
        String city =etCity.getText().toString().trim();
        String country= etCountry.getText().toString().trim();
        if(city.equals("")){
            Toast.makeText(MainActivity.this,"City Field cannot be empty",Toast.LENGTH_SHORT).show();
        }
        else if(country.equals("")){

            tempUrl= url + "?q=" + city + "," + country + "&appid=" + appid;
        }
        else{
            tempUrl = url + "?q=" + city  + "&appid="+ appid;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("response",response);
                /*so after this much code we run the app and copy the response from "logcat" and paste it in "online json viewer"
                to create a json file and fetch from it
                */

                //to store the output
                String output="";

                try {
                    //creating a json object from string response
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray= jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather= jsonArray.getJSONObject(0);
                    String description= jsonObjectWeather.getString("description");
                    JSONObject jsonObjectMain = jsonResponse.optJSONObject("main");

                    //-273.15 to convert kelvin to celcius
                    double temp=jsonObjectMain.getDouble("temp")-273.15;
                    double feelslike= jsonObjectMain.getDouble("feels_like") -273.15;
                    double pressure= jsonObjectMain.getDouble("pressure");
                    int humidity = jsonObjectMain.getInt("humidity");

                    JSONObject jsonObjectWind= jsonResponse.getJSONObject("wind");
                    String wind= jsonObjectWind.getString("speed");
                    JSONObject jsonObjectClouds= jsonResponse.getJSONObject("clouds");
                    String clouds= jsonObjectClouds.getString("all");
                    JSONObject jsonObjectsys = jsonResponse.getJSONObject("sys");
                    String countryname= jsonObjectsys.getString("country");

                    tvResults.setTextColor(Color.WHITE);

                    output = "Current weather of "+ city + "(" + countryname + ")"
                            + "\n Temp :" + df.format(temp) + "°C"
                            + "\n Feels like:" + df.format(feelslike) + "°C"
                            + "\n Description:" + description
                            + "\n Pressure" + pressure + "hpa"
                            + "\n Humidity" + humidity + "%"
                            + "\n Wind" + wind + "(m/s)"
                            + "\n Clouds" + clouds + "%";

                    tvResults.setText(output);
                    tvResults.setVisibility(View.VISIBLE);

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),error.toString().trim(),Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
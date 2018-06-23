package com.heaven.task;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    Retrofit.Builder builder;
    Retrofit retrofit;
    String error;
    //private ProgressDialog progressDialog = null;
    private Toolbar myToolbar;
    private Spinner currencySpinner, cryptoSpinner;
    private EditText currencyValue, cryptoValue;
    private String queryCurrency = "USD";
    private String queryValue = "0";
    private boolean coeffReceived = false;
    private float coeff = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        currencySpinner = findViewById(R.id.currency_spinner);
        cryptoSpinner = findViewById(R.id.crypto_spinner);
        currencyValue = findViewById(R.id.currency_value);
        cryptoValue = findViewById(R.id.crypto_value);

        interactiveInit();
    }



    public void sendRequest() {
         String API_BASE_URL = "https://blockchain.info/";

         OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

         builder =
                 new Retrofit.Builder()
                         .baseUrl(API_BASE_URL)
                         .addConverterFactory(
                                 GsonConverterFactory.create()
                         );

         retrofit =
                 builder
                         .client(
                                 httpClient.build()
                         )
                         .build();

         ExampleInterface client = retrofit.create(ExampleInterface.class);

         Call<Number> call =
                 client.requestExample(queryCurrency, queryValue);

         // Execute the call asynchronously. Get a positive or negative callback.
         call.enqueue(new Callback<Number>() {
             @Override
             public void onResponse(Call<Number> call, Response<Number> response) {
                 if(response.body() != null)  {
                     cryptoValue.setText(String.format("%.5f", response.body().floatValue()));
                     if(response.body().intValue() != 0) {
                         coeffReceived = true;
                         coeff = Float.parseFloat(queryValue) / response.body().floatValue();
                     }
                 }
             }

             @Override
             public void onFailure(Call<Number> call, Throwable t) {
                 error = t.getMessage();
                 Log.i("test", error);
             }
         });

    }

/*
    private void showLoading() {

        if (progressDialog == null) {
            try {
                progressDialog = ProgressDialog.show(this, "", "Loading...");
                progressDialog.setCancelable(false);
            } catch (Exception e) {

            }
        }
    }

    public void hideLoading() {

        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    } */

    public void interactiveInit() {
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Cryptocurrency converter");

        ArrayAdapter<CharSequence> sAdapter = ArrayAdapter.createFromResource(this,
                R.array.currency_spinner_array, android.R.layout.simple_spinner_item);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        currencySpinner.setAdapter(sAdapter);

        ArrayAdapter<CharSequence> lAdapter = ArrayAdapter.createFromResource(this,
                R.array.crypto_spinner_array, android.R.layout.simple_spinner_item);
        lAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        cryptoSpinner.setAdapter(lAdapter);

        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                queryCurrency = currencySpinner.getSelectedItem().toString();
                coeffReceived = false;
                if(!queryValue.isEmpty() && !queryValue.startsWith("0"))
                    sendRequest();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        currencyValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                queryValue = s.toString();
                if (!queryValue.isEmpty()) {
                    if (!coeffReceived)
                        sendRequest();
                    else
                        cryptoValue.setText(String.format("%.5f", Float.parseFloat(queryValue) / coeff));
                } else
                    cryptoValue.setText("");
            }
        });

    }
}

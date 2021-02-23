package com.smilias.smarket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class CheckOutActivity extends AppCompatActivity {
    double pr;
    TextView textViewPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        Bundle extras = getIntent().getExtras();
        pr= extras.getDouble("price");
        textViewPrice=findViewById(R.id.textViewPrice);
        textViewPrice.setText("price: "+ String.valueOf(pr));
        //Toast.makeText(CheckOutActivity.this,String.valueOf(pr), Toast.LENGTH_LONG).show();
    }
}
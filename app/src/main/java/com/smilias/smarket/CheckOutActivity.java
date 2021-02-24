package com.smilias.smarket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckOutActivity extends AppCompatActivity {
    double pr;
    TextView textViewPrice;
    EditText editTextTextPersonName,editTextNumber,editTextDate,editTextCCV;
    String PersonName,Number,Date,CCV;

    boolean validateCardExpiryDate(String expiryDate) {
        return expiryDate.matches("(?:0[1-9]|1[0-2])/[0-9]{2}");
    }

    public void finish(View view){
        PersonName = editTextTextPersonName.getText().toString();
        Number = editTextNumber.getText().toString();
        Date = editTextDate.getText().toString();
        CCV = editTextCCV.getText().toString();
        if(PersonName.isEmpty()){
            editTextTextPersonName.setError("Name is required");
            editTextTextPersonName.requestFocus();
            return;
        }
        if (Number.isEmpty()) {
            editTextNumber.setError("Card Number is required");
            editTextNumber.requestFocus();
            return;
        }else if(Number.length()!=16){
            editTextNumber.setError("Card Number is wrong");
            editTextNumber.requestFocus();
            return;
        }
        if (Date.isEmpty()) {
            editTextDate.setError("Expiration Date is required");
            editTextDate.requestFocus();
            return;
        }else if (validateCardExpiryDate(Date)) {
            editTextNumber.setError("Card has already expired");
            editTextNumber.requestFocus();
            return;
        }
        if (CCV.isEmpty()) {
            editTextCCV.setError("CCV is required");
            editTextCCV.requestFocus();
            return;
        }else if(CCV.length()!=3){
            editTextCCV.setError("CCV is wrong");
            editTextCCV.requestFocus();
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        editTextTextPersonName=findViewById(R.id.editTextTextPersonName);
        editTextNumber=findViewById(R.id.editTextNumber);
        editTextDate=findViewById(R.id.editTextDate);
        editTextCCV=findViewById(R.id.editTextCCV);

        Bundle extras = getIntent().getExtras();
        pr= extras.getDouble("price");
        textViewPrice=findViewById(R.id.textViewPrice);
        textViewPrice.setText("price: "+ String.valueOf(pr) +" €");
    }
}
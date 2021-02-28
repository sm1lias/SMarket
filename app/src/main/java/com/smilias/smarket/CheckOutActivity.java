package com.smilias.smarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CheckOutActivity extends AppCompatActivity {
    double pr;
    TextView textViewPrice;
    EditText editTextTextPersonName,editTextNumber,editTextDate,editTextCCV;
    String PersonName,Number,Date,CCV,item1,item2,supermarket;
    Date expiry;
    boolean expired;
    SQLiteDatabase db;
    DatabaseReference myRef;
    FirebaseDatabase database;
    int quantitydb,i;
    ArrayList<Integer> quantityfirebase= new ArrayList<>();
    ArrayList<Integer> quantityorders= new ArrayList<>();
    ArrayList<String> category= new ArrayList<>();
    private FirebaseAuth mAuth;
    FirebaseUser currentFirebaseUser;

    public void finish(View view) throws ParseException {
        i=0;
        PersonName = editTextTextPersonName.getText().toString();
        Number = editTextNumber.getText().toString();
        Date = editTextDate.getText().toString();
        CCV = editTextCCV.getText().toString();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
        simpleDateFormat.setLenient(false);

        try {
            expiry = simpleDateFormat.parse(Date);
            expired = expiry.before(new Date());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(PersonName.isEmpty()){
            editTextTextPersonName.setError("Name is required");
            editTextTextPersonName.requestFocus();
            return;
        }else if(!PersonName.matches("^((?:[A-Z]+ ?){1,3})$")){
            editTextTextPersonName.setError("Name is wrong");
            editTextTextPersonName.requestFocus();
            return;
        }
        else if (Number.isEmpty()) {
            editTextNumber.setError("Card Number is required");
            editTextNumber.requestFocus();
            return;
        }else if(Number.length()!=16){
            editTextNumber.setError("Card Number is wrong");
            editTextNumber.requestFocus();
            return;
        }
        else if (Date.isEmpty()) {
            editTextDate.setError("Expiration Date is required");
            editTextDate.requestFocus();
            return;
        }else if (!Date.matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
            editTextDate.setError("Card date is wrong, right format MM/YY");
            editTextDate.requestFocus();
            return;
        }else if(expired){
            editTextDate.setError("Card has already expired");
            editTextDate.requestFocus();
            return;
        }
        else if (CCV.isEmpty()) {
            editTextCCV.setError("CCV is required");
            editTextCCV.requestFocus();
            return;
        }else if(CCV.length()!=3){
            editTextCCV.setError("CCV is wrong");
            editTextCCV.requestFocus();
            return;
        }
        else {
            Cursor cursor = db.rawQuery("SELECT * FROM cart", null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    item2 = cursor.getString(0);
                    supermarket = cursor.getString(1);
                    quantitydb = cursor.getInt(2);
                    myRef.child("CATEGORIES").child(category.get(i)).child(item2).child(supermarket).child("QUANTITY").setValue(quantityfirebase.get(i) - quantitydb);
                    if(quantityorders.get(i)!=null)
                    myRef.child("ORDERS").child(currentFirebaseUser.getUid()).child(supermarket).child(item2).child("QUANTITY").setValue(quantitydb + quantityorders.get(i));
                    else myRef.child("ORDERS").child(currentFirebaseUser.getUid()).child(supermarket).child(item2).child("QUANTITY").setValue(quantitydb);
                    i++;
                }
            }
        }
        db.execSQL("DROP TABLE cart ");
        db.close();
        Toast.makeText(this, "YOUR BUY IS COMPLETE", Toast.LENGTH_LONG).show();
        Intent intent2= new Intent(this,MainActivity.class);
        startActivity(intent2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        db = openOrCreateDatabase("cartDb", Context.MODE_PRIVATE,null);

        editTextTextPersonName=findViewById(R.id.editTextTextPersonName);
        editTextNumber=findViewById(R.id.editTextNumber);
        editTextDate=findViewById(R.id.editTextDate);
        editTextCCV=findViewById(R.id.editTextCCV);

        Bundle extras = getIntent().getExtras();
        pr= extras.getDouble("price");
        quantityfirebase = extras.getIntegerArrayList("quantityfirebase");
        quantityorders = extras.getIntegerArrayList("quantityorders");
        category = extras.getStringArrayList("category");
        textViewPrice=findViewById(R.id.textViewPrice);
        textViewPrice.setText("price: "+ String.valueOf(pr) +" €");
    }
}
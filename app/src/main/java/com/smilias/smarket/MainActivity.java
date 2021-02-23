package com.smilias.smarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    SharedPreferences preferences;
    public String language,item1,item2,supermarket;
    public double price;
    public Locale locale;
    SQLiteDatabase db;
    DatabaseReference myRef;
    FirebaseDatabase database;
    int quantity;
    public void login(View view) {
        Intent intent2= new Intent(MainActivity.this,LogInActivity.class);
        startActivity(intent2);
    }

    public void lang_change(View view){
        SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
        if (language.equals("el")) {
            editor.putString("lang", "en");
            locale = new Locale("en");
            language="en";
            Toast.makeText(this,"Language changed to english",Toast.LENGTH_SHORT).show();
        }
        else {
            editor.putString("lang", "el");
            locale = new Locale("el");
            language="el";
            Toast.makeText(this,"Η γλώσσα άλλαξε σε ελληνικά",Toast.LENGTH_SHORT).show();
        }
        editor.commit();
        Intent refresh = new Intent(this, MainActivity.class);
        finish();
        startActivity(refresh);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        db = openOrCreateDatabase("cartDb", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS cart(item TEXT,supermarket TEXT, quantity INT)");

        preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        language  = preferences.getString("lang","en");  // Shared preferences
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        this.setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

//         as soon as the application opens the first
//         fragment should be shown to the user
//         in this case it is algorithm fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // By using switch we can easily get
            // the selected fragment
            // by using there id.
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.menuHome:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.menuCart:
                    selectedFragment = new CartFragment();
                    break;
                case R.id.menuLogin:
                    selectedFragment = new LoginFragment();
                    break;
                case R.id.menuCategories:
                    selectedFragment= new CategoriesFragment();
                    break;
            }
            // It will help to replace the
            // one fragment to other.
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, selectedFragment)
                    .commit();
            return true;
        }
    };

    public void testdb(View view ){
        Cursor cursor = db.rawQuery("SELECT * FROM cart",null);
        if (cursor.getCount()>0){
            StringBuilder builder = new StringBuilder();
            while (cursor.moveToNext()){
                builder.append("ITEM:").append(cursor.getString(0)).append("\n");
                builder.append("SUPERMARKET:").append(cursor.getString(1)).append("\n");
                builder.append("QUANTITY:").append(cursor.getString(2)).append("\n");
                builder.append("-----------------------------------\n");
            }
            showMessage("CART",builder.toString());
        }
        else Toast.makeText(this,"sdggag", Toast.LENGTH_LONG).show();
    }
    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .show();
    }
    public void toCheckOut(View view){
        price=0;
        Cursor cursor = db.rawQuery("SELECT * FROM cart",null);
        if (cursor.getCount()>0) {
            while (cursor.moveToNext()){
                item2=cursor.getString(0);
                supermarket=cursor.getString(1);
                quantity = cursor.getInt(2);
                database = FirebaseDatabase.getInstance();
                myRef = database.getReference();
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot MainSnapshot) {
                        for (DataSnapshot snap : MainSnapshot.child("CATEGORIES").getChildren()) {
                            if (snap.hasChild(item2)) {
                                item1=snap.getKey();
                                price = price + (quantity * snap.child(item1).child(item2).child(supermarket).child("PRICE").getValue(double.class));
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
        else Toast.makeText(this,"sdggag", Toast.LENGTH_LONG).show();
        Toast.makeText(this,String.valueOf(price), Toast.LENGTH_LONG).show();
        //Intent intent= new Intent(this, CheckOutActivity.class);
        //startActivity(intent);
    }
}
package com.smilias.smarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    SharedPreferences preferences;
    public String language;
    public Locale locale;



    public void login(View view) {
        Intent intent2= new Intent(MainActivity.this,LogInActivity.class);
        startActivity(intent2);
    }
    public void lang_change(View view){
        Toast.makeText(this,language,Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
        if (language=="el") {
            editor.putString("lang", "en");
            locale = new Locale("en");
            language="en";
        }
        else{
            editor.putString("lang", "el");
            locale = new Locale("el");
            language="el";
        }
        editor.apply();
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        Intent refresh = new Intent(this, MainActivity.class);
        finish();
        startActivity(refresh);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        language = preferences.getString("lang",Locale.getDefault().getLanguage());  // Shared preferences





        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // as soon as the application opens the first
        // fragment should be shown to the user
        // in this case it is algorithm fragment
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
}
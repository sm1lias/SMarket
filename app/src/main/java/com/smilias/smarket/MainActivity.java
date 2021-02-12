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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    public String language;
    public Locale locale;
    ListView listView;
    FirebaseDatabase database;
    ArrayList<String> categories= new ArrayList<>();

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

        preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        language  = preferences.getString("lang","en");  // Shared preferences
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        this.setContentView(R.layout.activity_main);

        listView=findViewById(R.id.listView);

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("SUPERMARKET");
        myRef.addValueEventListener (new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot MainSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot snapshot : MainSnapshot.getChildren()){

                    categories.add(snapshot.getValue(String.class).toString());
                }
                // value = MainSnapshot.child("test").getValue(String.class);
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,categories);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        for(int y=0; y<categories.size(); y++) {
                            if (i == y)
                                Toast.makeText(MainActivity.this, String.valueOf(y), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                // }
//                value=cities.toArray(new String[0]);
//                Toast.makeText(getActivity(), value[0], Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });





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
}
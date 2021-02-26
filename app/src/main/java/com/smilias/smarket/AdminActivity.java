package com.smilias.smarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {
    public String supermarket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        Bundle extras = getIntent().getExtras();
        supermarket = extras.getString("supermarket");

//         as soon as the application opens the first
//         fragment should be shown to the user
//         in this case it is algorithm fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.adminFragment, new CategoriesFragment(supermarket)).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // By using switch we can easily get
            // the selected fragment
            // by using there id.
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.menuAdd:
                    selectedFragment = new CategoriesFragment(supermarket, true);
                    break;
                case R.id.menuDelete:
                    selectedFragment = new CategoriesFragment(supermarket, false);
                    break;
                case R.id.menuLogOut:
                    selectedFragment = new LogOutFragment();
                    break;
            }
            // It will help to replace the
            // one fragment to other.
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adminFragment, selectedFragment)
                    .commit();
            return true;
        }
    };
    }


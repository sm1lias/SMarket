package com.smilias.smarket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity  {
    SharedPreferences preferences;
    public String language,item,item2,supermarket, notItem;
    public double price;
    public Locale locale;
    SQLiteDatabase db;
    DatabaseReference myRef;
    FirebaseDatabase database;
    FirebaseUser cuser;
    int quantitydb,i;
    Button bloginout;
    ArrayList<Integer> quantityfirebase= new ArrayList<>();
    ArrayList<Integer> quantityorders= new ArrayList<>();
    ArrayList<String> category= new ArrayList<>();
    FirebaseUser currentFirebaseUser;
    TextToSpeech tts;
    private static final int REC_RESULT = 653;


    public void login(View view) {
        if (cuser == null) {
            Intent intent2 = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent2);
        }
        else{
            FirebaseAuth.getInstance().signOut();
            cuser=null;
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, new LoginFragment(), "findThisFragment")
                    .commit();
        }
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

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });

        cuser = FirebaseAuth.getInstance().getCurrentUser();
        bloginout=findViewById(R.id.bLogInOut);


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
                case R.id.menuDelete:
                    selectedFragment = new CartFragment();
                    break;
                case R.id.menuLogOut:
                    selectedFragment = new LoginFragment();
                    break;
                case R.id.menuAdd:
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

    public void speech(View view){
        tts.setLanguage(Locale.US);
        tts.setSpeechRate((float) 0.5);
        if (cuser != null)
        tts.speak(FirebaseAuth.getInstance().getCurrentUser().getUid(), TextToSpeech.QUEUE_ADD, null);
        else Toast.makeText(MainActivity.this,"Please LogIn", Toast.LENGTH_LONG).show();
    }

    //gia tin anagnwrisi tis fwnis
    public void recognize(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        if (language.equals("el")) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "el-rGr");
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Παρακαλώ πείτε: παραγγελία");
        }else{
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please say: order");
        }
        startActivityForResult(intent,REC_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        StringBuffer buffer = new StringBuffer();
        super.onActivityResult(requestCode, resultCode, data);
        if (cuser != null) {
            if (requestCode==REC_RESULT && resultCode==RESULT_OK){
                ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (language.equals("el")) {
                    if(matches.contains("παραγγελία")){
                        buffer.append("κωδικός: " + FirebaseAuth.getInstance().getCurrentUser().getUid() + "\n");
                        buffer.append("---------------------------------\n");
                        showMessage(buffer.toString());
                    }else Toast.makeText(MainActivity.this,"Παρακαλώ πείτε: παραγγελία", Toast.LENGTH_LONG).show();
                }else {
                    if (matches.contains("order")) {
                        buffer.append("id: " + FirebaseAuth.getInstance().getCurrentUser().getUid() + "\n");
                        buffer.append("---------------------------------\n");
                        showMessage(buffer.toString());
                    } else Toast.makeText(MainActivity.this, "Say order", Toast.LENGTH_LONG).show();
                }
            }
        }else Toast.makeText(MainActivity.this,"Please LogIn", Toast.LENGTH_LONG).show();
    }

    //gia to showMessage
    public void showMessage(String s){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        if (language.equals("el")) builder.setTitle("ΠΑΡΑΓΓΕΛΙΑ");
        else builder.setTitle("ORDER");
        builder.setMessage(s);
        builder.show();
    }

    public void toMaps(View view ){
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    public void toCheckOut(View view) {
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        i=0;
        ArrayList<String> itemlist=new ArrayList<>();
        if (cuser != null) {
            price = 0.0;
            Cursor cursor = db.rawQuery("SELECT * FROM cart", null);
            if (cursor.getCount() > 0) {
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot MainSnapshot) {
                        while (cursor.moveToNext()) {
                            item2 = cursor.getString(0);
                            supermarket = cursor.getString(1);
                            quantitydb = cursor.getInt(2);
                            for (DataSnapshot snap : MainSnapshot.child("CATEGORIES").getChildren()) {
                                if (snap.hasChild(item2)) {
                                    category.add(snap.getKey());
                                }
                            }
                            price = price + (quantitydb * MainSnapshot.child("CATEGORIES").child(category.get(i)).child(item2).child(supermarket).child("PRICE").getValue(double.class));
                            quantityfirebase.add(MainSnapshot.child("CATEGORIES").child(category.get(i)).child(item2).child(supermarket).child("QUANTITY").getValue(int.class));
                            quantityorders.add(MainSnapshot.child("ORDERS").child(currentFirebaseUser.getUid()).child(supermarket).child(item2).child("QUANTITY").getValue(int.class));
                            if (quantityfirebase.get(i)-quantitydb<0) itemlist.add(item2+" in "+supermarket+", ");
                            i++;
                        }
                        if (!itemlist.isEmpty()){
                            StringBuilder builder = new StringBuilder();
                            for (int i=0;i<itemlist.size();i++) builder.append(itemlist.get(i));
                            notItem=builder.toString();
                            if (itemlist.size()==1)
                                Toast.makeText(MainActivity.this,"The quantity of "+ notItem +" is not available any more", Toast.LENGTH_LONG).show();
                            else Toast.makeText(MainActivity.this,"The quantity of "+ notItem +" are not available any more", Toast.LENGTH_LONG).show();
                        }else {
                            if(i>0){
                            Intent intent = new Intent(MainActivity.this, CheckOutActivity.class);
                            intent.putExtra("price", price);
                            intent.putExtra("quantityfirebase",quantityfirebase);
                            intent.putExtra("category",category);
                            intent.putExtra("quantityorders",quantityorders);
                            startActivity(intent);
                            }
                        }
                        i=0;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            } else Toast.makeText(this, "YOUR CART IS EMPTY", Toast.LENGTH_LONG).show();
        } else Toast.makeText(this, "PLEASE LOG IN", Toast.LENGTH_LONG).show();
    }
}
package com.smilias.smarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private static final String TAG = "VerifyActivity";
    private Button btnVerify, btnAlready;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnVerify:
                verify();
                break;
            case R.id.btnAlready:
                Intent intent=new Intent(VerifyActivity.this,LogInActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void verify(){
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(VerifyActivity.this,getString(R.string.vemail_sent),Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Email sent.");
                            Intent intent=new Intent(VerifyActivity.this,LogInActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        mAuth = FirebaseAuth.getInstance();

        btnVerify=findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(this);

        btnAlready=findViewById(R.id.btnAlready);
        btnAlready.setOnClickListener(this);
    }
}
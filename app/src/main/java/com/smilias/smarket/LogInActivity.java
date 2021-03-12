package com.smilias.smarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText editTEmail, editTPass;
    private static final String TAG = "LogInActivity";
    private String[] adminEmail = {"skadmin","metroadmin","abadmin","lidladmin","myadmin"};
    private String[] adminPass = {"skadmin","metroadmin","abadmin","lidladmin","myadmin"};
    private String[] supermarket = {"SKLAVENITIS","METRO","AB VASILOPOYLOS","LIDL","MY MARKET"};
    private Button btnLogIn, btnSignUp, btnFPass;

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnLogIn:
                logIn();
                break;
            case R.id.btnSignUp:
                Intent intent = new Intent(this,SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.btnFPass:
                Intent intent2=new Intent(LogInActivity.this,ForgotPasswordActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }



    private void logIn() {
        int b=0;
        final String email = editTEmail.getText().toString();
        final String password = editTPass.getText().toString();
        //check oti einai simplirwmena kai zitaei focus an den einai
        if (email.isEmpty()) {
            editTEmail.setError(getString(R.string.email_is_required));
            editTEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTPass.setError(getString(R.string.password_is_required));
            editTPass.requestFocus();
            return;
        }
        for(int i=0;i<5;i++) {
            if(email.equals( adminEmail[i])){
                if(password.equals( adminPass[i])){
                    Intent intent1= new Intent(LogInActivity.this,AdminActivity.class);
                    intent1.putExtra("supermarket",supermarket[i]);
                    startActivity(intent1);
                    break;
                }else{
                    Toast.makeText(LogInActivity.this, getString(R.string.wrong_password),
                            Toast.LENGTH_SHORT).show();
                }
            }else b++;
        }
        if(b==5){
            //log in to user
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(LogInActivity.this, "success.",
                                        Toast.LENGTH_SHORT).show();
                                if (user.isEmailVerified()) {
                                    Intent intent1 = new Intent(LogInActivity.this, MainActivity.class);
                                    startActivity(intent1);
                                } else {
                                    Intent intent2 = new Intent(LogInActivity.this, VerifyActivity.class);
                                    startActivity(intent2);
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LogInActivity.this, getString(R.string.auth_failed),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        editTEmail = findViewById(R.id.editTEmail);
        editTPass = findViewById(R.id.editTPass);
        mAuth = FirebaseAuth.getInstance();

        btnLogIn=findViewById(R.id.btnLogIn);
        btnLogIn.setOnClickListener(this);

        btnSignUp=findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);

        btnFPass=findViewById(R.id.btnFPass);
        btnFPass.setOnClickListener(this);
    }

}
package com.example.racecomm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button LoginButton;
    private EditText user_email, user_pass;
    private TextView register_link;
    private ProgressDialog loading_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        register_link = (TextView) findViewById(R.id.register_from_login);
        user_email = (EditText) findViewById(R.id.login_email);
        user_pass = (EditText) findViewById(R.id.login_password);
        LoginButton = (Button) findViewById(R.id.login_btn);
        loading_bar = new ProgressDialog(this);



        register_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginUser();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            // user is in real-time Database
            SendUserToMainActivity();
        }

    }

    private void SendUserToRegisterActivity() {
        Intent register_intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(register_intent);
        finish();
    }

    private void LoginUser() {
        String email = user_email.getText().toString();
        String pass = user_pass.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass))
            Toast.makeText(this, "No blank fields allowed", Toast.LENGTH_LONG).show();
        else{
            loading_bar.setTitle("Login");
            loading_bar.setMessage("Please wait...");
            loading_bar.show();
            loading_bar.setCanceledOnTouchOutside(true);


            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        SendUserToMainActivity();

                        Toast.makeText(LoginActivity.this, "Successfull", Toast.LENGTH_LONG).show();
                        loading_bar.dismiss();
                    }
                    else{
                        Toast.makeText(LoginActivity.this,
                                "Error Occurred " +task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        loading_bar.dismiss();
                    }
                }
            });
        }




    }

    private void SendUserToMainActivity() {
        Intent main_Intent = new Intent(LoginActivity.this, MainActivity.class);
        main_Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main_Intent);
        finish();
    }
}

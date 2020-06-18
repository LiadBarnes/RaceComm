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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText UserEmail, UserPassword, UserRePass;
    private Button reg_btn;

    private ProgressDialog loading_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        UserEmail = (EditText) findViewById(R.id.reg_email);
        UserPassword = (EditText) findViewById(R.id.reg_pass);
        UserRePass = (EditText) findViewById(R.id.reg_repass);

        reg_btn = (Button) findViewById(R.id.reg_btn);
        loading_bar = new ProgressDialog(this);


        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
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

    private void SendUserToMainActivity() {
        Intent main_Intent = new Intent(RegisterActivity.this, MainActivity.class);
        main_Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main_Intent);
        finish();
    }

    private void CreateNewAccount() {
        String email = UserEmail.getText().toString();
        String pass =UserPassword.getText().toString();
        String repass =UserRePass.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(repass) ) {
            Toast.makeText(this,"No blank fields allowed", Toast.LENGTH_SHORT).show();
        }
        else if(!pass.equals(repass)){
            Toast.makeText(this,"Passwords missmatch", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loading_bar.setTitle("Creating New Account");
            loading_bar.setMessage("Please wait...");
            loading_bar.show();
            loading_bar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {

                        SendUserToSetupActivity();
                        Toast.makeText(RegisterActivity.this, "Successfull", Toast.LENGTH_LONG).show();
                        loading_bar.dismiss();
                    }
                    else{
                        Toast.makeText(RegisterActivity.this,
                                "Error Occurred" +task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        loading_bar.dismiss();
                    }
                }
            });

        }
    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();

    }
}

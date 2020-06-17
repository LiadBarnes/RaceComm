package com.example.racecomm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText UserEmail, UserPassword, UserRePass;
    private Button reg_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        UserEmail = (EditText) findViewById(R.id.reg_email);
        UserPassword = (EditText) findViewById(R.id.reg_pass);
        UserRePass = (EditText) findViewById(R.id.reg_repass);

        reg_btn = (Button) findViewById(R.id.reg_btn);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount() {
        String email = UserEmail.getText().toString();
        String pass =UserPassword.getText().toString();
        String repass =UserRePass.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(repass) ) {
            Toast.makeText(this,"No blank fields allowed", Toast.LENGTH_SHORT).show();
        }
        else if(pass != repass){
            Toast.makeText(this,"Passwords missmatch", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(RegisterActivity.this, "Successfull", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }
}

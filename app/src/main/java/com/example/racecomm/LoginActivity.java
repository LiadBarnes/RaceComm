package com.example.racecomm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText user_email, user_pass;
    private TextView register_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        register_link = (TextView) findViewById(R.id.register_from_login);
        user_email = (EditText) findViewById(R.id.login_email);
        user_pass = (EditText) findViewById(R.id.login_password);
        LoginButton = (Button) findViewById(R.id.login_btn);

        register_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });
    }

    private void SendUserToRegisterActivity() {
        Intent register_intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(register_intent);
        finish();
    }
}

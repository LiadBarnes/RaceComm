package com.example.racecomm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText UserName, FullName, Country;
    private Button save_info_btn;
    private CircleImageView profileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        profileImage = (CircleImageView) findViewById(R.id.setup_profile_pic);
        UserName = (EditText) findViewById(R.id.setup_username);
        FullName = (EditText) findViewById(R.id.setup_Name);
        Country = (EditText) findViewById(R.id.setup_Country);

        save_info_btn = (Button) findViewById(R.id.setup_save_button);
    }
}

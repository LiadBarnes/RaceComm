package com.example.racecomm;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postFeed;
    private Toolbar toolbar;
    private ImageButton AddNewPostButton;

    private CircleImageView NavProfileImage;
    private TextView NavProfileUName;

    private FirebaseAuth mAuth;
    private DatabaseReference user_ref;

    String curr_user_id;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        curr_user_id = mAuth.getCurrentUser().getUid();
        user_ref = FirebaseDatabase.getInstance().getReference().child("Users");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        AddNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawble_layout_main);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileUName = (TextView) navView.findViewById(R.id.nav_user_full_name);

        user_ref.child(curr_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("fullname"))
                    {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        NavProfileUName.setText(fullname);
                    }
                    if(dataSnapshot.hasChild("profileimage"))
                    {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(NavProfileImage);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Profile name does not exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                UserMenuSelector(menuItem);
                return false;
            }
        });

        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToPostActivity();
            }
        });

        //DisplayAllUsersPosts();

    }



    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null) {
            // user is not logged in
            SendUserToLoginActivity();
        }
        else{
         CheckUserExistance();
        }

    }

    private void CheckUserExistance() {
        final String user_id = mAuth.getCurrentUser().getUid();

        user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(user_id)){
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToPostActivity() {
        Intent add_new_post = new Intent(MainActivity.this, PostActivity.class);
        startActivity(add_new_post);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_post:
                SendUserToPostActivity();
                break;
            case R.id.nav_profile:
                Toast.makeText(this,"Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_home:
                Toast.makeText(this,"Home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_friends:
                Toast.makeText(this,"Friends", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_find_friends:
                Toast.makeText(this,"Find Friends", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_Messages:
                Toast.makeText(this,"Messenger", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_Settings:
                Toast.makeText(this,"Settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_Logout:
                //Toast.makeText(this,"Logout", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                SendUserToLoginActivity();
                break;
        }
    }

}

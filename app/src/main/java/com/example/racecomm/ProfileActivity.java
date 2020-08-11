package com.example.racecomm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.racecomm.model.Post;
import com.example.racecomm.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private Context context = ProfileActivity.this;
    ImageView image_profile, logout;
    TextView posts, followers, following, fullname, bio;
    Button edit_profile;

    FirebaseUser firebaseUser;
    String profileid;

    ImageButton saved_photos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_layout);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = context.getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

//        profileid = firebaseUser.getUid();

        image_profile = findViewById(R.id.image_profile);
        posts = findViewById(R.id.posts);
        followers = findViewById(R.id.followers);
        following = findViewById(R.id.following);
        fullname = findViewById(R.id.fullname);
        bio = findViewById(R.id.bio);
        edit_profile = findViewById(R.id.edit_profile);
        logout = findViewById(R.id.logout);

        userInfo();
        getFollowers();
        getNrPosts();

        if ( profileid.equals(firebaseUser.getUid()) ) {
            edit_profile.setText("Edit Profile");

            if ( edit_profile.getText().toString().toUpperCase(Locale.getDefault()).contains("EDIT PROFILE") ) {
                edit_profile.setVisibility(View.GONE);
                logout.setVisibility(View.VISIBLE);
            } else {
                edit_profile.setVisibility(View.VISIBLE);
                logout.setVisibility(View.GONE);
            }
        } else {
            edit_profile.setVisibility(View.VISIBLE);
            logout.setVisibility(View.GONE);
            checkFollow();
        }

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = edit_profile.getText().toString();

                if ( btn.toLowerCase(Locale.getDefault()).equals("follow") ) {

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                } else if ( btn.toLowerCase(Locale.getDefault()).equals("following") ) {

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();

                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setIcon(R.drawable.profile_icon)
                        .setTitle("Log Out Application")
                        .setMessage("Are you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(ProfileActivity.this, LoginActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ( this == null ) {
                    return;
                }

                User user = dataSnapshot.getValue(User.class);

                if ( user.getProfileimage() != null || user.getProfileimage() != "" ) {
                    Glide.with(ProfileActivity.this).load(user.getProfileimage()).into(image_profile);
                } else {
                    image_profile.setImageResource(R.drawable.placeholder);
                }

                fullname.setText(user.getFullname());
                bio.setText(user.getStatus());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private void checkFollow() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ( dataSnapshot.child(profileid).exists() ) {
                    edit_profile.setText("following");
                } else {
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                following.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getNrPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if ( post.getPublisher().equals(profileid) ) {
                        i++;
                    }
                }
                posts.setText("" + i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

package com.example.racecomm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button save_info_btn;
    private ProgressDialog loadingBar;
    private ProgressDialog loading_bar;
    private DatabaseReference user_ref;
    private CircleImageView ProfileImage;
    private StorageReference user_profile_img_ref;
    private EditText UserName, FullName, Country, Car;

    String curr_user_id;
    final static int gallery_picker = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        loading_bar = new ProgressDialog(this);

        ProfileImage = (CircleImageView) findViewById(R.id.setup_profile_pic);
        UserName = (EditText) findViewById(R.id.setup_username);
        FullName = (EditText) findViewById(R.id.setup_Name);
        Country = (EditText) findViewById(R.id.setup_Country);
        Car = (EditText) findViewById(R.id.setup_Car);
        save_info_btn = (Button) findViewById(R.id.setup_save_button);
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        curr_user_id = mAuth.getCurrentUser().getUid();
        user_ref = FirebaseDatabase.getInstance().getReference().child("Users").child(curr_user_id);
        user_profile_img_ref = FirebaseStorage.getInstance().getReference().child("Profile_Images");


        save_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountInformation();
            }
        });

        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery_Intent = new Intent();
                gallery_Intent.setAction(Intent.ACTION_GET_CONTENT);
                gallery_Intent.setType("image/*");
                startActivityForResult(gallery_Intent, gallery_picker);
            }
        });

        user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ( dataSnapshot.exists() ) {
                    if ( dataSnapshot.hasChild("profileimage") ) {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(ProfileImage);
                    } else {
                        Toast.makeText(SetupActivity.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == gallery_picker && resultCode == RESULT_OK && data != null ) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if ( requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if ( resultCode == RESULT_OK ) {

                loadingBar.setTitle("Profile picture uploading");
                loadingBar.setMessage("Please wait...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();

                StorageReference filePath = user_profile_img_ref.child(curr_user_id + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if ( task.isSuccessful() ) {

                            Toast.makeText(SetupActivity.this, "The image saved successfully", Toast.LENGTH_SHORT).show();

                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();

                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    user_ref.child("profileimage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if ( task.isSuccessful() ) {
                                                        Intent selfIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                                        startActivity(selfIntent);

                                                        Toast.makeText(SetupActivity.this, "The image saved successfully...", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(SetupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                    }
                                                    loadingBar.dismiss();
                                                }
                                            });
                                }
                            });
                        }
                    }
                });
            } else {
                Toast.makeText(SetupActivity.this, "Error Occured, Try again", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private void SaveAccountInformation() {
        String user_name = UserName.getText().toString();
        String full_name = FullName.getText().toString();
        String country = Country.getText().toString();
        String car = Car.getText().toString();

        if ( TextUtils.isEmpty(user_name) || TextUtils.isEmpty(full_name) || TextUtils.isEmpty(country) ) {
            Toast.makeText(this, "No blank fields allowed", Toast.LENGTH_SHORT).show();
        } else {
            loading_bar.setTitle("Saving your information");
            loading_bar.setMessage("Please wait...");
            loading_bar.show();
            loading_bar.setCanceledOnTouchOutside(true);

            HashMap<String, Object> user_map = new HashMap<>();
            user_map.put("username", user_name);
            user_map.put("fullname", full_name);
            user_map.put("gender", "none");
            user_map.put("country", country);
            user_map.put("status", car);
            user_map.put("birthdate", "none");

            user_ref.updateChildren(user_map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if ( task.isSuccessful() ) {
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "User information stored successfully", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SetupActivity.this,
                                "Error Occurred" + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                    loading_bar.dismiss();
                }
            });


        }


    }

    private void SendUserToMainActivity() {
        Intent main_Intent = new Intent(SetupActivity.this, MainActivity.class);
        main_Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main_Intent);
        finish();
    }
}

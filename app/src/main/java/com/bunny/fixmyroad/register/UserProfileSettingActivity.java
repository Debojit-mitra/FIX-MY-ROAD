package com.bunny.fixmyroad.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bunny.fixmyroad.MainActivity;
import com.bunny.fixmyroad.R;
import com.bunny.fixmyroad.ReadWriteUserDetails;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileSettingActivity extends AppCompatActivity {

    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewDoB, textViewGender, textViewMobile;
    private SpinKitView spinkit1;
    private String fullName, email, doB, gender, mobile;
    public CircleImageView imageView;
    private ImageView userprofilesetting_refresh;
    private Button button_continue;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_setting);


        textViewWelcome =  findViewById(R.id.textview_show_welcome);
        textViewFullName =  findViewById(R.id.textview_show_full_name);
        textViewEmail =  findViewById(R.id.textview_show_email);
        textViewDoB =  findViewById(R.id.textview_show_dob);
        textViewGender =  findViewById(R.id.textview_show_gender);
        textViewMobile =  findViewById(R.id.textview_show_mobile);
        button_continue = findViewById(R.id.button_continue);
        userprofilesetting_refresh = findViewById(R.id.userprofilesetting_refresh);

        spinkit1 = findViewById(R.id.spin_kit);
        Sprite circle = new Circle();
        spinkit1.setIndeterminateDrawable(circle);

        //when continue button clicked takes you to main_activity
        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileSettingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //set onClickListener on imageView to open UploadProfilePictureActivity
        imageView = findViewById(R.id.imageView_profile_dp);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileSettingActivity.this, UploadProfilePictureActivity.class);
                startActivity(intent);
            }

        });

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(UserProfileSettingActivity.this, "Something went wrong! User`s details are not available.",
                    Toast.LENGTH_LONG).show();
        }else {
            spinkit1.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            showUserProfile(firebaseUser);

        }

        //set user dp
        Uri uri = firebaseUser.getPhotoUrl();

        //image viewer
        //Picasso.get().load(uri).rotate(30).into(imageView);
        Glide.with(this).load(uri).centerCrop().into(imageView);

        userprofilesetting_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        Intent intent = new Intent(UserProfileSettingActivity.this, UserProfileSettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
        Toast.makeText(UserProfileSettingActivity.this, "Refreshing....", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //extracting user reference from database from Registered users
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null){
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    doB = readUserDetails.dob;
                    gender = readUserDetails.gender;
                    mobile = readUserDetails.mobile;

                    textViewWelcome.setText("Welcome, " + fullName + "!");
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewDoB.setText(doB);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);

                    //set user dp
                    Uri uri = firebaseUser.getPhotoUrl();

                    //image viewer
                   //Picasso.get().load(uri).rotate(90).into(imageView);
                   Glide.with(imageView).load(uri).centerCrop().into(imageView);


                    spinkit1.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    Toast.makeText(UserProfileSettingActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                spinkit1.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(UserProfileSettingActivity.this, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }


}
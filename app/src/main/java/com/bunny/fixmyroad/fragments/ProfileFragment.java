package com.bunny.fixmyroad.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bunny.fixmyroad.MainActivity;
import com.bunny.fixmyroad.SelectActivity;
import com.bunny.fixmyroad.admin.AdminMainActivity;
import com.bunny.fixmyroad.login.LoginActivity;
import com.bunny.fixmyroad.R;
import com.bunny.fixmyroad.ReadWriteUserDetails;
import com.bunny.fixmyroad.profileActivities.ChangePasswordActivity;
import com.bunny.fixmyroad.profileActivities.EditProfileActivity;
import com.bunny.fixmyroad.register.UploadProfilePictureActivity;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;



public class ProfileFragment extends Fragment {

    private ImageView userprofile_refresh;
    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewDoB, textViewGender, textViewMobile;
    private String fullName, email, doB, gender, mobile;
    private FirebaseAuth authProfile;
    private Button button_logout, button_edit_profile,button_feedback;
    private ImageView imageView_profile_dp;
    FragmentTransaction fragmentTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_profile, container, false);
        View view = inflater.inflate(R.layout.fragment_profile,container,false);


        authProfile = FirebaseAuth.getInstance();
        //get instance of the current user
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        //view.findViewById()
        userprofile_refresh = view.findViewById(R.id.userprofile_refresh);
        textViewWelcome =  view.findViewById(R.id.textview_show_welcome);
        textViewFullName =  view.findViewById(R.id.textview_show_full_name);
        textViewEmail =  view.findViewById(R.id.textview_show_email);
        textViewDoB =  view.findViewById(R.id.textview_show_dob);
        textViewGender =  view.findViewById(R.id.textview_show_gender);
        textViewMobile =  view.findViewById(R.id.textview_show_mobile);
        //progressBar = findViewById(R.id.progressBar);

        //button
        button_logout = view.findViewById(R.id.button_logout);
        button_edit_profile =  view.findViewById(R.id.button_edit_profile);
        button_feedback =view.findViewById(R.id.button_feedback);


        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new KAlertDialog(requireContext(), KAlertDialog.WARNING_TYPE)
                        .setTitleText("Logout!")
                        .setContentText("Are you sure you want to Logout?")
                        .setCancelClickListener("Cancel",null)
                        .setConfirmClickListener("Yes", new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                authProfile.signOut();
                                Intent intent = new Intent(requireContext(), LoginActivity.class);
                                //clear stack to prevent user from coming back to main activity by pressing back button after logging out
                                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                getActivity().finish();
                                Toast.makeText(requireContext(), "You Have Been Logged Out!!", Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();




            }
        });

        button_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        button_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        //imageview
        imageView_profile_dp = view.findViewById(R.id.imageView_profile_dp);
       /* imageView_profile_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), UploadProfilePictureActivity.class);
                startActivity(intent);
            }
        });*/

        //set user dp
        Uri uri = firebaseUser.getPhotoUrl();

        //image viewer
        //Picasso.get().load(uri).into(imageView_profile_dp);
        Glide.with(this).load(uri).centerCrop().into(imageView_profile_dp);

        userprofile_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction = getFragmentManager().beginTransaction();
                ProfileFragment profileFragment = new ProfileFragment();
                fragmentTransaction.replace(R.id.fragmentReplace,profileFragment);
                fragmentTransaction.commit();
                Toast.makeText(requireContext(), "Refreshing....", Toast.LENGTH_SHORT).show();
            }
        });




        if(firebaseUser == null){
            Toast.makeText(requireContext(), "Something went wrong! User`s details are not available.",
                    Toast.LENGTH_LONG).show();
        }else {
            //progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);

        }



        return view;

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

                   // progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Something went wrong!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    //create actionbar menu

}
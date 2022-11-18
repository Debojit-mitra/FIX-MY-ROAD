package com.bunny.fixmyroad.profileActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bunny.fixmyroad.MainActivity;
import com.bunny.fixmyroad.R;
import com.bunny.fixmyroad.ReadWriteUserDetails;
import com.bunny.fixmyroad.fragments.ProfileFragment;
import com.bunny.fixmyroad.register.RegistrationActivity;
import com.bunny.fixmyroad.register.UploadProfilePictureActivity;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {

    private EditText  editTextUpdateName, editTextUpdateDoB, editTextUpdateMobile;
    private RadioGroup radioGroupUpdateGender;
    private RadioButton radioButtonUpadateGenderSelected;
    private String textFullName, textDoB, textGender, textMobile;
    private FirebaseAuth authProfile;
    private SpinKitView spinkit1;
    private Button button_change_password, button_delete_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editTextUpdateName =  findViewById(R.id.editText_update_profile_full_name);
        editTextUpdateDoB =  findViewById(R.id.editText_update_profile_dob);
        editTextUpdateMobile =  findViewById(R.id.editText_update_profile_mobile);
        radioGroupUpdateGender =  findViewById(R.id.radio_group_update_profile_gender);

        textFullName = editTextUpdateName.getText().toString();
        textDoB = editTextUpdateDoB.getText().toString();
        textMobile = editTextUpdateMobile.getText().toString();


        spinkit1 = findViewById(R.id.spin_kit);
        Sprite circle = new Circle();
        spinkit1.setIndeterminateDrawable(circle);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        //show profile data
        showProfile(firebaseUser);

        //change password button
        button_change_password = findViewById(R.id.button_change_password);
        button_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        //delete profile
        button_delete_profile = findViewById(R.id.button_delete_profile);
        button_delete_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, DeleteProfileActivity.class);
                startActivity(intent);
            }
        });

        //upload profile picture
        Button buttonUploadProfilePic = findViewById(R.id.button_update_profile_picture);
        buttonUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, UploadProfilePictureActivity.class);
                startActivity(intent);
            }
        });

        //change email
        Button buttonUpdateEmail = findViewById(R.id.button_update_email);
        buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, EditEmailActivity.class);
                startActivity(intent);
            }
        });

        //setting up datepicker on editText
        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //extracting saved date
                String textSADoB[] = textDoB.split("/");

                int day = Integer.parseInt(textSADoB[0]);
                int month = Integer.parseInt(textSADoB[1]) - 1;  //to take are of index starting from 0
                int year = Integer.parseInt(textSADoB[2]);

                DatePickerDialog picker;

                //date picker dialog
                picker = new DatePickerDialog(EditProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editTextUpdateDoB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        //update profile button
        Button buttonUpdateProfile = findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(firebaseUser);
            }
        });


    }

    private void updateProfile(FirebaseUser firebaseUser) {

        int selectedGenderID = radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpadateGenderSelected = findViewById(selectedGenderID);

        //validate mobile number
        String mobileRegex = "[6-9][0-9]{9}";  //first no. can be {6,8,9} and rest 9 can be any number
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(textMobile);

        if (TextUtils.isEmpty(textFullName)) {
            Toast.makeText(EditProfileActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
            editTextUpdateName.setError("Full name is required");
            editTextUpdateName.requestFocus();
        } else if (TextUtils.isEmpty(textDoB)) {
            Toast.makeText(EditProfileActivity.this, "Please select your Date of Birth", Toast.LENGTH_LONG).show();
            editTextUpdateDoB.setError("Date of Birth is required");
            editTextUpdateDoB.requestFocus();
        }else if (TextUtils.isEmpty(radioButtonUpadateGenderSelected.getText())) {
            Toast.makeText(EditProfileActivity.this, "Please select Gender", Toast.LENGTH_LONG).show();
            radioButtonUpadateGenderSelected.setError("Gender is require!");
            radioButtonUpadateGenderSelected.requestFocus();
        } else if (TextUtils.isEmpty(textMobile)) {
            Toast.makeText(EditProfileActivity.this, "Please enter your Mobile Number", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile Number is required");
            editTextUpdateMobile.requestFocus();
        } else if (textMobile.length() != 10) {
            Toast.makeText(EditProfileActivity.this, "Please enter your Mobile Number", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile Number Should be 10 digits");
            editTextUpdateMobile.requestFocus();
        } else if (!mobileMatcher.find()) {
            Toast.makeText(EditProfileActivity.this, "Please enter your Mobile Number", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile Number is not Valid");
            editTextUpdateMobile.requestFocus();
        } else {
            textGender = radioButtonUpadateGenderSelected.getText().toString();
            textFullName = editTextUpdateName.getText().toString();
            textDoB = editTextUpdateDoB.getText().toString();
            textMobile = editTextUpdateMobile.getText().toString();

            String textName = textFullName;

            String textEmail = firebaseUser.getEmail().toString();

            //enter user data into the firebase realtime database
            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB, textGender, textMobile, textName, textEmail);
            //extract user refernce from database from registered users
            DatabaseReference referceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

            String userID = firebaseUser.getUid();

           // progressBar.setVisibility(View.VISIBLE);
            spinkit1.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            referceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        //setting new display name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().
                                setDisplayName(textFullName).build();
                        firebaseUser.updateProfile(profileUpdates);
                       // progressBar.setVisibility(View.GONE);
                        spinkit1.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(EditProfileActivity.this, "Update Successful!! Refresh", Toast.LENGTH_SHORT).show();

                        //stop user from returning to editProfileActivity
                        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                             Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            
                            throw task.getException();
                            
                        } catch (Exception e) {
                            Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        //progressBar.setVisibility(View.GONE);
                        spinkit1.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            });

        }


    }

    //fetch data from firebase and display
    private void showProfile(FirebaseUser firebaseUser) {

        String userIDofRegistered = firebaseUser.getUid();

        //Extracting user reference from database for registered users
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

      //  progressBar.setVisibility(View.VISIBLE);
        spinkit1.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails =  snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    textFullName = firebaseUser.getDisplayName();
                    textDoB = readUserDetails.dob;
                    textGender = readUserDetails.gender;
                    textMobile = readUserDetails.mobile;

                    editTextUpdateName.setText(textFullName);
                    editTextUpdateDoB.setText(textDoB);
                    editTextUpdateMobile.setText(textMobile);

                    if (textGender.equals("Male")) {
                        radioButtonUpadateGenderSelected = findViewById(R.id.radio_male);
                    } else {
                        radioButtonUpadateGenderSelected = findViewById(R.id.radio_female);
                    }
                    radioButtonUpadateGenderSelected.setChecked(true);
                    //progressBar.setVisibility(View.GONE);
                    spinkit1.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    Toast.makeText(EditProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    //progressBar.setVisibility(View.GONE);
                    spinkit1.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, "Something went wrong! error!", Toast.LENGTH_SHORT).show();
                //progressBar.setVisibility(View.GONE);
                spinkit1.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
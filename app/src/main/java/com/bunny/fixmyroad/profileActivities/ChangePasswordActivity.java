package com.bunny.fixmyroad.profileActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bunny.fixmyroad.MainActivity;
import com.bunny.fixmyroad.R;
import com.bunny.fixmyroad.register.RegistrationActivity;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private EditText editTextPwdCurr, editTextPwdNew, EditTextPwdConfirmNew;
    private TextView textViewAuthenticated;
    private Button buttonChnagePwd, buttonReAuthenticate;
    private SpinKitView spinkit1;
    private String userPwdCurr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        editTextPwdNew = findViewById(R.id.editText_change_pwd_new);
        editTextPwdCurr = findViewById(R.id.editText_change_pwd_current);
        EditTextPwdConfirmNew = findViewById(R.id.editText_change_pwd_new_confirm);
        textViewAuthenticated = findViewById(R.id.textview_change_pwd_authenticated);
        buttonReAuthenticate = findViewById(R.id.button_authenticate_pwd);
        buttonChnagePwd = findViewById(R.id.button_update_pwd);

        spinkit1 = findViewById(R.id.spin_kit);
        Sprite circle = new Circle();
        spinkit1.setIndeterminateDrawable(circle);

        //disable edittext for new password, confirm password
        editTextPwdNew.setEnabled(false);
        EditTextPwdConfirmNew.setEnabled(false);
        buttonChnagePwd.setEnabled(false);
        //change color of update email
        buttonChnagePwd.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this, R.color.button_grey));
        buttonReAuthenticate.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this, R.color.black));

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(ChangePasswordActivity.this, "Something went wrong! User details not available", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }

    }

    //reauthenticate user before chnaging password
    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPwdCurr = editTextPwdCurr.getText().toString();

                if (TextUtils.isEmpty(userPwdCurr)) {
                    //Toast.makeText(ChangePasswordActivity.this, "Please enter your Password", Toast.LENGTH_LONG).show();
                    editTextPwdCurr.setError("Password is required");
                    editTextPwdCurr.requestFocus();
                } else if (userPwdCurr.length() <6) {
                    //Toast.makeText(ChangePasswordActivity.this, "Password should be atleast of 6 digits", Toast.LENGTH_LONG).show();
                    editTextPwdCurr.setError("Password should be atleast of 6 digits");
                    editTextPwdCurr.requestFocus();
                } else {
                  //  progressBar.setVisibility(View.VISIBLE);
                    spinkit1.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    //reauthenticate user now
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurr);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()) {
                           //progressBar.setVisibility(View.GONE);
                           spinkit1.setVisibility(View.GONE);
                           getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                           //disable edittext for current pass and authenticate button
                           editTextPwdCurr.setEnabled(false);
                           editTextPwdNew.setEnabled(true);
                           EditTextPwdConfirmNew.setEnabled(true);
                           buttonReAuthenticate.setEnabled(false);
                           buttonChnagePwd.setEnabled(true);
                           //change color of update email
                           buttonReAuthenticate.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this, R.color.button_grey));
                           buttonChnagePwd.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this, R.color.black));


                           //set textview to show user authenticated
                           textViewAuthenticated.setText("You are authenticated/Verified. You can change password now.");

                    buttonChnagePwd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            changePwd(firebaseUser);
                        }
                    });
                       } else {
                           try {
                               throw task.getException();
                           } catch (Exception e) {
                               Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                               
                           }
                           //progressBar.setVisibility(View.GONE);
                           spinkit1.setVisibility(View.GONE);
                           getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                       }

                        }
                    });
                }
            }
        });

    }

    private void changePwd(FirebaseUser firebaseUser) {

        String userPwdNew =editTextPwdNew.getText().toString();
        String userPwdConfirm = EditTextPwdConfirmNew.getText().toString();

        if (TextUtils.isEmpty(userPwdNew)) {
            Toast.makeText(ChangePasswordActivity.this, "Please enter your Password", Toast.LENGTH_LONG).show();
            editTextPwdNew.setError("Password is required");
            editTextPwdNew.requestFocus();
        } else if (userPwdNew.length() <6) {
            Toast.makeText(ChangePasswordActivity.this, "Password should be atleast of 6 digits", Toast.LENGTH_LONG).show();
            editTextPwdNew.setError("Password to weak");
            editTextPwdNew.requestFocus();
        } else if (TextUtils.isEmpty(userPwdConfirm)) {
            Toast.makeText(ChangePasswordActivity.this, "Please confirm your Password", Toast.LENGTH_LONG).show();
            EditTextPwdConfirmNew.setError("Password confirmation is required");
            EditTextPwdConfirmNew.requestFocus();
        } else if (!userPwdNew.equals(userPwdConfirm)) {
            Toast.makeText(ChangePasswordActivity.this, "Please enter same password", Toast.LENGTH_LONG).show();
            EditTextPwdConfirmNew.setError("Password doesn`t match");
            //clear the entered passswords
            editTextPwdNew.clearComposingText();
            EditTextPwdConfirmNew.clearComposingText();
        } else if(userPwdCurr.matches(userPwdNew)) {
            //Toast.makeText(ChangePasswordActivity.this, "New Password cannot be same as old Password.", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("New Password cannot be same as old Password.");
            editTextPwdNew.requestFocus();
        } else {
            //progressBar.setVisibility(View.VISIBLE);
            spinkit1.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChangePasswordActivity.this, "Password Has been Changed", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e){
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                       // progressBar.setVisibility(View.GONE);
                        spinkit1.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                 }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
package com.bunny.fixmyroad.profileActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bunny.fixmyroad.MainActivity;
import com.bunny.fixmyroad.R;
import com.bunny.fixmyroad.SelectActivity;
import com.cazaea.sweetalert.SweetAlertDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class DeleteProfileActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private EditText editTextUserPwd;
    private TextView textViewAuthenticated;
    private String userPwd;
    private SpinKitView spinkit1;
    private Button buttonReAuthenticate, buttonDeleteUser;
    private static final String TAG = "DeleteProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        editTextUserPwd = findViewById(R.id.editText_delete_user_current);
        textViewAuthenticated = findViewById(R.id.textview_delete_user_authenticated);
        buttonReAuthenticate = findViewById(R.id.button_delete_user_authenticate);
        buttonDeleteUser = findViewById(R.id.button_delete_user);

        spinkit1 = findViewById(R.id.spin_kit);
        Sprite circle = new Circle();
        spinkit1.setIndeterminateDrawable(circle);


        //disable delete user
        buttonDeleteUser.setEnabled(false);
        //change color of update email
        buttonDeleteUser.setBackgroundTintList(ContextCompat.getColorStateList(DeleteProfileActivity.this, R.color.button_grey));
        buttonReAuthenticate.setBackgroundTintList(ContextCompat.getColorStateList(DeleteProfileActivity.this, R.color.black));

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        
        if(firebaseUser == null) {
            Toast.makeText(this, "Something went wrong! User Null", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteProfileActivity.this, MainActivity.class);
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
                userPwd = editTextUserPwd.getText().toString();

                if (TextUtils.isEmpty(userPwd)) {
                    //Toast.makeText(ChangePasswordActivity.this, "Please enter your Password", Toast.LENGTH_LONG).show();
                    editTextUserPwd.setError("Password is required");
                    editTextUserPwd.requestFocus();
                } else if (userPwd.length() <6) {
                    //Toast.makeText(ChangePasswordActivity.this, "Password should be atleast of 6 digits", Toast.LENGTH_LONG).show();
                    editTextUserPwd.setError("Password should be atleast of 6 digits");
                    editTextUserPwd.requestFocus();
                } else {
                    spinkit1.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    //reauthenticate user now
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwd);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                spinkit1.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                //disable edittext for current pass and authenticate button
                                editTextUserPwd.setEnabled(false);
                                buttonReAuthenticate.setEnabled(false);
                                buttonDeleteUser.setEnabled(true);
                                //change color of update email
                                buttonReAuthenticate.setBackgroundTintList(ContextCompat.getColorStateList(DeleteProfileActivity.this, R.color.button_grey));
                                buttonDeleteUser.setBackgroundTintList(ContextCompat.getColorStateList(DeleteProfileActivity.this, R.color.button_red));


                                //set textview to show user authenticated
                                textViewAuthenticated.setText("You are Authenticated. You can delete your profile now.");

                                buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showAlertDialog();
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                                spinkit1.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }

                        }
                    });
                }
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void showAlertDialog() {

        /*//setup alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteProfileActivity.this);
        builder.setTitle("Delete user");
        builder.setMessage("Are you sure you want to delete?");
        builder.setCancelable(false);

        //open email apps if user click/taps continue button
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteUser(firebaseUser);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });


        //create the alert dialog
        AlertDialog alertDialog = builder.create();

        //change color of button alert
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.button_red));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
            }
        });

        //show the dialog
        alertDialog.show();*/


        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitleText("Delete Your Profile!")
                .setContentText("Are you sure? Won't be able to recover!")
                .setCancelText("No,cancel!")
                .setConfirmText("Yes,delete it!")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        deleteUser(firebaseUser);
                    }
                })

                .show();

    }

    private void deleteUser(FirebaseUser firebaseUser) {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                deleteUserData();
                authProfile.signOut();
                Toast.makeText(DeleteProfileActivity.this, "Your profile has been deleted!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DeleteProfileActivity.this, SelectActivity.class);
                startActivity(intent);
                finish();
                } else {
                try {
                    throw task.getException();
                } catch (Exception e) {
                    Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                spinkit1.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
            }
        });

    }

    private void deleteUserData() {
        //delete user profile picture
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(firebaseUser.getPhotoUrl().toString());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "OnSuccess: Photo Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
                Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        //delete data from realtime database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "OnSuccess: Photo Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
                Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
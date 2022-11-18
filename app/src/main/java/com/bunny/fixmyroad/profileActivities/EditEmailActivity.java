package com.bunny.fixmyroad.profileActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditEmailActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private SpinKitView spinkit1;
    private TextView textViewAuthenticate;
    private String userOldEmail, userNewEmail, userPwd;
    private Button buttonUpdateEmail;
    private EditText editTextNewEmail, editTextPwd, editText_update_email_old;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);

        editTextPwd = findViewById(R.id.editText_update_email_verify_password);
        editTextNewEmail = findViewById(R.id.editText_update_email_new);
        textViewAuthenticate = findViewById(R.id.textview_update_email_authenticated);
        buttonUpdateEmail = findViewById(R.id.button_update_email);
        editText_update_email_old = findViewById(R.id.editText_update_email_old);

        spinkit1 = findViewById(R.id.spin_kit);
        Sprite circle = new Circle();
        spinkit1.setIndeterminateDrawable(circle);

    buttonUpdateEmail.setEnabled(false);
    editTextNewEmail.setEnabled(false);
    editText_update_email_old.setEnabled(false);
    buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(EditEmailActivity.this, R.color.button_grey));

        authProfile = FirebaseAuth.getInstance();
    firebaseUser = authProfile.getCurrentUser();

    //set old email
        userOldEmail = firebaseUser.getEmail();
        TextView textViewOldEmail = findViewById(R.id.editText_update_email_old);
        textViewOldEmail.setText(userOldEmail);

        if (firebaseUser.equals("")) {
            Toast.makeText(EditEmailActivity.this, "Something Went Wrong!! Error", Toast.LENGTH_SHORT).show();
        } else {
            reAuthenticate(firebaseUser);
        }


    }

    //reauthenticate to update email
    private void reAuthenticate(FirebaseUser firebaseUser) {

        Button buttonVerifyUser = findViewById(R.id.button_authenticate_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //obtain password for authentication
                userPwd = editTextPwd.getText().toString();

                if (TextUtils.isEmpty(userPwd)) {
                    Toast.makeText(EditEmailActivity.this, "Password is required to continue", Toast.LENGTH_LONG).show();
                    editTextPwd.setError("Password is required for authentication");
                    editTextPwd.requestFocus();
                } else {
                    spinkit1.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                spinkit1.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(EditEmailActivity.this, "Password has been verifed!", Toast.LENGTH_SHORT).show();

                                //set textview to authenticated
                                textViewAuthenticate.setText("You are authenticated! You can update your email.");

                                //diasble edittext for passird and enable editext for email
                                editTextNewEmail.setEnabled(true);
                                editTextPwd.setEnabled(false);
                                buttonVerifyUser.setEnabled(false);
                                buttonUpdateEmail.setEnabled(true);

                                //change color of update email
                                buttonVerifyUser.setBackgroundTintList(ContextCompat.getColorStateList(EditEmailActivity.this, R.color.button_grey));
                                buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(EditEmailActivity.this, R.color.black));

                                buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        userNewEmail = editTextNewEmail.getText().toString();
                                        if (TextUtils.isEmpty(userNewEmail)) {
                                            Toast.makeText(EditEmailActivity.this, "Please enter your Email", Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Email is required");
                                            editTextNewEmail.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                            Toast.makeText(EditEmailActivity.this, "Please re-enter your Email", Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Enter valid email");
                                            editTextNewEmail.requestFocus();
                                        } else if(userOldEmail.matches(userNewEmail)) {
                                            Toast.makeText(EditEmailActivity.this, "New Email cannot be same as old email.", Toast.LENGTH_SHORT).show();
                                            editTextNewEmail.setError("Enter new email");
                                            editTextNewEmail.requestFocus();
                                        } else {
                                            spinkit1.setVisibility(View.VISIBLE);
                                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });

                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(EditEmailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    spinkit1.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                }
                            }

                        }
                    });
                }
            }
        });

    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isComplete()) {
                    //verify email
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(EditEmailActivity.this, "Email has been updated. Please verify your new Email!!",
                            Toast.LENGTH_LONG).show();

                  /*  Intent intent = new Intent(EditEmailActivity.this, EditProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);*/
                        firebaseUser.sendEmailVerification();
                        showAlertDialog();

                } else {
                   try {
                       throw task.getException();
                   } catch (Exception e) {
                       Toast.makeText(EditEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                   }
                }
                spinkit1.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void showAlertDialog() {

        //setup alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(EditEmailActivity.this);
        builder.setTitle("Email Not Verified!!");
        builder.setMessage("Please verify your email to continue");
        builder.setCancelable(false);

        //open email apps if user click/taps continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //to email app in new window and not within our app
                startActivity(intent);
                finish();
            }
        });

        //create the alert dialog
        AlertDialog alertDialog = builder.create();
        //show the dialog
        alertDialog.show();

    }
}
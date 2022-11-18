package com.bunny.fixmyroad.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bunny.fixmyroad.MainActivity;
import com.bunny.fixmyroad.R;
import com.bunny.fixmyroad.SelectActivity;
import com.bunny.fixmyroad.login.LoginActivity;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText editText_login_admin_email, editText_login_admin_password;
    private SpinKitView spinkit1;
    private FirebaseAuth authProfile;
    private Button button_admin_login;

    private static final String TAG = "AdminLoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        editText_login_admin_email = findViewById(R.id.editText_login_admin_email);
        editText_login_admin_password = findViewById(R.id.editText_login_admin_password);
        button_admin_login = findViewById(R.id.button_admin_login);

        spinkit1 = findViewById(R.id.spin_kit);
        Sprite circle = new Circle();
        spinkit1.setIndeterminateDrawable(circle);

        authProfile = FirebaseAuth.getInstance();

        //show hide password
        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd_admin);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText_login_admin_password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //if pass is visible then it will hide
                    editText_login_admin_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //change icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    editText_login_admin_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        button_admin_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textEmail = editText_login_admin_email.getText().toString();
                String textPwd = editText_login_admin_password.getText().toString();

                if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(AdminLoginActivity.this, "Please enter your Email", Toast.LENGTH_LONG).show();
                    editText_login_admin_email.setError("Email is required");
                    editText_login_admin_email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(AdminLoginActivity.this, "Please re-enter your Email", Toast.LENGTH_LONG).show();
                    editText_login_admin_email.setError("Enter valid email");
                    editText_login_admin_email.requestFocus();
                }else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(AdminLoginActivity.this, "Please enter your Password", Toast.LENGTH_LONG).show();
                    editText_login_admin_password.setError("Password is required");
                    editText_login_admin_password.requestFocus();
                } else if (textPwd.length() <6) {
                    Toast.makeText(AdminLoginActivity.this, "Password should be atleast of 6 digits", Toast.LENGTH_LONG).show();
                    editText_login_admin_password.setError("Enter Valid Password");
                    editText_login_admin_password.requestFocus();
                } else {
                    spinkit1.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    loginAdmin(textEmail, textPwd);
                }
                
                
            }
        });


    }

    private void loginAdmin(String email, String pwd) {

        authProfile.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(AdminLoginActivity.this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // progressBar.setVisibility(View.GONE);
                    spinkit1.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(AdminLoginActivity.this, "User Successfully Logged In", Toast.LENGTH_SHORT).show();

                    /*Intent intent = new Intent(AdminLoginActivity.this, AdminMainActivity.class);
                    startActivity(intent);
                    finish();*/
                    //get instance of the current user
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();
                    //check if email is verified or not\
                    if(firebaseUser.isEmailVerified()){
                        //Toast.makeText(LoginActivity.this, "User Successfully Logged In", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminLoginActivity.this, AdminMainActivity.class);
                        startActivity(intent);
                        finish();
                    } else{
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialog();
                    }

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        editText_login_admin_email.setError("User doesnt exists. Please Register again!");
                        editText_login_admin_email.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        editText_login_admin_password.setError("Invalid Password!");
                        editText_login_admin_password.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(AdminLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    //progressBar.setVisibility(View.GONE);
                    spinkit1.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    //Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
    private void showAlertDialog() {

        //setup alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminLoginActivity.this);
        builder.setTitle("Email Not Verified!!");
        builder.setMessage("Please verify your email to continue login");

        //open email apps if user click/taps continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //to email app in new window and not within our app
                startActivity(intent);
            }
        });

        //create the alert dialog
        AlertDialog alertDialog = builder.create();
        //show the dialog
        alertDialog.show();

    }

    //check if user is already logged in
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (authProfile.getCurrentUser() != null) {
            String userID = firebaseUser.getUid();
            String dontEnter = "qVI7JCT3sjUH7NM0tdTQYxxSwFY2";
            Log.i("AdmiLoginActivityreport", " = " + userID);
            if (authProfile.getCurrentUser() != null && userID.equals(dontEnter)) {
                Intent intent = new Intent(AdminLoginActivity.this, AdminMainActivity.class);
                startActivity(intent);
                finish();
            } else {

            }
        }
    }
}
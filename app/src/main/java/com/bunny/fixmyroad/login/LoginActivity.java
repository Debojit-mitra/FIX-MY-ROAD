package com.bunny.fixmyroad.login;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextLoginEmail, editTextLoginPwd;
    private TextView textview_login_sign_up, textview_login_forgot_password;
    private ProgressBar progressBar;
    private SpinKitView spinkit1;
    private FirebaseAuth authProfile;

    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextLoginEmail = findViewById(R.id.editText_login_email);
        editTextLoginPwd = findViewById(R.id.editText_login_password);
        textview_login_sign_up = findViewById(R.id.textview_login_sign_up);
        progressBar = findViewById(R.id.progressBar);
        textview_login_sign_up = findViewById(R.id.textview_login_sign_up);
        textview_login_forgot_password = findViewById(R.id.textview_login_forgot_password);

        spinkit1 = findViewById(R.id.spin_kit);
        Sprite circle = new Circle();
        spinkit1.setIndeterminateDrawable(circle);

        authProfile = FirebaseAuth.getInstance();

        //forgotten password
        textview_login_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "You can reset your password now!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));

            }
        });

        //when sign_up is clicked it should go to registration activity
        textview_login_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //show hide password
        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //if pass is visible then it will hide
                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //change icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        //login user
        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = editTextLoginEmail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();

                if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(LoginActivity.this, "Please enter your Email", Toast.LENGTH_LONG).show();
                    editTextLoginEmail.setError("Email is required");
                    editTextLoginEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(LoginActivity.this, "Please re-enter your Email", Toast.LENGTH_LONG).show();
                    editTextLoginEmail.setError("Enter valid email");
                    editTextLoginEmail.requestFocus();
                }else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(LoginActivity.this, "Please enter your Password", Toast.LENGTH_LONG).show();
                    editTextLoginPwd.setError("Password is required");
                    editTextLoginPwd.requestFocus();
                } else if (textPwd.length() <6) {
                    Toast.makeText(LoginActivity.this, "Password should be atleast of 6 digits", Toast.LENGTH_LONG).show();
                    editTextLoginPwd.setError("Enter Valid Password");
                    editTextLoginPwd.requestFocus();
                } else {
                    spinkit1.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    loginUser(textEmail, textPwd);
                }

            }
        });
    }

    private void loginUser(String email, String pwd) {

        authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                   // progressBar.setVisibility(View.GONE);
                    spinkit1.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(LoginActivity.this, "User Successfully Logged In", Toast.LENGTH_SHORT).show();

                    //get instance of the current user
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();
                    //check if email is verified or not\
                    if(firebaseUser.isEmailVerified()){
                        //Toast.makeText(LoginActivity.this, "User Successfully Logged In", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
                        editTextLoginEmail.setError("User doesnt exists. Please Register again!");
                        editTextLoginEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextLoginPwd.setError("Invalid Password!");
                        editTextLoginPwd.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
            Log.i("LoginActivityreport", " = " + userID);
            if (authProfile.getCurrentUser() != null && !userID.equals(dontEnter)) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }else {

            }
        }
    }
}
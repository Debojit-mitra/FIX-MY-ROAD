package com.bunny.fixmyroad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bunny.fixmyroad.admin.AdminLoginActivity;
import com.bunny.fixmyroad.admin.AdminMainActivity;
import com.bunny.fixmyroad.login.LoginActivity;
import com.bunny.fixmyroad.register.RegistrationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SelectActivity extends AppCompatActivity {

    private Button button_login, button_register, button_login_admin;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        authProfile = FirebaseAuth.getInstance();

        button_login = findViewById(R.id.button_login);
        button_register = findViewById(R.id.button_register);
        button_login_admin = findViewById(R.id.button_login_admin);

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectActivity.this, RegistrationActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                //overridePendingTransition(-5, -5);
                //finish();
            }
        });

        button_login_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectActivity.this, AdminLoginActivity.class);
                startActivity(intent);
            }
        });

    }


    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (authProfile.getCurrentUser() != null){
            String userID = firebaseUser.getUid();
            String dontEnter = "qVI7JCT3sjUH7NM0tdTQYxxSwFY2";
            Log.i("SelectActivityreport", " = " + userID);
            if (authProfile.getCurrentUser() != null && !userID.equals(dontEnter)) {
                Intent intent = new Intent(SelectActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (userID.equals(dontEnter)) {
                Intent intent = new Intent(SelectActivity.this, AdminMainActivity.class);
                startActivity(intent);
                finish();
            }
        }else {

        }
    }
}
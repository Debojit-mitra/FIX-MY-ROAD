package com.bunny.fixmyroad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bunny.fixmyroad.fragments.HomeFragment;
import com.bunny.fixmyroad.fragments.ProfileFragment;
import com.bunny.fixmyroad.fragments.ReportsFragment;
import com.bunny.fixmyroad.login.LoginActivity;
import com.developer.kalert.KAlertDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback;
import org.imaginativeworld.oopsnointernet.dialogs.pendulum.DialogPropertiesPendulum;
import org.imaginativeworld.oopsnointernet.dialogs.pendulum.NoInternetDialogPendulum;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FrameLayout ic_home, ic_issues, ic_profile;

    FragmentTransaction fragmentTransaction;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        fragmentTransaction.replace(R.id.fragmentReplace,homeFragment);
        fragmentTransaction.commit();

        ic_home = findViewById(R.id.ic_home);
        ic_issues = findViewById(R.id.ic_issues);
        ic_profile = findViewById(R.id.ic_profile);

        ic_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                ProfileFragment profileFragment = new ProfileFragment();
                fragmentTransaction.replace(R.id.fragmentReplace,profileFragment);
                fragmentTransaction.commit();
                }
        });
        ic_issues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                ReportsFragment reportsFragment = new ReportsFragment();
                fragmentTransaction.replace(R.id.fragmentReplace,reportsFragment);
                fragmentTransaction.commit();
            }
        });
        ic_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                HomeFragment homeFragment = new HomeFragment();
                fragmentTransaction.replace(R.id.fragmentReplace,homeFragment);
                fragmentTransaction.commit();
            }
        });


        authProfile = FirebaseAuth.getInstance();

        //if email not verified
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        if(firebaseUser.isEmailVerified()){
            //Toast.makeText(MainActivity.this, "User Successfully Logged In", Toast.LENGTH_SHORT).show();
        } else{
            firebaseUser.sendEmailVerification();
            authProfile.signOut();
            showAlertDialog();
        }

      /*  //no internet
        NoInternetDialogSignal.Builder builder = new NoInternetDialogSignal.Builder(
                this,
                getLifecycle()
        );
        DialogPropertiesSignal properties = builder.getDialogProperties();
        properties.setConnectionCallback(new ConnectionCallback() { // Optional
            @Override
            public void hasActiveConnection(boolean hasActiveConnection) {
                // ...
            }
        });
        properties.setCancelable(false); // Optional
        properties.setNoInternetConnectionTitle("No Internet"); // Optional
        properties.setNoInternetConnectionMessage("Check your Internet connection and try again"); // Optional
        properties.setShowInternetOnButtons(true); // Optional
        properties.setPleaseTurnOnText("Please turn on"); // Optional
        properties.setWifiOnButtonText("Wifi"); // Optional
        properties.setMobileDataOnButtonText("Mobile data"); // Optional

        properties.setOnAirplaneModeTitle("No Internet"); // Optional
        properties.setOnAirplaneModeMessage("You have turned on the airplane mode."); // Optional
        properties.setPleaseTurnOffText("Please turn off"); // Optional
        properties.setAirplaneModeOffButtonText("Airplane mode"); // Optional
        properties.setShowAirplaneModeOffButtons(true); // Optional

        builder.build();*/

        // No Internet Dialog: Pendulum
        NoInternetDialogPendulum.Builder builder = new NoInternetDialogPendulum.Builder(
                this,
                getLifecycle()
        );

        DialogPropertiesPendulum properties = builder.getDialogProperties();

        properties.setConnectionCallback(new ConnectionCallback() { // Optional
            @Override
            public void hasActiveConnection(boolean hasActiveConnection) {
                // ...
            }
        });
        properties.setCancelable(false); // Optional
        properties.setNoInternetConnectionTitle("No Internet"); // Optional
        properties.setNoInternetConnectionMessage("Check your Internet connection and try again"); // Optional
        properties.setShowInternetOnButtons(true); // Optional
        properties.setPleaseTurnOnText("Please turn on"); // Optional
        properties.setWifiOnButtonText("Wifi"); // Optional
        properties.setMobileDataOnButtonText("Mobile data"); // Optional
        properties.setOnAirplaneModeTitle("No Internet"); // Optional
        properties.setOnAirplaneModeMessage("You have turned on the airplane mode."); // Optional
        properties.setPleaseTurnOffText("Please turn off"); // Optional
        properties.setAirplaneModeOffButtonText("Airplane mode"); // Optional
        properties.setShowAirplaneModeOffButtons(true); // Optional
        builder.build();

    }

    private void showAlertDialog() {

        //setup alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Email Not Verified!!");
        builder.setMessage("Please verify your email to continue login");
        builder.setCancelable(false);

        //open email apps if user click/taps continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                authProfile.signOut();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//to email app in new window and not within our app
                Intent intentnew = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intentnew);
                finish();
                startActivity(intent);
            }
        });

        //create the alert dialog
        AlertDialog alertDialog = builder.create();
        //show the dialog
        alertDialog.show();

    }
    public void reload(){
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        ft.replace(R.id.fragmentReplace, homeFragment);
        ft.commit();
    }
    @Override
    public void onBackPressed() {
       /* new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();*/

        new KAlertDialog(this, KAlertDialog.WARNING_TYPE)
                .setTitleText("Really Exit?")
                .setContentText("Are you sure you want to exit?")
                .setCancelClickListener("Cancel",null)
                .setConfirmClickListener("Yes", new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .show();

    }
}
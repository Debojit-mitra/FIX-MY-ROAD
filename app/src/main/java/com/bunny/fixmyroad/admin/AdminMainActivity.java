package com.bunny.fixmyroad.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bunny.fixmyroad.MainActivity;
import com.bunny.fixmyroad.R;
import com.bunny.fixmyroad.SelectActivity;
import com.bunny.fixmyroad.fragments.HomeFragment;
import com.bunny.fixmyroad.fragments.ProfileFragment;
import com.bunny.fixmyroad.login.LoginActivity;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;

import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback;
import org.imaginativeworld.oopsnointernet.dialogs.pendulum.DialogPropertiesPendulum;
import org.imaginativeworld.oopsnointernet.dialogs.pendulum.NoInternetDialogPendulum;

public class AdminMainActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FrameLayout ic_solved, ic_workinprogress, ic_logout;

    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        SolvedFragment solvedFragment = new SolvedFragment();
        fragmentTransaction.replace(R.id.fragmentReplace_admin,solvedFragment);
        fragmentTransaction.commit();

        ic_solved = findViewById(R.id.ic_solved);
        ic_workinprogress = findViewById(R.id.ic_workinprogress);
        ic_logout = findViewById(R.id.ic_logout);

        ic_solved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                SolvedFragment solvedFragment = new SolvedFragment();
                fragmentTransaction.replace(R.id.fragmentReplace_admin,solvedFragment);
                fragmentTransaction.commit();
            }
        });

        ic_workinprogress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                WorkinprogressFragment workinprogressFragment = new WorkinprogressFragment();
                fragmentTransaction.replace(R.id.fragmentReplace_admin,workinprogressFragment);
                fragmentTransaction.commit();
            }
        });

        ic_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new KAlertDialog(AdminMainActivity.this, KAlertDialog.WARNING_TYPE)
                        .setTitleText("Logout!")
                        .setContentText("Are you sure you want to Logout?")
                        .setCancelClickListener("Cancel",null)
                        .setConfirmClickListener("Yes", new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                authProfile = FirebaseAuth.getInstance();
                                authProfile.signOut();
                                Intent intent = new Intent(AdminMainActivity.this, SelectActivity.class);
                                //clear stack to prevent user from coming back to main activity by pressing back button after logging out
                                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                Toast.makeText(AdminMainActivity.this, "You Have Been Logged Out!!", Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();

            }
        });


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
                        AdminMainActivity.super.onBackPressed();
                    }
                })
                .show();

    }


}
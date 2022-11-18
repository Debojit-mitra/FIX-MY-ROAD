package com.bunny.fixmyroad.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bunny.fixmyroad.R;
import com.bunny.fixmyroad.ReadWriteUserDetails;
import com.bunny.fixmyroad.login.LoginActivity;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDoB, editTextRegisterMobile,
            editTextRegisterPwd, editTextRegisterConfirmPwD;
    private TextView textview_register_sign_in, textview_verify_button;
    private SpinKitView spinkit1;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private DatePickerDialog picker;
    private static final String TAG = "RegistrationActivity";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        authProfile = FirebaseAuth.getInstance();

       // getSupportActionBar().setTitle("Register");
        //Toast.makeText(RegistrationActivity.this, "You can register now", Toast.LENGTH_SHORT).show();
        //initialization
        //progressBar = findViewById(R.id.progressBar);
        spinkit1 = findViewById(R.id.spin_kit);
        Sprite circle = new Circle();
        spinkit1.setIndeterminateDrawable(circle);
        //edittext
        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDoB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwD = findViewById(R.id.editText_register_confirm_password);
        textview_register_sign_in = findViewById(R.id.textview_register_sign_in);


        //radio button
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        //when sign_in is clicked it should go to login activity
        textview_register_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //setting up datepicker on editText
        editTextRegisterDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar =  Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);


                //date picker dialog
                picker = new DatePickerDialog(RegistrationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editTextRegisterDoB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                final Calendar cmax =  Calendar.getInstance();
                cmax.set(2003,01,01);
                picker.getDatePicker().setMaxDate(cmax.getTimeInMillis());
                picker.show();
            }
        });

        //button
        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

                //obtain the entered data
                String textfullname = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textDoB = editTextRegisterDoB.getText().toString();
                String textMobile = editTextRegisterMobile.getText().toString();
                String textPwd = editTextRegisterPwd.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPwD.getText().toString();
                String textGender;

                //validate mobile number
                String mobileRegex = "[6-9][0-9]{9}";  //first no. can be {6,8,9} and rest 9 can be any number
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);


                if (TextUtils.isEmpty(textfullname)) {
                    Toast.makeText(RegistrationActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full name is required");
                    editTextRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegistrationActivity.this, "Please enter your Email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegistrationActivity.this, "Please re-enter your Email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Enter valid email");
                    editTextRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(RegistrationActivity.this, "Please select your Date of Birth", Toast.LENGTH_LONG).show();
                    editTextRegisterDoB.setError("Date of Birth is required");
                    editTextRegisterDoB.requestFocus();
                } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegistrationActivity.this, "Please select your Gender", Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.setError("Gender is required");
                    radioButtonRegisterGenderSelected.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(RegistrationActivity.this, "Please enter your Mobile Number", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile Number is required");
                    editTextRegisterMobile.requestFocus();
                } else if (textMobile.length() != 10) {
                    Toast.makeText(RegistrationActivity.this, "Please enter your Mobile Number", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile Number Should be 10 digits");
                    editTextRegisterMobile.requestFocus();
                } else if (!mobileMatcher.find()) {
                    Toast.makeText(RegistrationActivity.this, "Please enter your Mobile Number", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile Number is not Valid");
                    editTextRegisterMobile.requestFocus();
                }else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(RegistrationActivity.this, "Please enter your Password", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is required");
                    editTextRegisterPwd.requestFocus();
                } else if (textPwd.length() <6) {
                    Toast.makeText(RegistrationActivity.this, "Password should be atleast of 6 digits", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password to weak");
                    editTextRegisterPwd.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPwd)) {
                    Toast.makeText(RegistrationActivity.this, "Please confirm your Password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwD.setError("Password confirmation is required");
                    editTextRegisterConfirmPwD.requestFocus();
                } else if (!textPwd.equals(textConfirmPwd)){
                    Toast.makeText(RegistrationActivity.this, "Please enter same password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwD.setError("Password doesn`t match");
                    //clear the entered passswords
                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwD.clearComposingText();
                } else {
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    spinkit1.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    registerUser(textfullname, textEmail, textDoB, textGender, textMobile, textPwd);

                }
            }
        });


    }



    //register user using the credentials given
    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textMobile, String textPwd) {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        //create user profile
        auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(RegistrationActivity.this, "User Registered Successfully", Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    //update display name of user
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    //enter user data into the firebase realtime database
                    String textName =  textFullName;
                    ReadWriteUserDetails writeuserDetails = new ReadWriteUserDetails (textDoB, textGender, textMobile, textName, textEmail);

                    //extracting user reference from database for "registered users"
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeuserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                           if (task.isSuccessful()){

                               //send verification email
                               firebaseUser.sendEmailVerification();
                               spinkit1.setVisibility(View.GONE);
                               getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                               Toast.makeText(RegistrationActivity.this, "User Registered Successfully. Please verify email!", Toast.LENGTH_LONG).show();

                              //open user profile after successful verification
                               Intent intent = new Intent(RegistrationActivity.this, UserProfileSettingActivity.class);
                               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                       | Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(intent);
                               finish(); //to close register activity

                           } else {
                               Toast.makeText(RegistrationActivity.this, "Registration Failed!", Toast.LENGTH_LONG).show();

                           }

                        }
                    });


                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthUserCollisionException e) {
                        spinkit1.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        editTextRegisterEmail.setError("Already an user is registered with this email");
                        editTextRegisterEmail.requestFocus();

                    } catch (Exception e) {
                        spinkit1.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        spinkit1.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }
        });
    }
}
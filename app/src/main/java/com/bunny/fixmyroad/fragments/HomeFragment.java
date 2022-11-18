package com.bunny.fixmyroad.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bunny.fixmyroad.MainActivity;
import com.bunny.fixmyroad.R;
import com.bunny.fixmyroad.ReadWriteReportDetails;
import com.bunny.fixmyroad.ReadWriteReportSolvedUnsolvedDetails;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;



public class HomeFragment extends Fragment {

    private ImageView imageView1_reports;
    private EditText editText_current_location, editText_report_desc, editText_current_location_address;
    private TextView textview_report_number, textview_report_date, textview_max_limit_reached;
    private Button buttonto_get_location, button_save_report;
    private ScrollView scrollview_home;
    //maps
    private FusedLocationProviderClient client;
    //firebase
    private FirebaseAuth authProfile;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri imageUriReport;

    private ProgressBar progressBar;
    private SpinKitView spinkit1;

    private String countReportNumber;
    private int numberofreports, maxCLicks = 3, currentNumberClick;
    private int CurrentDateReport = 0101;
    public static final String SHARED_PREFS = "sharedprefs";
    public static final String CURRENT_NO = "currentno";
    public static final Integer DATE_REPORT = 0101;



    FragmentTransaction fragmentTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_home, container, false);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        spinkit1 = view.findViewById(R.id.spin_kit);
        Sprite circle = new Circle();
        spinkit1.setIndeterminateDrawable(circle);

        //find viewbyid
        imageView1_reports = view.findViewById(R.id.imageView1_reports);
        editText_current_location = view.findViewById(R.id.editText_current_location);
        editText_report_desc = view.findViewById(R.id.editText_report_desc);
        buttonto_get_location = view.findViewById(R.id.buttonto_get_location);
        button_save_report = view.findViewById(R.id.button_save_report);
        progressBar =  view.findViewById(R.id.progressBar);
        textview_report_number = view.findViewById(R.id.textview_report_number);
        textview_report_date = view.findViewById(R.id.textview_report_date);
        editText_current_location_address = view.findViewById(R.id.editText_current_location_address);
        textview_max_limit_reached = view.findViewById(R.id.textview_max_limit_reached);
        scrollview_home = view.findViewById(R.id.scrollview_home);

        //firebase
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        //setting report number in textview
        if(authProfile.getCurrentUser() != null) {
            String userID = firebaseUser.getUid();
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
            referenceProfile.child(userID).child("reports").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    numberofreports = (int) snapshot.getChildrenCount();
                    int count = 0;
                    count = numberofreports + 1;
                    textview_report_number.setText("Report Number : " + count);
                    //Log.i("reports","total reports = "+count);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    textview_report_number.setText("Report Number : Database Error!");
                }
            });
        }

        //setting date in textview
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        textview_report_date.setText("Date: "+date);

        //maximum reports reached for the day
        SharedPreferences sharedPreferencesload = requireContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        currentNumberClick = sharedPreferencesload.getInt(CURRENT_NO,0);
        CurrentDateReport = sharedPreferencesload.getInt(String.valueOf(DATE_REPORT), 0101);
        String datebtttt = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(new Date());
        int datebtnnnn = Integer.parseInt(datebtttt);
        if(currentNumberClick > maxCLicks && CurrentDateReport == datebtnnnn) {
            button_save_report.setEnabled(false);
            button_save_report.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_shape_grey));
            textview_max_limit_reached.setText("You Have Reach Maximum Reports of the day!");
        }


        //map
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.maps_home);

        //async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {

            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED){

                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                    LocationManager locationManager = (LocationManager) getActivity()
                            .getSystemService(Context.LOCATION_SERVICE);

                        //when loaction service is enabled
                        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                //initialize location
                                Location location = task.getResult();
                                //check permission
                                if(location != null) {
                                    //when location result is not null
                                    //editText_current_location.setText(String.valueOf(location.getLatitude()+","+location.getLongitude()));
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16.0f));

                                }else {
                                    //when location result is null
                                    //initialize location request
                                    LocationRequest locationRequest = new LocationRequest()
                                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                            .setInterval(10000)
                                            .setFastestInterval(1000)
                                            .setNumUpdates(1);
                                    //initialize location call back
                                    LocationCallback locationCallback = new LocationCallback() {
                                        public void onLocationResult( LocationResult locationResult) {
                                            //initialize loaction
                                            Location location1 = locationResult.getLastLocation();
                                            //set
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location1.getLatitude(), location1.getLongitude()), 16.0f));
                                            //editText_current_location.setText(String.valueOf(location1.getLatitude()+","+location1.getLongitude()));
                                        }
                                    };
                                    //request location updates
                                    client.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                                }
                            }
                        });



                }else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                   Manifest.permission.ACCESS_COARSE_LOCATION},101);

                    ((MainActivity)getActivity()).reload();


                }

                //when map is loaded
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        //when clicked on map initialize marker loacation
                        MarkerOptions markerOptions = new MarkerOptions();
                        //set position of marker
                        markerOptions.position(latLng);
                        //set title of marker
                        markerOptions.title(latLng.latitude + " , " + latLng.longitude);
                        //markerOptions.title("MY POSITION");
                        //remove all marker
                        googleMap.clear();
                        //animating to zoom marker
                       googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                latLng,18
                        ));
                        //add marker on map
                        googleMap.addMarker(markerOptions);
                        googleMap.getUiSettings().setZoomGesturesEnabled(false);
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        googleMap.getUiSettings().setAllGesturesEnabled(false);
                        googleMap.getMaxZoomLevel();
                        googleMap.getMinZoomLevel();
                        googleMap.getUiSettings().setScrollGesturesEnabled(false);
                        googleMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);

                    }
                });
            }
        });

        //runtime permission


        //current location //initialize location client
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        //button current location
        buttonto_get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED){
                    //when permission granted //call method
                    getCurrentLocation();
                }else {
                    //when permission is not granted request permission
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},100);
                }
            }
        });

        button_save_report.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String textLocation = editText_current_location.getText().toString();
                String textDescription = editText_report_desc.getText().toString();
                if (TextUtils.isEmpty(textLocation)) {
                    Toast.makeText(requireContext(), "Please CLick Get Location", Toast.LENGTH_SHORT).show();
                    editText_current_location.setError("Location is required!");
                    editText_current_location.requestFocus();
                } else if (TextUtils.isEmpty(textDescription)) {
                    Toast.makeText(requireContext(), "Please Enter Description", Toast.LENGTH_SHORT).show();
                    editText_report_desc.setError("Description is required");
                    editText_report_desc.requestFocus();
                } else {
                    //String textLocation = editText_current_location.getText().toString();
                    //String textDescription = editText_report_desc.getText().toString();
                    String datebt = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(new Date());
                    int datebtn = Integer.parseInt(datebt);
                    SharedPreferences sharedPreferencesload = requireContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                    currentNumberClick = sharedPreferencesload.getInt(CURRENT_NO,0);
                    CurrentDateReport = sharedPreferencesload.getInt(String.valueOf(DATE_REPORT), 0101);

                    if(currentNumberClick > maxCLicks && CurrentDateReport == datebtn) {
                        Log.i("reports"," passed 1"+CurrentDateReport+" "+datebtn);
                        button_save_report.setEnabled(false);
                        textview_max_limit_reached.setText("You Have Reach Maximum Reports of the day!");
                    }else if (currentNumberClick > maxCLicks && CurrentDateReport != datebtn) {
                        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(CURRENT_NO);
                        editor.remove(CURRENT_NO);
                        editor.apply();
                        userreport(textLocation, textDescription);
                       // Log.i("reports"," passed 2"+CurrentDateReport+" "+datebtn);
                    } else if (currentNumberClick <= maxCLicks && CurrentDateReport != datebtn) {
                        userreport(textLocation, textDescription);
                      //  Log.i("reports"," passed 3"+CurrentDateReport+" "+datebtn);
                    }
                }

            }
        });

        imageView1_reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_picture();

            }
        });


        return view;
    }

    private void choose_picture() {

        ImagePicker.with(this)
                .crop()
                .start(1);



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && data!=null && data.getData()!=null){
            imageUriReport = data.getData();
            imageView1_reports.setImageURI(imageUriReport);
        }
    }

    private void userreport(String textLocation, String textDescription) {

        if (imageUriReport !=null){
            //progressBar.setVisibility(View.VISIBLE);
            //progressBar.requestFocus();
            spinkit1.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            storageReference = FirebaseStorage.getInstance().getReference("ReportPics");
            StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid()).child(String.valueOf(currentDateandTime) + ".jpg" );
            fileReference.putFile(imageUriReport).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            String textReportImage = downloadUri.toString();
                            String TextLocationAddress = editText_current_location_address.getText().toString();
                            //int setReportCount = numberofreports+1;
                            //letsCount = 0;
                            FirebaseUser firebaseUser = authProfile.getCurrentUser();
                            String userID = firebaseUser.getUid();
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users").child(userID).child("reports");
                            referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    int count1 = (int) snapshot.getChildrenCount();
                                    //Log.i("reports11","total count = "+count1);
                                    int counttot = count1+1;
                                    countReportNumber = Integer.toString(counttot);
                                    //Log.i("reports12","total count = "+countReportNumber);

                                    String TextReportNumberSaved = countReportNumber;
                                   // Log.i("reports12","total count = "+TextReportNumberSaved);

                                    String ReportNumber = TextReportNumberSaved;
                                   // Log.i("reports22","total count2 = "+ReportNumber);
                                    String TextReportNumber = ReportNumber;
                                   // Log.i("reports22","total count2 = "+TextReportNumber);


                                    //String TextReportNumber = Integer.toString(totalReportNumber);
                                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                    String currentDate = sdf1.format(new Date());
                                    String TextReportDate = currentDate;
                                    String TextStatus = "Unsolved";
                                    ReadWriteReportDetails writeReportDetails = new ReadWriteReportDetails (textLocation, textDescription, textReportImage, TextLocationAddress, TextReportNumber, TextReportDate, TextStatus);
                                    String TextUserID = firebaseUser.getUid();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
                                    String currentDateandTime = sdf.format(new Date());
                                    DatabaseReference referenceProfile1 = FirebaseDatabase.getInstance().getReference("Registered Users").child(userID).child("reports");
                                    referenceProfile1.child(String.valueOf(currentDateandTime)).setValue(writeReportDetails).addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                               // progressBar.setVisibility(View.GONE);

                                                spinkit1.setVisibility(View.GONE);
                                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                Toast.makeText(getActivity(), "It`s Successful", Toast.LENGTH_SHORT).show();
                                                //making all fields empty
                                                editText_current_location.setText("");
                                                editText_current_location_address.setText("");
                                                editText_report_desc.setText("");
                                                imageView1_reports.setImageResource(R.drawable.ic_photo_camera);
                                                imageUriReport = null;

                                                int NewReportNumber = numberofreports+2;
                                                textview_report_number.setText("Report Number : "+NewReportNumber);
                                                //Log.i("reports","total reports = "+NewReportNumber);

                                                String datecu = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(new Date());
                                                int datecur = Integer.parseInt(datecu);

                                                //report updates
                                                SharedPreferences sharedPreferencesload = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                                                currentNumberClick = sharedPreferencesload.getInt(CURRENT_NO, 1);
                                                CurrentDateReport = sharedPreferencesload.getInt(String.valueOf(DATE_REPORT), 0101);

                                                // Log.i("reports","total reports = "+CurrentDateReport+" "+dateof+" "+DATE_REPORT);
                                                if(currentNumberClick == 1 && CurrentDateReport != datecur) {
                                                    int click1 = maxCLicks-1;
                                                    button_save_report.setEnabled(true);
                                                    ++currentNumberClick;
                                                    textview_max_limit_reached.setText(click1+" More Report Updates Availabe for today");
                                                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putInt(CURRENT_NO, currentNumberClick);
                                                    editor.apply();
                                                    Log.i("reports1"," cdr = "+currentNumberClick+" "+CurrentDateReport);


                                                } else if(currentNumberClick == 2 && CurrentDateReport != datecur) {
                                                    int click2 = maxCLicks-2;
                                                    button_save_report.setEnabled(true);
                                                    ++currentNumberClick;
                                                    textview_max_limit_reached.setText(click2+" More Report Updates Availabe for today");
                                                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putInt(CURRENT_NO, currentNumberClick);
                                                    editor.apply();
                                                    Log.i("reports2"," cdr = "+currentNumberClick+" "+CurrentDateReport);


                                                } else if(currentNumberClick == 3 && CurrentDateReport != datecur) {
                                                    int click2 = maxCLicks-3;
                                                    button_save_report.setEnabled(true);
                                                    ++currentNumberClick;
                                                    textview_max_limit_reached.setText(click2+" More Report Updates Availabe for today");
                                                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putInt(CURRENT_NO, currentNumberClick);
                                                    CurrentDateReport = datecur;
                                                    editor.putInt(String.valueOf(DATE_REPORT), CurrentDateReport);
                                                    editor.apply();
                                                    //Log.i("report3"," cdr = "+currentNumberClick+" "+CurrentDateReport);
                                                    button_save_report.setEnabled(false);
                                                    button_save_report.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_shape_grey));


                                                }


                                            }else {
                                               // progressBar.setVisibility(View.GONE);
                                                spinkit1.setVisibility(View.GONE);
                                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                Toast.makeText(getActivity(), "It failed", Toast.LENGTH_SHORT).show();
                                                editText_current_location.setText("");
                                                editText_current_location_address.setText("");
                                                editText_report_desc.setText("");
                                            }

                                        }
                                    });

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            String currentDate = sdf1.format(new Date());
                            String TextReportDate = currentDate;
                            String TextStatus = "Unsolved";
                            String TextUserID = firebaseUser.getUid();

                            DatabaseReference referenceProfile2 = FirebaseDatabase.getInstance()
                                    .getReference("AdminReports").child(currentDate);


                            referenceProfile2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshotop) {
                                    int childCount = (int) snapshotop.getChildrenCount();
                                    //int childCount1 = childCount+1;
                                    String totCount = String.valueOf(childCount);
                                    Log.i("totCount"," setCount = "+totCount);
                                    setCount(totCount);

                                }

                                private void setCount(String totCount) {
                                    int totcountplus = Integer.parseInt(totCount);
                                    int totcountplus1 = totcountplus+1;
                                    String totcountreal = String.valueOf(totcountplus1);
                                    String TextReportID = totcountreal;
                                    ReadWriteReportSolvedUnsolvedDetails writeReportSolvedDetails = new ReadWriteReportSolvedUnsolvedDetails(textLocation, textDescription, textReportImage, TextLocationAddress, TextReportDate, TextStatus, TextUserID, TextReportID);
                                    referenceProfile2.child(totcountreal).setValue(writeReportSolvedDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getActivity(), "Successfull Uploaded to Admin Database", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), "Failed to Uploaded to Admin Database", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                            /*referenceProfile2.child(countintop).setValue(writeReportSolvedDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getActivity(), "Successfull Uploaded to Admin Database", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to Uploaded to Admin Database", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });*/



                          /*  referenceProfile2.setValue(writeReportSolvedDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getActivity(), "Successfull Uploaded to Admin Database", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to Uploaded to Admin Database", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });*/

                        }
                    });
                }
            });
        } else {
            Toast.makeText(requireContext(), "Add Image To Continue Further", Toast.LENGTH_SHORT).show();
        }

        //reff = FirebaseDatabase.getInstance().getReference()
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //check conditon
        if(requestCode == 100 && (grantResults.length > 0) &&
                (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){
            //when permission granted //call method
            getCurrentLocation();
        } else {
            //when permission are denied
            Toast.makeText(getActivity(), "Location Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        //initialize loaction manager
        LocationManager locationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        //check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            //when loaction service is enabled
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //initialize location
                    Location location = task.getResult();
                    //check permission
                    if(location != null) {
                        //when location result is not null
                        editText_current_location.setText(String.valueOf(location.getLatitude()+","+location.getLongitude()));

                        String errorMessage = "";
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                        List<Address> addresses = null;

                        try {
                            addresses = geocoder. getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            //String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                            editText_current_location_address.setText(address);
                        } catch (IOException e) {
                            Toast.makeText(getActivity(), "Some Error Occured", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        //when location result is null
                        //initialize location request
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);
                        //initialize location call back
                        LocationCallback locationCallback = new LocationCallback() {
                            public void onLocationResult( LocationResult locationResult) {
                               //initialize loaction
                                Location location1 = locationResult.getLastLocation();
                                //set

                                editText_current_location.setText(String.valueOf(location1.getLatitude()+","+location1.getLongitude()));
                            }
                        };
                        //request location updates
                        client.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                    }
                }
            });
        }else {
            //when location service disabled
            //open location settings
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }

}
package com.bunny.fixmyroad.fragments;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bunny.fixmyroad.R;
import com.bunny.fixmyroad.ReadWriteReportDetails;
import com.bunny.fixmyroad.myAdapterForReports;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class ReportsFragment extends Fragment {

    RecyclerView recyclierview_reports;
    myAdapterForReports adapter;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth authProfile;
    private TextView textview_ifempty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_reports, container, false);
        View view = inflater.inflate(R.layout.fragment_reports,container,false);

        //firebase
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        textview_ifempty = view.findViewById(R.id.textview_ifempty);
        recyclierview_reports = view.findViewById(R.id.recyclierview_reports);
        recyclierview_reports.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclierview_reports.setItemAnimator(null);//this because or else crash if lockscreen

        String userID = firebaseUser.getUid();
        FirebaseRecyclerOptions<ReadWriteReportDetails> options =
                new FirebaseRecyclerOptions.Builder<ReadWriteReportDetails>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users")
                                        .child(userID).child("reports"), ReadWriteReportDetails.class).build();

        ///check if reports are available and set text
        DatabaseReference referenceProfileCheck = FirebaseDatabase.getInstance().getReference("Registered Users")
                .child(userID);
        referenceProfileCheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("reports")){


                }else {
                        textview_ifempty.setText("No Reports Availabe!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                textview_ifempty.setText("Unable to fetch data from database!");
            }
        });

        adapter = new myAdapterForReports(options);
        recyclierview_reports.setAdapter(adapter);

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
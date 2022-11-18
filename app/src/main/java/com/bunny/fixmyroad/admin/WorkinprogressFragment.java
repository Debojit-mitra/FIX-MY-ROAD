package com.bunny.fixmyroad.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bunny.fixmyroad.R;
import com.bunny.fixmyroad.ReadWriteReportSolvedUnsolvedDetails;
import com.bunny.fixmyroad.myAdapterForReportsUnsolved;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class WorkinprogressFragment extends Fragment {

    private EditText editText_unsolved_date;
    private DatePickerDialog picker;
    private RecyclerView recyclierview_unsolvedreports;
    private Button button_search_unsolved_report, btn_report_Unsolved;
    private TextView textview_ifempty_unsolvedreports;
    private SpinKitView spinkit1;
    myAdapterForReportsUnsolved adapterUnsolved;
    FirebaseUser firebaseUser;
    FirebaseAuth authProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workinprogress, container, false);


        editText_unsolved_date = view.findViewById(R.id.editText_unsolved_date);
        button_search_unsolved_report = view.findViewById(R.id.button_search_unsolved_report);
        //btn_report_Unsolved = view.findViewById(R.id.btn_report_Unsolved);
        textview_ifempty_unsolvedreports = view.findViewById(R.id.textview_ifempty_unsolvedreports);

        spinkit1 = view.findViewById(R.id.spin_kit);
        Sprite circle = new Circle();
        spinkit1.setIndeterminateDrawable(circle);

        editText_unsolved_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar =  Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);


                //date picker dialog
                picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editText_unsolved_date.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                    }
                }, year, month, day);
                final Calendar cmin =  Calendar.getInstance();
                cmin.set(2022,9,01);
                picker.getDatePicker().setMinDate(cmin.getTimeInMillis());
                picker.show();
            }
        });

        //firebase
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        recyclierview_unsolvedreports = view.findViewById(R.id.recyclierview_unsolvedreports);
        recyclierview_unsolvedreports.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclierview_unsolvedreports.setItemAnimator(null);//this because or else crash if lockscreen

        button_search_unsolved_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(editText_unsolved_date.getText().toString().equals(""))
                {
                    Toast.makeText(getActivity(), "Did you select Date!", Toast.LENGTH_SHORT).show();
                }else {
                    spinkit1.setVisibility(View.VISIBLE);
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    String solveDate = String.valueOf(editText_unsolved_date.getText());
                    FirebaseRecyclerOptions<ReadWriteReportSolvedUnsolvedDetails> options =
                            new FirebaseRecyclerOptions.Builder<ReadWriteReportSolvedUnsolvedDetails>()
                                    .setQuery(FirebaseDatabase.getInstance().getReference().child("AdminReports")
                                            .child(solveDate).orderByChild("Status").equalTo("Unsolved"), ReadWriteReportSolvedUnsolvedDetails.class).build();


                    adapterUnsolved = new myAdapterForReportsUnsolved(options);
                    adapterUnsolved.startListening();
                    recyclierview_unsolvedreports.setAdapter(adapterUnsolved);

                    DatabaseReference databaseReferencecheck = FirebaseDatabase.getInstance().getReference()
                            .child("AdminReports").child(solveDate);
                    Query query = databaseReferencecheck.orderByChild("Status").equalTo("Unsolved");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                spinkit1.setVisibility(View.GONE);
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                textview_ifempty_unsolvedreports.setText("");
                            } else {
                                spinkit1.setVisibility(View.GONE);
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                textview_ifempty_unsolvedreports.setText("No Unsolved Reports Availabe!");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            spinkit1.setVisibility(View.GONE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            textview_ifempty_unsolvedreports.setText("Unable to fetch data from database!");
                        }
                    });

                }
            }
        });



        return view;
    }
}
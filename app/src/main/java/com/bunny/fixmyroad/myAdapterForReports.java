package com.bunny.fixmyroad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developer.kalert.KAlertDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;

import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoomConfig;
import ozaydin.serkan.com.image_zoom_view.SaveFileListener;

public class myAdapterForReports extends FirebaseRecyclerAdapter<ReadWriteReportDetails, myAdapterForReports.myviewholder>
{

    public myAdapterForReports(@NonNull FirebaseRecyclerOptions<ReadWriteReportDetails> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull ReadWriteReportDetails model) {

        holder.reportnumber_textview.setText(model.ReportNumber);
        holder.etd_report_progress.setText("Status: "+model.Status);
        holder.longlati_textview.setText(model.LocationLatLon);
        holder.address_textview.setText(model.LocationAddress);
        holder.desciption_textview.setText(model.description);
        holder.reportdate_textview.setText(model.ReportDate);
        Glide.with(holder.image_report.getContext()).load(model.reportImage).centerCrop().into(holder.image_report);

        String status = String.valueOf(holder.etd_report_progress.getText());
        if(status == "Status: Solved"){
            holder.etd_report_progress.setBackgroundResource(R.drawable.button_shape3_green);
        } else {

        }

        //delete button for report
        holder.btn_report_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                String userID = currentFirebaseUser.getUid();
               // Log.i("reports"," passed 1 "+userID);
                String tot = String.valueOf(holder.reportnumber_textview.getText());
                DatabaseReference referenceforReport = FirebaseDatabase.getInstance().getReference()
                        .child("Registered Users").child(userID).child("reports");
                        referenceforReport.orderByChild("ReportNumber").equalTo(tot).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    String keys=ds.getKey();
                                    //Log.i("reports"," passed 1 "+keys);
                                    DatabaseReference referencefordeleting = FirebaseDatabase.getInstance()
                                            .getReference().child("Registered Users").child(userID).child("reports")
                                            .child(keys);
                                    referencefordeleting.removeValue();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

        //click me to locate on google maps
        holder.clicklocate_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new KAlertDialog(view.getContext(), KAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Open Google Maps")
                        .setCustomImage(R.drawable.google_maps)
                        .setContentText("The Location will open in google maps with marker pinned")
                        .setCancelClickListener("Cancel",null)
                        .setConfirmClickListener("Yes", new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                String latlon = String.valueOf(holder.longlati_textview.getText());
                                String uri = "https://www.google.com.tw/maps/place/" + latlon;
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                view.getContext().startActivity(intent);
                                view.setFocusable(false);
                                kAlertDialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        //image popup


    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reports,parent,false);
        return new myviewholder(view);
    }

    public class myviewholder extends RecyclerView.ViewHolder {

        ImageView image_report;
        TextView reportdate_textview, reportnumber_textview, longlati_textview, address_textview, desciption_textview,clicklocate_textview;
        Button btn_report_delete;
        EditText etd_report_progress;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            image_report = itemView.findViewById(R.id.image_report);
            reportdate_textview = itemView.findViewById(R.id.reportdate_textview);
            reportnumber_textview = itemView.findViewById(R.id.reportnumber_textview);
            longlati_textview = itemView.findViewById(R.id.longlati_textview);
            address_textview = itemView.findViewById(R.id.address_textview);
            desciption_textview = itemView.findViewById(R.id.desciption_textview);
            btn_report_delete = itemView.findViewById(R.id.btn_report_delete);
            clicklocate_textview = itemView.findViewById(R.id.clicklocate_textview);
            etd_report_progress = itemView.findViewById(R.id.etd_report_progress);
        }


    }

}

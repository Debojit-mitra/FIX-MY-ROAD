package com.bunny.fixmyroad;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bunny.fixmyroad.admin.AdminMainActivity;
import com.developer.kalert.KAlertDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class myAdapterForReportsUnsolved extends FirebaseRecyclerAdapter <ReadWriteReportSolvedUnsolvedDetails, myAdapterForReportsUnsolved.myviewholder>
{

    public myAdapterForReportsUnsolved(@NonNull FirebaseRecyclerOptions<ReadWriteReportSolvedUnsolvedDetails> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull ReadWriteReportSolvedUnsolvedDetails model) {

        holder.status_unsolved_textview.setText(model.Status);
        holder.reportdate_unsolved_textview.setText(model.ReportDate);
        holder.longlati_unsolved_textview.setText(model.LocationLatLon);
        holder.address_unsolved_textview.setText(model.LocationAddress);
        holder.description_unsolved_textview.setText(model.description);
        holder.btn_report_unSolved.setText("Status: "+model.Status);
        holder.reportID_unsolved_textview.setText(model.ReportID);
        holder.userId_unsolved_textview.setText(model.UserID);
        Glide.with(holder.image_report_unsolved.getContext()).load(model.reportImage).centerCrop().into(holder.image_report_unsolved);

    holder.btn_report_unSolved.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            view.setFocusable(false);
            String userId = String.valueOf(holder.userId_unsolved_textview.getText());
            String latlon = String.valueOf(holder.longlati_unsolved_textview.getText());
            String reportid = String.valueOf(holder.reportID_unsolved_textview.getText());
            String reportdate = String.valueOf(holder.reportdate_unsolved_textview.getText());


            new KAlertDialog(view.getContext(), KAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Is is Solved?")
                    .setContentText("Are you sure?")
                    .setCancelClickListener("Cancel",null)
                    .setConfirmClickListener("Yes", new KAlertDialog.KAlertClickListener() {
                        @Override
                        public void onClick(KAlertDialog kAlertDialog) {
                            DatabaseReference referenceforReport = FirebaseDatabase.getInstance().getReference()
                                    .child("AdminReports").child(reportdate).child(reportid).child("Status");
                            referenceforReport.setValue("Solved");
                           String useri = userId;
                            String lat = latlon;
                            DatabaseReference referenceforReport2 = FirebaseDatabase.getInstance()
                                    .getReference("Registered Users").child(useri).child("reports");
                            //Log.i("reports"," passed 1 "+useri);
                            referenceforReport2.orderByChild("LocationLatLon").equalTo(lat);
                            //String parent = snapshot.getValue().toString();
                            //Log.i("reports"," passed 1 "+parent);

                            referenceforReport2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                                        String key = userSnapshot.getKey();
                                        //Log.i("reports"," passed 100 "+key);
                                        DatabaseReference referenceforReport3 = FirebaseDatabase.getInstance()
                                                .getReference("Registered Users").child(useri).child("reports").child(key);
                                            referenceforReport3.child("Status").setValue("Solved");
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            kAlertDialog.dismiss();
                        }
                    })
                    .show();
        }
    });

        holder.clicklocate_unsolved_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setFocusable(false);
                new KAlertDialog(view.getContext(), KAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Open Google Maps")
                        .setCustomImage(R.drawable.google_maps)
                        .setContentText("The Location will open in google maps with marker pinned")
                        .setCancelClickListener("Cancel",null)
                        .setConfirmClickListener("Yes", new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                String latlon = String.valueOf(holder.longlati_unsolved_textview.getText());
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

        holder.clickUser_unsolved_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setFocusable(false);
                String userId = holder.userId_unsolved_textview.getText().toString();
                //image
                StorageReference storageReferenceprofilepic = FirebaseStorage.getInstance().getReference();

                StorageReference imageref = storageReferenceprofilepic.child("DisplayPics/"+userId+".jpg");
                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //String imageUrl = uri.toString();

                        //all
                        DatabaseReference databaseReferenceall = FirebaseDatabase.getInstance().getReference()
                                .child("Registered Users").child(userId);
                        databaseReferenceall.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String name = snapshot.child("name").getValue().toString();
                                String email = snapshot.child("email").getValue().toString();
                                String mobile = snapshot.child("mobile").getValue().toString();
                                Log.i("reports"," outside 1 "+name);
                                Log.i("reports"," outside 1 "+email);
                                Log.i("reports"," outside 1 "+mobile);

                                String imageurl = String.valueOf(uri);
                                AlertDialog.Builder ImageDialog = new AlertDialog.Builder(view.getContext());
                                ImageDialog.setMessage("Email: "+email+"\nPhone No.: "+mobile);
                                ImageDialog.setTitle("Name: "+name);
                                ImageView showImage = new ImageView(view.getContext());
                                Glide.with(view.getContext()).load(imageurl).placeholder(R.drawable.ic_profile_loadimage)
                                        .into(showImage);
                                ImageDialog.setView(showImage);
                                ImageDialog.setCancelable(false);
                                ImageDialog.setNeutralButton("Email", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface arg0, int arg1)
                                    {
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setType("plain/text");
                                        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
                                        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback/Support - Fix My Road");
                                        intent.putExtra(Intent.EXTRA_TEXT, "Hi! "+name+"! How are you?");
                                        intent.setPackage("com.google.android.gm");
                                        ImageDialog.getContext().startActivity(Intent.createChooser(intent, ""));

                                    }
                                });

                                ImageDialog.setNegativeButton("Go Back", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface arg0, int arg1)
                                    {
                                    }
                                });
                                ImageDialog.show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });

            }
        });


    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reports_unsolved,parent,false);
        return new myviewholder(view);
    }

    public class myviewholder extends RecyclerView.ViewHolder{

        ImageView image_report_unsolved;
        TextView status_unsolved_textview, reportdate_unsolved_textview, longlati_unsolved_textview, address_unsolved_textview,
                description_unsolved_textview;
        TextView btn_report_unSolved,reportID_unsolved_textview, clicklocate_unsolved_textview, clickUser_unsolved_textview, userId_unsolved_textview;
        SpinKitView spinkit1;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            image_report_unsolved = itemView.findViewById(R.id.image_report_unsolved);
            status_unsolved_textview = itemView.findViewById(R.id.status_unsolved_textview);
            reportdate_unsolved_textview = itemView.findViewById(R.id.reportdate_unsolved_textview);
            longlati_unsolved_textview = itemView.findViewById(R.id.longlati_unsolved_textview);
            address_unsolved_textview = itemView.findViewById(R.id.address_unsolved_textview);
            description_unsolved_textview = itemView.findViewById(R.id.description_unsolved_textview);
            btn_report_unSolved = itemView.findViewById(R.id.btn_report_unSolved);
            reportID_unsolved_textview = itemView.findViewById(R.id.reportID_unsolved_textview);
            clicklocate_unsolved_textview = itemView.findViewById(R.id.clicklocate_unsolved_textview);
            clickUser_unsolved_textview = itemView.findViewById(R.id.clickUser_unsolved_textview);
            userId_unsolved_textview =itemView.findViewById(R.id.userId_unsolved_textview);

            spinkit1 = itemView.findViewById(R.id.spin_kit);
            Sprite circle = new Circle();
            spinkit1.setIndeterminateDrawable(circle);

        }
    }
}

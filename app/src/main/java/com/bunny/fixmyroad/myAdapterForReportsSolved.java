package com.bunny.fixmyroad;

import static java.security.AccessController.getContext;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developer.kalert.KAlertDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gurutouchlabs.kenneth.elegantdialog.ElegantDialog;

public class myAdapterForReportsSolved extends FirebaseRecyclerAdapter <ReadWriteReportSolvedUnsolvedDetails, myAdapterForReportsSolved.myviewholder>
{

    public myAdapterForReportsSolved(@NonNull FirebaseRecyclerOptions<ReadWriteReportSolvedUnsolvedDetails> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull ReadWriteReportSolvedUnsolvedDetails model) {

        holder.status_solved_textview.setText(model.Status);
        holder.reportdate_solved_textview.setText(model.ReportDate);
        holder.longlati_solved_textview.setText(model.LocationLatLon);
        holder.address_solved_textview.setText(model.LocationAddress);
        holder.description_solved_textview.setText(model.description);
        holder.btn_report_Solved.setText("Status: "+model.Status);
        holder.userId_solved_textview.setText(model.UserID);
        Glide.with(holder.image_report_solved.getContext()).load(model.reportImage).centerCrop().into(holder.image_report_solved);

        holder.clicklocate_solved_textview.setOnClickListener(new View.OnClickListener() {
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
                                String latlon = String.valueOf(holder.longlati_solved_textview.getText());
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

        holder.clickUser_solved_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setFocusable(false);
                String userId = holder.userId_solved_textview.getText().toString();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reports_solved,parent,false);
        return new myviewholder(view);
    }

    public class myviewholder extends RecyclerView.ViewHolder{

        ImageView image_report_solved;
        TextView status_solved_textview, reportdate_solved_textview, longlati_solved_textview, address_solved_textview,
                description_solved_textview;
        TextView btn_report_Solved, clicklocate_solved_textview, clickUser_solved_textview, userId_solved_textview;


        public myviewholder(@NonNull View itemView) {
            super(itemView);

            image_report_solved = itemView.findViewById(R.id.image_report_solved);
            status_solved_textview = itemView.findViewById(R.id.status_solved_textview);
            reportdate_solved_textview = itemView.findViewById(R.id.reportdate_solved_textview);
            longlati_solved_textview = itemView.findViewById(R.id.longlati_solved_textview);
            address_solved_textview = itemView.findViewById(R.id.address_solved_textview);
            description_solved_textview = itemView.findViewById(R.id.description_solved_textview);
            btn_report_Solved = itemView.findViewById(R.id.btn_report_Solved);
            clicklocate_solved_textview = itemView.findViewById(R.id.clicklocate_solved_textview);
            clickUser_solved_textview = itemView.findViewById(R.id.clickUser_solved_textview);
            userId_solved_textview = itemView.findViewById(R.id.userId_solved_textview);

        }
    }
}

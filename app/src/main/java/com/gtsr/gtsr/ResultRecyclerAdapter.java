/*
package com.gtsr.gtsr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gtsr.gtsr.database.TestFactors;
import com.gtsr.gtsr.database.UrineResultsDataController;
import com.gtsr.gtsr.database.UrineresultsModel;
import com.gtsr.gtsr.testModule.ResultPageViewController;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ResultRecyclerAdapter extends RecyclerView.Adapter<ResultRecyclerAdapter.ResultHolder> {
    Context context;
    int itemPosition =-1;
    ArrayList<TestFactors> testFactorsArrayList;

    public ResultRecyclerAdapter(Context context, ArrayList<TestFactors> testFactorsArrayList1) {
        this.context = context;
        this.testFactorsArrayList=testFactorsArrayList1;
    }

    @NonNull
    @Override
    public ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View resultView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_result_item, parent,
                false);
        return new ResultHolder(resultView);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultHolder holder, int position) {
        UrineresultsModel objTestFactors =UrineResultsDataController.getInstance().allUrineResults.get(position);
         holder.txtDate.setText(convertTimestampTodate(objTestFactors.getTestedTime()));
        if (itemPosition==position){
            holder.imgRound.setImageResource(R.drawable.check);
            HomeActivity.isFromHome=true;
            UrineResultsDataController.getInstance().currenturineresultsModel=UrineResultsDataController.getInstance().allUrineResults.get(itemPosition);
            context.startActivity(new Intent(context, ResultPageViewController.class));
        }else{
            holder.imgRound.setImageResource(R.drawable.ellipse);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(itemPosition != position){
                   itemPosition=position;
                   notifyDataSetChanged();
               }else{
                   itemPosition=-1;
                   notifyDataSetChanged();
               }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (UrineResultsDataController.getInstance().allUrineResults.size() > 0) {
            return UrineResultsDataController.getInstance().allUrineResults.size();
        } else {
            return 0;
        }
    }
    public class ResultHolder extends RecyclerView.ViewHolder {
        ImageView imgRound;
        TextView txtDate;
        public ResultHolder(@NonNull View itemView) {
            super(itemView);
            imgRound = itemView.findViewById(R.id.img_check);
            txtDate=itemView.findViewById(R.id.txtdate);
;
        }
    }
    public String convertTimestampTodate(String stringData) {
        long yourmilliseconds = Long.parseLong(stringData);
        SimpleDateFormat weekFormatter = new SimpleDateFormat("MMM dd,yyyy", Locale.ENGLISH);
        Date resultdate = new Date(yourmilliseconds * 1000);
        String weekString = weekFormatter.format(resultdate);
        return weekString;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void cameraBottomSheet() {
        TextView cam, gal, canc, file;
        View dialogView =getLayoutInflater().inflate(R.layout.file_dailog, null);
        final BottomSheetDialog cameraBottomSheetDialog = new BottomSheetDialog(Objects.requireNonNull(PatientProfileActivity.this), R.style.BottomSheetDialogTheme);
        cameraBottomSheetDialog.setContentView(dialogView);
        cam = cameraBottomSheetDialog.findViewById(R.id.camera);
        gal = cameraBottomSheetDialog.findViewById(R.id.gallery);
        file = cameraBottomSheetDialog.findViewById(R.id.file);
        canc = cameraBottomSheetDialog.findViewById(R.id.cancel);
        file.setVisibility(View.GONE);
        FrameLayout bottomSheet = (FrameLayout) cameraBottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        bottomSheet.setBackground(null);

        cameraBottomSheetDialog.show();
        canc.setTextColor(Color.parseColor("#53B9c6"));

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        canc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraBottomSheetDialog.cancel();
            }
        });

    }
}
*/

package com.gtsr.gtsr.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import com.gtsr.gtsr.Constants;
import com.gtsr.gtsr.HomeActivity;
import com.gtsr.gtsr.R;
import com.gtsr.gtsr.dataController.QubeController;
import com.gtsr.gtsr.database.TestFactorDataController;
import com.gtsr.gtsr.database.UrineResultsDataController;
import com.gtsr.gtsr.database.UrineresultsModel;
import com.gtsr.gtsr.qubeTestResults.QubeGraphResultActivity;
import com.gtsr.gtsr.qubeTestResults.QubeSampleReadyActivity;
import com.gtsr.gtsr.qubeTestResults.QubeTestResultActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;


public class AntiBodyTestAdapter extends RecyclerView.Adapter<AntiBodyTestAdapter.AntiBodyHolder> {
    Context context;
    int itemPosition = -1;
    public ArrayList<UrineresultsModel> tempUrineResults = new ArrayList<>();

    public AntiBodyTestAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AntiBodyTestAdapter.AntiBodyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_result_item, parent, false);
        return new AntiBodyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AntiBodyTestAdapter.AntiBodyHolder holder, int position) {
        UrineresultsModel objTestFactors = tempUrineResults.get(position);
        Log.e("objTestFactors","call"+objTestFactors.getUserName());

        holder.txtAntiBodyDate.setText(convertTimestampTodate(objTestFactors.getTestedTime()));
        holder.txtAntiBodyName.setText(objTestFactors.getUserName());

        if (itemPosition == position) {
            holder.imgCircle.setImageResource(R.drawable.check);
            HomeActivity.isFromHome = true;
            UrineResultsDataController.getInstance().currenturineresultsModel =tempUrineResults.get(itemPosition);
            context.startActivity(new Intent(context, QubeTestResultActivity.class));
        } else {
            holder.imgCircle.setImageResource(R.drawable.ellipse);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemPosition != position) {
                    itemPosition = position;
                    notifyDataSetChanged();
                } else {
                    itemPosition = -1;
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        loadResultData();
        if(tempUrineResults.size()>0){
            return tempUrineResults.size();
        }else {
            return 0;
        }
    }

    public class AntiBodyHolder extends RecyclerView.ViewHolder {
        ImageView imgCircle;
        TextView txtAntiBodyDate, txtAntiBodyName;

        public AntiBodyHolder(@NonNull View itemView) {
            super(itemView);
            imgCircle = itemView.findViewById(R.id.img_check);
            txtAntiBodyDate = itemView.findViewById(R.id.txtdate);
            txtAntiBodyName = itemView.findViewById(R.id.txtname);
        }
    }
    public String convertTimestampTodate(String stringData) {
        long yourmilliseconds = Long.parseLong(stringData);
        SimpleDateFormat weekFormatter = new SimpleDateFormat("MMM dd,yyyy", Locale.ENGLISH);
        Date resultdate = new Date(yourmilliseconds * 1000);
        String weekString = weekFormatter.format(resultdate);
        return weekString;
    }
    private void loadResultData() {
        tempUrineResults.clear();
        if (UrineResultsDataController.getInstance().allUrineResults.size() > 0) {
            for (int i = 0; i < UrineResultsDataController.getInstance().allUrineResults.size(); i++) {
                UrineresultsModel urineresultsModel = UrineResultsDataController.getInstance().allUrineResults.get(i);
                if (urineresultsModel.getTestType().contains(Constants.TestNames.qube.toString())) {
                    tempUrineResults.add(urineresultsModel);
                }
            }
            Log.e("zxcvvvvv","call"+tempUrineResults.size());

        }
    }
}

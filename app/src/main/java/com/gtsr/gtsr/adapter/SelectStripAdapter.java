package com.gtsr.gtsr.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gtsr.gtsr.R;
import com.gtsr.gtsr.testModule.SelectTestStripActivity;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SelectStripAdapter extends RecyclerView.Adapter<SelectStripAdapter.StripHolder> {
    Context context;

    public SelectStripAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public StripHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_strip_itemview, parent, false);
        return new StripHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StripHolder holder, int position) {
       holder.testName.setText(/*"CUBE_Covid19 Test strip"*/"i-Mitox SARS-CoV2 Urine Antibody Test Strip");
        if (SelectTestStripActivity.selectedPosition == position) {
            Log.e("if", "" + position);
            holder.image.setVisibility(View.VISIBLE);
        } else {
            Log.e("else", "" + position);
            holder.image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectTestStripActivity.selectedPosition != position) {
                    SelectTestStripActivity.selectedPosition = position;
                    notifyDataSetChanged();
                } else {
                    SelectTestStripActivity.selectedPosition = -1;
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class StripHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView testName;
        public StripHolder(@NonNull View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            testName = (TextView) itemView.findViewById(R.id.testName);


        }
    }
}

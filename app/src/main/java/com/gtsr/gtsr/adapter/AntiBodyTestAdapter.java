package com.gtsr.gtsr.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gtsr.gtsr.HomeActivity;
import com.gtsr.gtsr.R;
import com.gtsr.gtsr.database.UrineResultsDataController;
import com.gtsr.gtsr.qubeTestResults.QubeGraphResultActivity;
import com.gtsr.gtsr.qubeTestResults.QubeSampleReadyActivity;
import com.gtsr.gtsr.qubeTestResults.QubeTestResultActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AntiBodyTestAdapter extends RecyclerView.Adapter<AntiBodyTestAdapter.AntiBodyHolder> {
    Context context;
    int itemPosition = -1;

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

        holder.txtAntiBodyName.setText("Andrea COV-Antibody Analysis");

        if (itemPosition == position) {
            holder.imgCircle.setImageResource(R.drawable.check);
            HomeActivity.isFromHome=true;
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
        return 3;
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
}

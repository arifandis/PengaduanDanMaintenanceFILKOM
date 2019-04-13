package com.example.hanum.skripsi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hanum.skripsi.R;
import com.example.hanum.skripsi.model.LaporanMaintenanceModel;
import com.example.hanum.skripsi.model.LaporanRatingModel;

import java.util.List;

public class LaporanRatingRecyclerAdapter extends RecyclerView.Adapter<LaporanRatingRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<LaporanRatingModel> laporanRatingModels;

    public LaporanRatingRecyclerAdapter(Context context, List<LaporanRatingModel> laporanRatingModels) {
        this.context = context;
        this.laporanRatingModels = laporanRatingModels;
    }

    @NonNull
    @Override
    public LaporanRatingRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_laporan_rating,parent,false);
        return new LaporanRatingRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LaporanRatingRecyclerAdapter.ViewHolder holder, int position) {
        LaporanRatingModel laporanRatingModel = laporanRatingModels.get(position);

        holder.tvNomor.setText(laporanRatingModel.getNomor());
        holder.tvNama.setText(laporanRatingModel.getNama());

        float fixRating = 0;
        if (laporanRatingModel.getCount() != 0){
            fixRating = laporanRatingModel.getRating()/laporanRatingModel.getCount();
        }
        holder.tvRating.setText(String.valueOf(fixRating));
    }

    @Override
    public int getItemCount() {
        return laporanRatingModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomor,tvNama,tvRating;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNomor = itemView.findViewById(R.id.itemLaporanRating_nomorTv);
            tvNama = itemView.findViewById(R.id.itemLaporanRating_namaTv);
            tvRating = itemView.findViewById(R.id.itemLaporanRating_ratingTv);
        }
    }
}

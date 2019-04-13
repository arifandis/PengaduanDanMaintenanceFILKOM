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

import java.util.List;

public class LaporanMaintenanceRecyclerAdapter extends RecyclerView.Adapter<LaporanMaintenanceRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<LaporanMaintenanceModel> laporanMaintenanceModels;

    public LaporanMaintenanceRecyclerAdapter(Context context, List<LaporanMaintenanceModel> laporanMaintenanceModels) {
        this.context = context;
        this.laporanMaintenanceModels = laporanMaintenanceModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_laporan_maintenance,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LaporanMaintenanceModel laporanMaintenanceModel = laporanMaintenanceModels.get(position);

        holder.tvNoInventaris.setText(laporanMaintenanceModel.getNoInventaris());
        holder.tvSkala.setText(laporanMaintenanceModel.getSkala());
        holder.tvWaktuPelaksanaan.setText(laporanMaintenanceModel.getWaktuPelaksanaan());
    }

    @Override
    public int getItemCount() {
        return laporanMaintenanceModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNoInventaris,tvSkala,tvWaktuPelaksanaan;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNoInventaris = itemView.findViewById(R.id.itemLaporan_noInvetarisTv);
            tvSkala = itemView.findViewById(R.id.itemLaporan_skalaTv);
            tvWaktuPelaksanaan = itemView.findViewById(R.id.itemLaporan_waktuPelaksanaanTv);
        }
    }
}

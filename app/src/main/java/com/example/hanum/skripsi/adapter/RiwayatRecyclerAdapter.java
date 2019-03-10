package com.example.hanum.skripsi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hanum.skripsi.R;
import com.example.hanum.skripsi.model.Maintenance;

import java.util.List;

public class RiwayatRecyclerAdapter extends RecyclerView.Adapter<RiwayatRecyclerAdapter.RiwayatViewHolder> {
    private Context context;
    private List<Maintenance> maintenances;

    public RiwayatRecyclerAdapter(Context context, List<Maintenance> maintenances) {
        this.context = context;
        this.maintenances = maintenances;
    }

    @Override
    public RiwayatRecyclerAdapter.RiwayatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_riwayat,parent,false);
        return new RiwayatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RiwayatRecyclerAdapter.RiwayatViewHolder holder, int position) {
        Maintenance maintenance = maintenances.get(position);

        holder.tvTanggal.setText(maintenance.getTanggal());
    }

    @Override
    public int getItemCount() {
        return maintenances.size();
    }

    class RiwayatViewHolder extends RecyclerView.ViewHolder{
        TextView tvTanggal,tvOleh;

        public RiwayatViewHolder(View itemView) {
            super(itemView);

            tvTanggal = itemView.findViewById(R.id.itemRiwayat_tanggalTv);
            tvOleh = itemView.findViewById(R.id.itemRiwayat_olehTv);
        }
    }
}

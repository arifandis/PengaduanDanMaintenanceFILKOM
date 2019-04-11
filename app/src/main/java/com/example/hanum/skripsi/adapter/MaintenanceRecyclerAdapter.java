package com.example.hanum.skripsi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hanum.skripsi.R;
import com.example.hanum.skripsi.RiwayatMaintenance;
import com.example.hanum.skripsi.model.Maintenance;

import java.util.List;

public class MaintenanceRecyclerAdapter extends RecyclerView.Adapter<MaintenanceRecyclerAdapter.RiwayatViewHolder> {
    private Context context;
    private List<Maintenance> maintenances;

    public MaintenanceRecyclerAdapter(Context context, List<Maintenance> maintenances) {
        this.context = context;
        this.maintenances = maintenances;
    }

    @Override
    public MaintenanceRecyclerAdapter.RiwayatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_maintenance,parent,false);
        return new RiwayatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MaintenanceRecyclerAdapter.RiwayatViewHolder holder, int position) {
        Maintenance maintenance = maintenances.get(position);

        holder.tvInventaris.setText(maintenance.getKategori()+" "+maintenance.getNomor());
        holder.tvInventaris.setOnClickListener(v -> {
            Intent intent = new Intent(context, RiwayatMaintenance.class);
            intent.putExtra("idMaintenance",maintenance.getIdMaintenance());
            intent.putExtra("noInventaris",maintenance.getKategori()+" "+maintenance.getNomor());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return maintenances.size();
    }

    class RiwayatViewHolder extends RecyclerView.ViewHolder{
        TextView tvInventaris;

        public RiwayatViewHolder(View itemView) {
            super(itemView);

            tvInventaris = itemView.findViewById(R.id.itemMaintenance_inventarisTv);
        }
    }
}

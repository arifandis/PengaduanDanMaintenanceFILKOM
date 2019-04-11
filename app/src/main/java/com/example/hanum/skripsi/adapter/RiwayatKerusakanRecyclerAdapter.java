package com.example.hanum.skripsi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hanum.skripsi.R;
import com.example.hanum.skripsi.model.Kerusakan;

import java.util.List;

public class RiwayatKerusakanRecyclerAdapter extends RecyclerView.Adapter<RiwayatKerusakanRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Kerusakan> kerusakans;

    public RiwayatKerusakanRecyclerAdapter(Context context, List<Kerusakan> kerusakans) {
        this.context = context;
        this.kerusakans = kerusakans;
    }

    @NonNull
    @Override
    public RiwayatKerusakanRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_kerusakan,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RiwayatKerusakanRecyclerAdapter.ViewHolder holder, int position) {
        Kerusakan kerusakan = kerusakans.get(position);

        holder.tvTanggalDiterima.setText("Tanggal Diterima : "+kerusakan.getTanggalDiterima());
        holder.tvTanggalSelesai.setText("Tanggal Selesai : "+kerusakan.getTanggalSelesai());
        holder.tvOleh.setText("Oleh : "+kerusakan.getNamaPegawai());

    }

    @Override
    public int getItemCount() {
        return kerusakans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTanggalDiterima,tvTanggalSelesai,tvOleh;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTanggalDiterima = itemView.findViewById(R.id.itemKerusakan_tglDiterimaTv);
            tvTanggalSelesai = itemView.findViewById(R.id.itemKerusakan_tglSelesaiTv);
            tvOleh = itemView.findViewById(R.id.itemKerusakan_olehTv);
        }
    }
}

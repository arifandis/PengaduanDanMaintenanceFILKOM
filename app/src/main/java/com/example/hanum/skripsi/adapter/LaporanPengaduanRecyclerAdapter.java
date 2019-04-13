package com.example.hanum.skripsi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hanum.skripsi.R;
import com.example.hanum.skripsi.model.LaporanPengaduanModel;

import java.util.List;

public class LaporanPengaduanRecyclerAdapter extends RecyclerView.Adapter<LaporanPengaduanRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<LaporanPengaduanModel> pengaduanList;

    public LaporanPengaduanRecyclerAdapter(Context context, List<LaporanPengaduanModel> pengaduanList) {
        this.context = context;
        this.pengaduanList = pengaduanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_laporan_pengaduan,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LaporanPengaduanModel pengaduanModel = pengaduanList.get(position);

        holder.tvJudul.setText(pengaduanModel.getJudul());
        holder.tvStatus.setText(pengaduanModel.getStatus());
        holder.tvTanggalMulai.setText(pengaduanModel.getTanggalMulai());
        holder.tvTanggalSelesai.setText(pengaduanModel.getTanggalSelesai());
    }

    @Override
    public int getItemCount() {
        return pengaduanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudul,tvStatus,tvTanggalMulai,tvTanggalSelesai;

        ViewHolder(View itemView) {
            super(itemView);

            tvJudul = itemView.findViewById(R.id.itemLaporanPengaduan_JudulTv);
            tvStatus = itemView.findViewById(R.id.itemLaporanPengaduan_StatusTv);
            tvTanggalMulai = itemView.findViewById(R.id.itemLaporanPengaduan_TglMulaiTv);
            tvTanggalSelesai = itemView.findViewById(R.id.itemLaporanPengaduan_TglSelesaiTv);
        }
    }
}

package com.example.hanum.skripsi.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanum.skripsi.DetailPengaduan;
import com.example.hanum.skripsi.R;
import com.example.hanum.skripsi.model.Pengaduan;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PengaduanRecyclerAdapter extends RecyclerView.Adapter<PengaduanRecyclerAdapter.PengaduanViewHolder> {
    private Context context;
    private List<Pengaduan> pengaduans;

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    public PengaduanRecyclerAdapter(Context context, List<Pengaduan> pengaduans) {
        this.context = context;
        this.pengaduans = pengaduans;
    }

    @NonNull
    @Override
    public PengaduanRecyclerAdapter.PengaduanViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pengaduan,viewGroup,false);
        return new PengaduanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PengaduanRecyclerAdapter.PengaduanViewHolder pengaduanViewHolder, int i) {
        final Pengaduan pengaduan = pengaduans.get(i);

        pengaduanViewHolder.tvTanggal.setText(pengaduan.getTanggalMasuk());
        pengaduanViewHolder.tvDeskripsi.setText("Deskripsi: "+pengaduan.getKerusakan());
        pengaduanViewHolder.tvStatus.setText(pengaduan.getStatus());
        if (pengaduan.getStatus().equalsIgnoreCase("belum diterima")){
            pengaduanViewHolder.tvStatus.setBackgroundResource(R.drawable.bg_belum_diproses);
        }else if (pengaduan.getStatus().equalsIgnoreCase("sedang diproses")){
            pengaduanViewHolder.tvStatus.setBackgroundResource(R.drawable.bg_diproses);
        }else if (pengaduan.getStatus().equalsIgnoreCase("diterima")){
            pengaduanViewHolder.tvStatus.setBackgroundResource(R.drawable.bg_diterima);
        }else if (pengaduan.getStatus().equalsIgnoreCase("sedang di vendor")){
            pengaduanViewHolder.tvStatus.setBackgroundResource(R.drawable.bg_divendor);
        }else if (pengaduan.getStatus().equalsIgnoreCase("selesai")){
            pengaduanViewHolder.tvStatus.setBackgroundResource(R.drawable.bg_selesai);
        }

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String namaBarang = "";

                if (pengaduan.getIdBarang().contains("barang")){
                    DataSnapshot data = dataSnapshot.child("barang").child(pengaduan.getIdBarang());
                    String noInventaris = data.child("noInventaris").getValue(String.class);
                    namaBarang = data.child("nama").getValue(String.class)+" "+noInventaris;
                }else if (pengaduan.getIdBarang().contains("bersih")){
                    DataSnapshot data = dataSnapshot.child("bersih").child(pengaduan.getIdBarang());
                    namaBarang = data.child("nama").getValue(String.class);
                }else if (pengaduan.getIdBarang().contains("taman")){
                    DataSnapshot data = dataSnapshot.child("taman").child(pengaduan.getIdBarang());
                    namaBarang = data.child("nama").getValue(String.class);
                }else if (pengaduan.getIdBarang().contains("ruang")){
                    DataSnapshot data = dataSnapshot.child("ruang").child(pengaduan.getIdBarang());
                    namaBarang = data.child("nama").getValue(String.class);
                }
                pengaduanViewHolder.tvNamaBarang.setText(namaBarang);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        pengaduanViewHolder.itemCard.setOnClickListener(v -> {
            Intent intent = new Intent(context,DetailPengaduan.class);
            intent.putExtra("idPengaduan", pengaduan.getIdPengaduan());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pengaduans.size();
    }

    class PengaduanViewHolder extends RecyclerView.ViewHolder{
        CardView itemCard;
        TextView tvTanggal,tvNamaBarang,tvDeskripsi,tvStatus;

        public PengaduanViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCard = itemView.findViewById(R.id.itemPengaduan_itemCard);
            tvTanggal = itemView.findViewById(R.id.itemPengaduan_tanggalTv);
            tvNamaBarang = itemView.findViewById(R.id.itemPengaduan_namaTv);
            tvDeskripsi = itemView.findViewById(R.id.itemPengaduan_deskripsiTv);
            tvStatus = itemView.findViewById(R.id.itemPengaduan_statustv);
        }
    }
}

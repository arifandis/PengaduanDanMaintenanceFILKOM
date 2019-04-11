package com.example.hanum.skripsi;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanum.skripsi.adapter.PengaduanRecyclerAdapter;
import com.example.hanum.skripsi.model.Pengaduan;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DaftarPengaduanBarang extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvKosong;
    private Button btnFilter;
    private ProgressBar progressBar;
    private RadioGroup radioGroup;

    private PengaduanRecyclerAdapter adapter;
    private List<Pengaduan> pengaduanList = new ArrayList<>();

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private Dialog dialogFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_pengaduan_barang);

        recyclerView = findViewById(R.id.daftarPengaduanBarang_recyclerView);
        tvKosong = findViewById(R.id.daftarPengaduanBarang_labelKosongTv);
        btnFilter = findViewById(R.id.daftarPengaduanBarang_filterBtn);
        progressBar = findViewById(R.id.daftarPengaduanBarang_progressBar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        adapter = new PengaduanRecyclerAdapter(this,pengaduanList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        //Filter Dialog
        dialogFilter = new Dialog(this);
        dialogFilter.setContentView(R.layout.dialog_filter);
        dialogFilter.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFilter.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
        radioGroup = dialogFilter.findViewById(R.id.dialogFilter_radioGroup);

        showPengaduan();

        btnFilter.setOnClickListener(v->{
            dialogFilter.show();
        });

        filter();
    }

    public void showPengaduan(){
        progressBar.setVisibility(View.VISIBLE);
        mRef.child("pengaduan").orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pengaduanList.clear();
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    String idBarang = data.child("idBarang").getValue(String.class);
                    if (idBarang.contains("barang")){
                        Pengaduan pengaduan = new Pengaduan();
                        pengaduan.setFoto(data.child("foto").getValue(String.class));
                        pengaduan.setIdBarang(data.child("idBarang").getValue(String.class));
                        pengaduan.setIdPengadu(data.child("idPengadu").getValue(String.class));
                        pengaduan.setIdPengaduan(data.getKey());
                        pengaduan.setKerusakan(data.child("kerusakan").getValue(String.class));
                        pengaduan.setLokasi(data.child("lokasi").getValue(String.class));
                        pengaduan.setStatus(data.child("status").getValue(String.class));
                        pengaduan.setTanggalMasuk(data.child("tanggalMasuk").getValue(String.class));
                        pengaduan.setTanggalSelesai(data.child("tanggalSelesai").getValue(String.class));

                        pengaduanList.add(pengaduan);
                        adapter.notifyDataSetChanged();
                    }
                }

                progressBar.setVisibility(View.GONE);
                if (pengaduanList.isEmpty()){
                    recyclerView.setVisibility(View.GONE);
                    tvKosong.setVisibility(View.VISIBLE);
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    tvKosong.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void filter(){
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.dialogFilter_blmDiterimaBtn:
                    pengaduanList.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    mRef.child("pengaduan").orderByChild("id")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Pengaduan pengaduan = new Pengaduan();
                                    for (DataSnapshot data: dataSnapshot.getChildren()){
                                        String status = data.child("status").getValue(String.class);
                                        String idBarang = data.child("idBarang").getValue(String.class);
                                        if (status.equals("belum diterima") && idBarang.contains("barang")){
                                            pengaduan.setFoto(data.child("foto").getValue(String.class));
                                            pengaduan.setIdBarang(data.child("idBarang").getValue(String.class));
                                            pengaduan.setIdPengadu(data.child("idPengadu").getValue(String.class));
                                            pengaduan.setIdPengaduan(data.getKey());
                                            pengaduan.setKerusakan(data.child("kerusakan").getValue(String.class));
                                            pengaduan.setLokasi(data.child("lokasi").getValue(String.class));
                                            pengaduan.setStatus(status);
                                            pengaduan.setTanggalMasuk(data.child("tanggalMasuk").getValue(String.class));
                                            pengaduan.setTanggalSelesai(data.child("tanggalSelesai").getValue(String.class));

                                            pengaduanList.add(pengaduan);
                                            adapter.notifyDataSetChanged();
                                        }

                                    }

                                    progressBar.setVisibility(View.GONE);
                                    if (pengaduanList.isEmpty()){
                                        recyclerView.setVisibility(View.GONE);
                                        tvKosong.setVisibility(View.VISIBLE);
                                    }else{
                                        recyclerView.setVisibility(View.VISIBLE);
                                        tvKosong.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                    dialogFilter.dismiss();
                    break;
                case R.id.dialogFilter_diterimaBtn:
                    pengaduanList.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    mRef.child("pengaduan").orderByChild("id")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Pengaduan pengaduan = new Pengaduan();
                                    for (DataSnapshot data: dataSnapshot.getChildren()){
                                        String status = data.child("status").getValue(String.class);
                                        String idBarang = data.child("idBarang").getValue(String.class);
                                        if (status.equals("diterima") && idBarang.contains("barang")){
                                            pengaduan.setFoto(data.child("foto").getValue(String.class));
                                            pengaduan.setIdBarang(data.child("idBarang").getValue(String.class));
                                            pengaduan.setIdPengadu(data.child("idPengadu").getValue(String.class));
                                            pengaduan.setIdPengaduan(data.getKey());
                                            pengaduan.setKerusakan(data.child("kerusakan").getValue(String.class));
                                            pengaduan.setLokasi(data.child("lokasi").getValue(String.class));
                                            pengaduan.setStatus(status);
                                            pengaduan.setTanggalMasuk(data.child("tanggalMasuk").getValue(String.class));
                                            pengaduan.setTanggalSelesai(data.child("tanggalSelesai").getValue(String.class));

                                            pengaduanList.add(pengaduan);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }

                                    progressBar.setVisibility(View.GONE);
                                    if (pengaduanList.isEmpty()){
                                        recyclerView.setVisibility(View.GONE);
                                        tvKosong.setVisibility(View.VISIBLE);
                                    }else{
                                        recyclerView.setVisibility(View.VISIBLE);
                                        tvKosong.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                    dialogFilter.dismiss();
                    break;
                case R.id.dialogFilter_diprosesBtn:
                    pengaduanList.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    mRef.child("pengaduan").orderByChild("id")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Pengaduan pengaduan = new Pengaduan();
                                    for (DataSnapshot data: dataSnapshot.getChildren()){
                                        String status = data.child("status").getValue(String.class);
                                        String idBarang = data.child("idBarang").getValue(String.class);
                                        if (status.equals("sedang diproses") && idBarang.contains("barang")){
                                            pengaduan.setFoto(data.child("foto").getValue(String.class));
                                            pengaduan.setIdBarang(data.child("idBarang").getValue(String.class));
                                            pengaduan.setIdPengadu(data.child("idPengadu").getValue(String.class));
                                            pengaduan.setIdPengaduan(data.getKey());
                                            pengaduan.setKerusakan(data.child("kerusakan").getValue(String.class));
                                            pengaduan.setLokasi(data.child("lokasi").getValue(String.class));
                                            pengaduan.setStatus(status);
                                            pengaduan.setTanggalMasuk(data.child("tanggalMasuk").getValue(String.class));
                                            pengaduan.setTanggalSelesai(data.child("tanggalSelesai").getValue(String.class));

                                            pengaduanList.add(pengaduan);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }

                                    progressBar.setVisibility(View.GONE);
                                    if (pengaduanList.isEmpty()){
                                        recyclerView.setVisibility(View.GONE);
                                        tvKosong.setVisibility(View.VISIBLE);
                                    }else{
                                        recyclerView.setVisibility(View.VISIBLE);
                                        tvKosong.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                    dialogFilter.dismiss();
                    break;
                case R.id.dialogFilter_divendorBtn:
                    pengaduanList.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    mRef.child("pengaduan").orderByChild("id")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Pengaduan pengaduan = new Pengaduan();
                                    for (DataSnapshot data: dataSnapshot.getChildren()){
                                        String status = data.child("status").getValue(String.class);
                                        String idBarang = data.child("idBarang").getValue(String.class);
                                        if (status.equals("sedang divendor") && idBarang.contains("barang")){
                                            pengaduan.setFoto(data.child("foto").getValue(String.class));
                                            pengaduan.setIdBarang(data.child("idBarang").getValue(String.class));
                                            pengaduan.setIdPengadu(data.child("idPengadu").getValue(String.class));
                                            pengaduan.setIdPengaduan(data.getKey());
                                            pengaduan.setKerusakan(data.child("kerusakan").getValue(String.class));
                                            pengaduan.setLokasi(data.child("lokasi").getValue(String.class));
                                            pengaduan.setStatus(status);
                                            pengaduan.setTanggalMasuk(data.child("tanggalMasuk").getValue(String.class));
                                            pengaduan.setTanggalSelesai(data.child("tanggalSelesai").getValue(String.class));

                                            pengaduanList.add(pengaduan);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }

                                    progressBar.setVisibility(View.GONE);
                                    if (pengaduanList.isEmpty()){
                                        recyclerView.setVisibility(View.GONE);
                                        tvKosong.setVisibility(View.VISIBLE);
                                    }else{
                                        recyclerView.setVisibility(View.VISIBLE);
                                        tvKosong.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                    dialogFilter.dismiss();
                    break;
                case R.id.dialogFilter_selesaiBtn:
                    pengaduanList.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    mRef.child("pengaduan").orderByChild("id")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Pengaduan pengaduan = new Pengaduan();
                                    for (DataSnapshot data: dataSnapshot.getChildren()){
                                        String status = data.child("status").getValue(String.class);
                                        String idBarang = data.child("idBarang").getValue(String.class);
                                        if (status.equals("selesai") && idBarang.contains("barang")){
                                            pengaduan.setFoto(data.child("foto").getValue(String.class));
                                            pengaduan.setIdBarang(data.child("idBarang").getValue(String.class));
                                            pengaduan.setIdPengadu(data.child("idPengadu").getValue(String.class));
                                            pengaduan.setIdPengaduan(data.getKey());
                                            pengaduan.setKerusakan(data.child("kerusakan").getValue(String.class));
                                            pengaduan.setLokasi(data.child("lokasi").getValue(String.class));
                                            pengaduan.setStatus(status);
                                            pengaduan.setTanggalMasuk(data.child("tanggalMasuk").getValue(String.class));
                                            pengaduan.setTanggalSelesai(data.child("tanggalSelesai").getValue(String.class));

                                            pengaduanList.add(pengaduan);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }

                                    progressBar.setVisibility(View.GONE);
                                    if (pengaduanList.isEmpty()){
                                        recyclerView.setVisibility(View.GONE);
                                        tvKosong.setVisibility(View.VISIBLE);
                                    }else{
                                        recyclerView.setVisibility(View.VISIBLE);
                                        tvKosong.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                    dialogFilter.dismiss();
                    break;
            }
        });
    }
}

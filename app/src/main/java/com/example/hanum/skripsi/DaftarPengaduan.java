package com.example.hanum.skripsi;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DaftarPengaduan extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvKosong;
    private Button btnFilter;
    private RadioGroup radioGroup;
    private PengaduanRecyclerAdapter adapter;
    private List<Pengaduan> pengaduanList = new ArrayList<>();

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private Dialog dialogFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_pengaduan);

        recyclerView = findViewById(R.id.daftarPengaduan_recycler);
        tvKosong = findViewById(R.id.daftarPengaduan_labelKosongTv);
        btnFilter = findViewById(R.id.daftarPengaduan_filterBtn);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        adapter = new PengaduanRecyclerAdapter(this,pengaduanList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        //Filter Dialog
        dialogFilter = new Dialog(this);
        dialogFilter.setContentView(R.layout.dialog_filter);
        dialogFilter.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFilter.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
        radioGroup = dialogFilter.findViewById(R.id.dialogFilter_radioGroup);

        btnFilter.setOnClickListener(v->{
            dialogFilter.show();
        });

        filter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showPengaduan();
    }

    public void showPengaduan(){
        mRef.child("pengaduan").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pengaduanList.clear();
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    Pengaduan pengaduan = new Pengaduan();
                    pengaduan.setFoto(data.child("foto").getValue(String.class));
                    pengaduan.setIdBarang(data.child("idBarang").getValue(String.class));
                    pengaduan.setIdPengadu(data.child("idPengadu").getValue(String.class));
                    pengaduan.setIdPengaduan(data.child("idPengaduan").getValue(String.class));
                    pengaduan.setKerusakan(data.child("kerusakan").getValue(String.class));
                    pengaduan.setLokasi(data.child("lokasi").getValue(String.class));
                    pengaduan.setStatus(data.child("status").getValue(String.class));
                    pengaduan.setTanggalMasuk(data.child("tanggalMasuk").getValue(String.class));
                    pengaduan.setTanggalSelesai(data.child("tanggalSelesai").getValue(String.class));

                    pengaduanList.add(pengaduan);
                    adapter.notifyDataSetChanged();
                }

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
                Toast.makeText(DaftarPengaduan.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void filter(){
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.dialogFilter_blmDiterimaBtn:
                    pengaduanList.clear();
                    mRef.child("pengaduan").orderByChild("status").equalTo("belum diterima")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data: dataSnapshot.getChildren()){
                                        Pengaduan pengaduan = new Pengaduan();
                                        pengaduan.setFoto(data.child("foto").getValue(String.class));
                                        pengaduan.setIdBarang(data.child("idBarang").getValue(String.class));
                                        pengaduan.setIdPengadu(data.child("idPengadu").getValue(String.class));
                                        pengaduan.setIdPengaduan(data.child("idPengaduan").getValue(String.class));
                                        pengaduan.setKerusakan(data.child("kerusakan").getValue(String.class));
                                        pengaduan.setLokasi(data.child("lokasi").getValue(String.class));
                                        pengaduan.setStatus(data.child("status").getValue(String.class));
                                        pengaduan.setTanggalMasuk(data.child("tanggalMasuk").getValue(String.class));
                                        pengaduan.setTanggalSelesai(data.child("tanggalSelesai").getValue(String.class));

                                        pengaduanList.add(pengaduan);
                                        adapter.notifyDataSetChanged();
                                    }

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
                    mRef.child("pengaduan").orderByChild("status").equalTo("diterima")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data: dataSnapshot.getChildren()){
                                        Pengaduan pengaduan = new Pengaduan();
                                        pengaduan.setFoto(data.child("foto").getValue(String.class));
                                        pengaduan.setIdBarang(data.child("idBarang").getValue(String.class));
                                        pengaduan.setIdPengadu(data.child("idPengadu").getValue(String.class));
                                        pengaduan.setIdPengaduan(data.child("idPengaduan").getValue(String.class));
                                        pengaduan.setKerusakan(data.child("kerusakan").getValue(String.class));
                                        pengaduan.setLokasi(data.child("lokasi").getValue(String.class));
                                        pengaduan.setStatus(data.child("status").getValue(String.class));
                                        pengaduan.setTanggalMasuk(data.child("tanggalMasuk").getValue(String.class));
                                        pengaduan.setTanggalSelesai(data.child("tanggalSelesai").getValue(String.class));

                                        pengaduanList.add(pengaduan);
                                        adapter.notifyDataSetChanged();
                                    }

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
                    mRef.child("pengaduan").orderByChild("status").equalTo("sedang diproses")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data: dataSnapshot.getChildren()){
                                        Pengaduan pengaduan = new Pengaduan();
                                        pengaduan.setFoto(data.child("foto").getValue(String.class));
                                        pengaduan.setIdBarang(data.child("idBarang").getValue(String.class));
                                        pengaduan.setIdPengadu(data.child("idPengadu").getValue(String.class));
                                        pengaduan.setIdPengaduan(data.child("idPengaduan").getValue(String.class));
                                        pengaduan.setKerusakan(data.child("kerusakan").getValue(String.class));
                                        pengaduan.setLokasi(data.child("lokasi").getValue(String.class));
                                        pengaduan.setStatus(data.child("status").getValue(String.class));
                                        pengaduan.setTanggalMasuk(data.child("tanggalMasuk").getValue(String.class));
                                        pengaduan.setTanggalSelesai(data.child("tanggalSelesai").getValue(String.class));

                                        pengaduanList.add(pengaduan);
                                        adapter.notifyDataSetChanged();
                                    }

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
                    pengaduanList.clear();
                    mRef.child("pengaduan").orderByChild("status").equalTo("sedang divendor")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data: dataSnapshot.getChildren()){
                                        Pengaduan pengaduan = new Pengaduan();
                                        pengaduan.setFoto(data.child("foto").getValue(String.class));
                                        pengaduan.setIdBarang(data.child("idBarang").getValue(String.class));
                                        pengaduan.setIdPengadu(data.child("idPengadu").getValue(String.class));
                                        pengaduan.setIdPengaduan(data.child("idPengaduan").getValue(String.class));
                                        pengaduan.setKerusakan(data.child("kerusakan").getValue(String.class));
                                        pengaduan.setLokasi(data.child("lokasi").getValue(String.class));
                                        pengaduan.setStatus(data.child("status").getValue(String.class));
                                        pengaduan.setTanggalMasuk(data.child("tanggalMasuk").getValue(String.class));
                                        pengaduan.setTanggalSelesai(data.child("tanggalSelesai").getValue(String.class));

                                        pengaduanList.add(pengaduan);
                                        adapter.notifyDataSetChanged();
                                    }

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
                    pengaduanList.clear();
                    mRef.child("pengaduan").orderByChild("status").equalTo("selesai")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data: dataSnapshot.getChildren()){
                                        Pengaduan pengaduan = new Pengaduan();
                                        pengaduan.setFoto(data.child("foto").getValue(String.class));
                                        pengaduan.setIdBarang(data.child("idBarang").getValue(String.class));
                                        pengaduan.setIdPengadu(data.child("idPengadu").getValue(String.class));
                                        pengaduan.setIdPengaduan(data.child("idPengaduan").getValue(String.class));
                                        pengaduan.setKerusakan(data.child("kerusakan").getValue(String.class));
                                        pengaduan.setLokasi(data.child("lokasi").getValue(String.class));
                                        pengaduan.setStatus(data.child("status").getValue(String.class));
                                        pengaduan.setTanggalMasuk(data.child("tanggalMasuk").getValue(String.class));
                                        pengaduan.setTanggalSelesai(data.child("tanggalSelesai").getValue(String.class));

                                        pengaduanList.add(pengaduan);
                                        adapter.notifyDataSetChanged();
                                    }

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

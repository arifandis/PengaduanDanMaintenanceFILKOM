package com.example.hanum.skripsi;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.hanum.skripsi.adapter.PengaduanRecyclerAdapter;
import com.example.hanum.skripsi.model.Pengaduan;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TugasSaya extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvKosong;
    private Button btnFilter;
    private ProgressBar progressBar;
    private RadioGroup radioGroup;

    private PengaduanRecyclerAdapter adapter;
    private List<Pengaduan> pengaduanList = new ArrayList<>();

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private Dialog dialogFilter;

    private String PREF_NAME = "SkripsiPrefs",nipnim;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tugas_saya);

        recyclerView = findViewById(R.id.tugasSaya_recyclerView);
        tvKosong = findViewById(R.id.tugasSaya_labelKosongTv);
        btnFilter = findViewById(R.id.tugasSaya_filterBtn);
        progressBar = findViewById(R.id.tugasSaya_progressBar);

        preferences = getSharedPreferences(PREF_NAME,MODE_PRIVATE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        adapter = new PengaduanRecyclerAdapter(this,pengaduanList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        //Filter Dialog
        dialogFilter = new Dialog(this);
        dialogFilter.setContentView(R.layout.dialog_filter_tugas_saya);
        dialogFilter.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFilter.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
        radioGroup = dialogFilter.findViewById(R.id.dialogFilterTugasSaya_radioGroup);

        nipnim = preferences.getString("id",null);
        showPengaduan();

        btnFilter.setOnClickListener(v->{
            dialogFilter.show();
        });

        filter();
    }

    private void showPengaduan(){
        pengaduanList.clear();
        progressBar.setVisibility(View.VISIBLE);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.child("pegawaiPerkap").child(nipnim).child("idPengaduan")
                        .getChildren()){
                    String idPengaduan = data.getKey();
                    for (DataSnapshot value: dataSnapshot.child("pengaduan").getChildren()){
                        if (idPengaduan.equals(value.getKey())){
                            Pengaduan pengaduan = new Pengaduan();
                            pengaduan.setFoto(value.child("foto").getValue(String.class));
                            pengaduan.setIdBarang(value.child("idBarang").getValue(String.class));
                            pengaduan.setIdPengadu(value.child("idPengadu").getValue(String.class));
                            pengaduan.setIdPengaduan(idPengaduan);
                            pengaduan.setKerusakan(value.child("kerusakan").getValue(String.class));
                            pengaduan.setLokasi(value.child("lokasi").getValue(String.class));
                            pengaduan.setStatus(value.child("status").getValue(String.class));
                            pengaduan.setTanggalMasuk(value.child("tanggalMasuk").getValue(String.class));
                            pengaduan.setTanggalSelesai(value.child("tanggalSelesai").getValue(String.class));
                            pengaduan.setIdPegawai(value.child("idPegawai").getValue(String.class));
                            pengaduan.setId(value.child("id").getValue(Integer.class));

                            pengaduanList.add(pengaduan);
                            Collections.sort(pengaduanList, (p1, p2) -> {
                                return p1.getId() - p2.getId(); // Ascending
                            });
                            adapter.notifyDataSetChanged();
                        }
                    }
                    Log.d("tugassayapengaduan",idPengaduan);
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

            }
        });
    }

    public void filter(){
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.dialogFilter_diterimaBtn:
                    pengaduanList.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot data: dataSnapshot.child("pegawaiPerkap").child(nipnim).child("idPengaduan")
                                    .getChildren()){
                                String idPengaduan = data.getKey();
                                Log.d("tugassayapengaduan",idPengaduan);

                                DataSnapshot value = dataSnapshot.child("pengaduan").child(idPengaduan);
                                String status = value.child("status").getValue(String.class);

                                if (status.equals("diterima")){
                                    Pengaduan pengaduan = new Pengaduan();
                                    pengaduan.setFoto(value.child("foto").getValue(String.class));
                                    pengaduan.setIdBarang(value.child("idBarang").getValue(String.class));
                                    pengaduan.setIdPengadu(value.child("idPengadu").getValue(String.class));
                                    pengaduan.setIdPengaduan(idPengaduan);
                                    pengaduan.setKerusakan(value.child("kerusakan").getValue(String.class));
                                    pengaduan.setLokasi(value.child("lokasi").getValue(String.class));
                                    pengaduan.setStatus(value.child("status").getValue(String.class));
                                    pengaduan.setTanggalMasuk(value.child("tanggalMasuk").getValue(String.class));
                                    pengaduan.setTanggalSelesai(value.child("tanggalSelesai").getValue(String.class));
                                    pengaduan.setIdPegawai(value.child("idPegawai").getValue(String.class));
                                    pengaduan.setId(value.child("id").getValue(Integer.class));

                                    pengaduanList.add(pengaduan);
                                    Collections.sort(pengaduanList, (p1, p2) -> {
                                        return p1.getId() - p2.getId(); // Ascending
                                    });
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

                        }
                    });
                    dialogFilter.dismiss();
                    break;
                case R.id.dialogFilter_diprosesBtn:
                    pengaduanList.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot data: dataSnapshot.child("pegawaiPerkap").child(nipnim).child("idPengaduan")
                                    .getChildren()){
                                String idPengaduan = data.getKey();
                                Log.d("tugassayapengaduan",idPengaduan);

                                DataSnapshot value = dataSnapshot.child("pengaduan").child(idPengaduan);
                                String status = value.child("status").getValue(String.class);

                                if (status.equals("sedang diproses")){
                                    Pengaduan pengaduan = new Pengaduan();
                                    pengaduan.setFoto(value.child("foto").getValue(String.class));
                                    pengaduan.setIdBarang(value.child("idBarang").getValue(String.class));
                                    pengaduan.setIdPengadu(value.child("idPengadu").getValue(String.class));
                                    pengaduan.setIdPengaduan(idPengaduan);
                                    pengaduan.setKerusakan(value.child("kerusakan").getValue(String.class));
                                    pengaduan.setLokasi(value.child("lokasi").getValue(String.class));
                                    pengaduan.setStatus(value.child("status").getValue(String.class));
                                    pengaduan.setTanggalMasuk(value.child("tanggalMasuk").getValue(String.class));
                                    pengaduan.setTanggalSelesai(value.child("tanggalSelesai").getValue(String.class));
                                    pengaduan.setIdPegawai(value.child("idPegawai").getValue(String.class));
                                    pengaduan.setId(value.child("id").getValue(Integer.class));

                                    pengaduanList.add(pengaduan);
                                    Collections.sort(pengaduanList, (p1, p2) -> {
                                        return p1.getId() - p2.getId(); // Ascending
                                    });
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

                        }
                    });
                    dialogFilter.dismiss();
                    break;
                case R.id.dialogFilter_divendorBtn:
                    pengaduanList.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot data: dataSnapshot.child("pegawaiPerkap").child(nipnim).child("idPengaduan")
                                    .getChildren()){
                                String idPengaduan = data.getKey();
                                Log.d("tugassayapengaduan",idPengaduan);

                                DataSnapshot value = dataSnapshot.child("pengaduan").child(idPengaduan);
                                String status = value.child("status").getValue(String.class);

                                if (status.equals("sedang divendor")){
                                    Pengaduan pengaduan = new Pengaduan();
                                    pengaduan.setFoto(value.child("foto").getValue(String.class));
                                    pengaduan.setIdBarang(value.child("idBarang").getValue(String.class));
                                    pengaduan.setIdPengadu(value.child("idPengadu").getValue(String.class));
                                    pengaduan.setIdPengaduan(idPengaduan);
                                    pengaduan.setKerusakan(value.child("kerusakan").getValue(String.class));
                                    pengaduan.setLokasi(value.child("lokasi").getValue(String.class));
                                    pengaduan.setStatus(value.child("status").getValue(String.class));
                                    pengaduan.setTanggalMasuk(value.child("tanggalMasuk").getValue(String.class));
                                    pengaduan.setTanggalSelesai(value.child("tanggalSelesai").getValue(String.class));
                                    pengaduan.setIdPegawai(value.child("idPegawai").getValue(String.class));
                                    pengaduan.setId(value.child("id").getValue(Integer.class));

                                    pengaduanList.add(pengaduan);
                                    Collections.sort(pengaduanList, (p1, p2) -> {
                                        return p1.getId() - p2.getId(); // Ascending
                                    });
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

                        }
                    });
                    dialogFilter.dismiss();
                    break;
                case R.id.dialogFilter_selesaiBtn:
                    pengaduanList.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot data: dataSnapshot.child("pegawaiPerkap").child(nipnim).child("idPengaduan")
                                    .getChildren()){
                                String idPengaduan = data.getKey();
                                Log.d("tugassayapengaduan",idPengaduan);

                                DataSnapshot value = dataSnapshot.child("pengaduan").child(idPengaduan);
                                String status = value.child("status").getValue(String.class);

                                if (status.equals("selesai")){
                                    Pengaduan pengaduan = new Pengaduan();
                                    pengaduan.setFoto(value.child("foto").getValue(String.class));
                                    pengaduan.setIdBarang(value.child("idBarang").getValue(String.class));
                                    pengaduan.setIdPengadu(value.child("idPengadu").getValue(String.class));
                                    pengaduan.setIdPengaduan(idPengaduan);
                                    pengaduan.setKerusakan(value.child("kerusakan").getValue(String.class));
                                    pengaduan.setLokasi(value.child("lokasi").getValue(String.class));
                                    pengaduan.setStatus(value.child("status").getValue(String.class));
                                    pengaduan.setTanggalMasuk(value.child("tanggalMasuk").getValue(String.class));
                                    pengaduan.setTanggalSelesai(value.child("tanggalSelesai").getValue(String.class));
                                    pengaduan.setIdPegawai(value.child("idPegawai").getValue(String.class));
                                    pengaduan.setId(value.child("id").getValue(Integer.class));

                                    pengaduanList.add(pengaduan);
                                    Collections.sort(pengaduanList, (p1, p2) -> {
                                        return p1.getId() - p2.getId(); // Ascending
                                    });
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

                        }
                    });
                    dialogFilter.dismiss();
                    break;
            }
        });
    }
}

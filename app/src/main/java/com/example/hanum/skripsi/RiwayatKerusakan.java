package com.example.hanum.skripsi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.hanum.skripsi.adapter.RiwayatKerusakanRecyclerAdapter;
import com.example.hanum.skripsi.model.Kerusakan;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RiwayatKerusakan extends AppCompatActivity {
    private TextView tvNamaBarang;
    private RecyclerView recyclerView;
    private RiwayatKerusakanRecyclerAdapter adapter;
    private List<Kerusakan> kerusakans = new ArrayList<>();

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_kerusakan);

        tvNamaBarang = findViewById(R.id.riwayatKerusakan_namaBarangTv);
        recyclerView = findViewById(R.id.riwayatKerusakan_recyclerView);

        String idBarang = getIntent().getStringExtra("idBarang");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        adapter = new RiwayatKerusakanRecyclerAdapter(this,kerusakans);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        getNomor(idBarang);
        rvKerusakan(idBarang);
    }

    public void getNomor(String idBarang){
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String barang = dataSnapshot.child("barang").child(idBarang).child("nama").getValue(String.class);
                String noInventaris = dataSnapshot.child("barang").child(idBarang).child("noInventaris").getValue(String.class);
                String nomor = barang+" "+noInventaris;
                showNomor(nomor);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showNomor(String nomor){
        tvNamaBarang.setText(nomor);
    }

    public void rvKerusakan(String idBarang){
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.child("pengaduan").getChildren()){
                    if (idBarang.equals(data.child("idBarang").getValue(String.class))){

                        String tanggalDiterima = data.child("tanggalMasuk").getValue(String.class);
                        String tanggalSelesai = data.child("tanggalSelesai").getValue(String.class);
                        String idPegawai = data.child("idPegawai").getValue(String.class);
                        String namaPegawai = "-";
                        if (!idPegawai.equals("-")){
                            namaPegawai = dataSnapshot.child("pegawaiPerkap").child(idPegawai).child("nama").getValue(String.class);
                        }

                        kerusakans.add(new Kerusakan(tanggalDiterima,tanggalSelesai,namaPegawai));
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

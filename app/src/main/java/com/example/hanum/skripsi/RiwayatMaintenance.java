package com.example.hanum.skripsi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.hanum.skripsi.adapter.MaintenanceRecyclerAdapter;
import com.example.hanum.skripsi.model.Maintenance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RiwayatMaintenance extends AppCompatActivity {
    private TextView tvInventaris,tvLabelKosong;
    private RecyclerView recyclerView;
    private MaintenanceRecyclerAdapter adapter;
    private List<Maintenance> maintenances = new ArrayList<>();

    private String idMaintenance;

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_maintenance);

        tvInventaris = findViewById(R.id.riwayat_nomorTv);
        tvLabelKosong = findViewById(R.id.riwayat_labelKosongTv);
        recyclerView = findViewById(R.id.riwayat_recyclerView);

        idMaintenance = getIntent().getStringExtra("idMaintenance");
        String noInventaris = getIntent().getStringExtra("noInventaris");
        tvInventaris.setText(noInventaris);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new MaintenanceRecyclerAdapter(this,maintenances);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        showRiwayat();
    }

    protected void showRiwayat(){
        mRef.child("maintenance").child(idMaintenance).child("riwayat")
                .orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    String id = data.getKey();
                    String tanggal = data.child("tanggalTerakhir").getValue(String.class);
                    maintenances.add(new Maintenance(id,tanggal,"",0));
                    adapter.notifyDataSetChanged();
                }

                if (maintenances.isEmpty()){
                    tvLabelKosong.setVisibility(View.VISIBLE);
                    tvLabelKosong.setText("Riwayat Kosong");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("databaseerror",databaseError.getMessage());
            }
        });
    }
}

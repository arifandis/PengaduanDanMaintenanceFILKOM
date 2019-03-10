package com.example.hanum.skripsi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.hanum.skripsi.adapter.RiwayatRecyclerAdapter;
import com.example.hanum.skripsi.model.Maintenance;

import java.util.ArrayList;
import java.util.List;

public class RiwayatMaintenance extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RiwayatRecyclerAdapter adapter;
    private List<Maintenance> maintenances = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_maintenance);

        recyclerView = findViewById(R.id.riwayat_recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RiwayatRecyclerAdapter(this,maintenances);
        recyclerView.setAdapter(adapter);

        maintenances.add(new Maintenance("1234","1234","19 Desember 2019, 19.00"));
        maintenances.add(new Maintenance("1234","1234","19 Desember 2019, 19.00"));

        adapter.notifyDataSetChanged();
    }
}

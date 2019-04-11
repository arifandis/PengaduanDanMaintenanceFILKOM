package com.example.hanum.skripsi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.hanum.skripsi.adapter.MaintenanceRecyclerAdapter;
import com.example.hanum.skripsi.model.Maintenance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JadwalMaintenance extends AppCompatActivity {

    private TextView tvTanggal;
    private RecyclerView recyclerView;
    private MaintenanceRecyclerAdapter adapter;
    private List<Maintenance> maintenanceList = new ArrayList<>();

    private String tanggal;
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_maintenance);

        tvTanggal = findViewById(R.id.jadwalMaintenance_tanggalTv);
        recyclerView = findViewById(R.id.jadwalMaintenance_recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new MaintenanceRecyclerAdapter(this,maintenanceList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        tanggal = getIntent().getStringExtra("date");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("ID"));
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("ID"));
        Calendar cal = Calendar.getInstance();

        try {
            cal.setTime(dateFormat.parse(tanggal));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String currentDay = dayFormat.format(cal.getTime());
        tvTanggal.setText(currentDay);

        showMaintenance();
    }

    protected void showMaintenance(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("ID"));
        mRef.child("maintenance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    Date date = new Date();
                    String sDate = data.child("tanggalMulai").getValue(String.class);
                    int skala = data.child("skala").getValue(Integer.class);

                    try {
                        date = dateFormat.parse(sDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_MONTH,skala);
                    String sDate2 = dateFormat.format(calendar.getTime());

                    if (tanggal.equals(sDate2)){
                        String idMaintenance = data.child("idMaintenance").getValue(String.class);
                        String kategori = data.child("kategori").getValue(String.class);
                        String inventaris = data.child("nomor").getValue(String.class);
                        maintenanceList.add(new Maintenance(idMaintenance,kategori,inventaris,skala));
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("databaseerror",databaseError.getMessage());
            }
        });
    }
}

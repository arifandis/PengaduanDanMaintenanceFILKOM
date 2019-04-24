package com.example.hanum.skripsi;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.hanum.skripsi.notif.NotifExample;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PilihRoleActivity extends AppCompatActivity {
    private Button btnPengadu,btnPegawai,btnKasubag;

    private SharedPreferences pref;
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    private String PREF_NAME = "SkripsiPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_role);

        btnPengadu = findViewById(R.id.role_pengaduBtn);
        btnPegawai = findViewById(R.id.role_pegawaiBtn);
        btnKasubag = findViewById(R.id.role_kasubagBtn);

        pref = getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        String role = pref.getString("role",null);

        if (role !=null){
            startActivity(new Intent(this,HomeActivity.class));
            finish();
        }

        btnPengadu.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            intent.putExtra("role","pengadu");
            startActivity(intent);
            finish();
        });

        btnPegawai.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra("role","pegawai");
            startActivity(intent);
            finish();
        });

        btnKasubag.setOnClickListener(v-> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra("role", "kasubag");
            startActivity(intent);
            finish();
        });
    }
}

package com.example.hanum.skripsi;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.onesignal.OneSignal;

import java.util.Calendar;

public class PilihRoleActivity extends AppCompatActivity {
    private Button btnPengadu,btnPegawai,btnKasubag;

    private SharedPreferences pref;

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
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            intent.putExtra("role","pegawai");
            startActivity(intent);
            finish();
        });

//        btnPegawai.setOnClickListener(v->{
//            Calendar cal = Calendar.getInstance();
//            cal.set(Calendar.HOUR_OF_DAY,7);
//            cal.set(Calendar.MINUTE,4);
//            cal.set(Calendar.SECOND,0);
//
//            AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//            Intent intent = new Intent(this, Alarm.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//            // cal.add(Calendar.SECOND, 5);
//            alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//        });

    }
}

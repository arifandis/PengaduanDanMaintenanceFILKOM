package com.example.hanum.skripsi;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanum.skripsi.notif.NotificationMaintenance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class HomeActivity extends AppCompatActivity {
    private CardView cardTanaman,cardKebersihan,cardBarang,cardRuangan;
    private ImageView imgTugasSaya,imgBuatJadwal,imgLaporanPengaduan,imgLaporanMaintenance,imgLaporanRating;
    private TextView tvTugasSaya,tvBuatJadwal,tvName,tvLaporanPengaduan,tvLaporanMaintenance,tvLaporanRating;
    private Button btnDaftarPengaduan,btnLogout,btnStatistikPengaduan,btnJadwalMaintenance;
    private RatingBar ratingBar;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private String PREF_NAME = "SkripsiPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvName = findViewById(R.id.home_nameTv);
        imgBuatJadwal = findViewById(R.id.home_iconBuatJadwalImg);
        imgTugasSaya = findViewById(R.id.home_iconTugasImg);
        tvTugasSaya = findViewById(R.id.home_tugasLabelTv);
        tvBuatJadwal = findViewById(R.id.home_buatLabelTv);
        cardTanaman = findViewById(R.id.home_tugasTamanCard);
        cardKebersihan = findViewById(R.id.home_tugasKebersihanCard);
        cardBarang = findViewById(R.id.home_tugasBarangCard);
        cardRuangan = findViewById(R.id.home_tugasRuangCard);
        btnDaftarPengaduan = findViewById(R.id.home_daftarPengaduanBtn);
        btnStatistikPengaduan = findViewById(R.id.home_statistikPengaduanBtn);
        btnJadwalMaintenance = findViewById(R.id.home_jadwalMaintenanceBtn);
        btnLogout = findViewById(R.id.home_logoutBtn);
        ratingBar = findViewById(R.id.ratingBar);
        imgLaporanPengaduan = findViewById(R.id.home_laporan1Img);
        imgLaporanMaintenance = findViewById(R.id.home_laporan2Img);
        imgLaporanRating = findViewById(R.id.home_laporan3Img);
        tvLaporanPengaduan = findViewById(R.id.home_labelLaporanPengaduanTv);
        tvLaporanMaintenance = findViewById(R.id.home_labelLaporanMaintenanceTv);
        tvLaporanRating = findViewById(R.id.home_labelLaporanRatingTv);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        OneSignal.setSubscription(true);

        prefs = getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        editor = getSharedPreferences(PREF_NAME,MODE_PRIVATE).edit();
        String role = prefs.getString("role",null);
        String nipnim = prefs.getString("id",null);

        if (role.equals("pegawai")){
            OneSignal.sendTag("pegawai", nipnim);
            imgBuatJadwal.setVisibility(View.VISIBLE);
            imgTugasSaya.setVisibility(View.VISIBLE);
            tvBuatJadwal.setVisibility(View.VISIBLE);
            tvTugasSaya.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);
            imgLaporanPengaduan.setVisibility(View.GONE);
            imgLaporanMaintenance.setVisibility(View.GONE);
            imgLaporanRating.setVisibility(View.GONE);
            tvLaporanPengaduan.setVisibility(View.GONE);
            tvLaporanMaintenance.setVisibility(View.GONE);
            tvLaporanRating.setVisibility(View.GONE);
        }else if (role.equals("pengadu")){
            OneSignal.sendTag("pengadu", nipnim);
            imgBuatJadwal.setVisibility(View.GONE);
            imgTugasSaya.setVisibility(View.GONE);
            tvBuatJadwal.setVisibility(View.GONE);
            tvTugasSaya.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
            imgLaporanPengaduan.setVisibility(View.GONE);
            imgLaporanMaintenance.setVisibility(View.GONE);
            imgLaporanRating.setVisibility(View.GONE);
            tvLaporanPengaduan.setVisibility(View.GONE);
            tvLaporanMaintenance.setVisibility(View.GONE);
            tvLaporanRating.setVisibility(View.GONE);
        }else if(role.equals("kasubag")){
            OneSignal.sendTag("kasubag", nipnim);
            imgBuatJadwal.setVisibility(View.GONE);
            imgTugasSaya.setVisibility(View.GONE);
            tvBuatJadwal.setVisibility(View.GONE);
            tvTugasSaya.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
            imgLaporanPengaduan.setVisibility(View.VISIBLE);
            imgLaporanMaintenance.setVisibility(View.VISIBLE);
            imgLaporanRating.setVisibility(View.VISIBLE);
            tvLaporanPengaduan.setVisibility(View.VISIBLE);
            tvLaporanMaintenance.setVisibility(View.VISIBLE);
            tvLaporanRating.setVisibility(View.VISIBLE);
        }

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nama = "";
//                float rating = 0;

                if (role != null){
                    if (role.equals("pengadu")){
                        for (DataSnapshot data: dataSnapshot.getChildren()){
                            String key = data.getKey();
                            if (key.equals("dosen")){
                                for (DataSnapshot value: data.getChildren()){
                                    String id = value.getKey();
                                    if (nipnim.equals(id)){
                                        nama = value.child("nama").getValue(String.class);
                                    }
                                }
                            }else if(key.equals("mahasiswa")){
                                for (DataSnapshot value: data.getChildren()){
                                    String id = value.getKey();
                                    if (nipnim.equals(id)){
                                        nama = value.child("nama").getValue(String.class);
                                    }
                                }
                            }
                        }
                    }else if (role.equals("pegawai")){
                        for (DataSnapshot data: dataSnapshot.child("pegawaiPerkap").getChildren()){
                            String id = data.getKey();
                            if (nipnim.equals(id)){
                                nama = data.child("nama").getValue(String.class);
//                                rating = data.child("rating").getValue(Float.class);
                                setNotifMaintenance();
                            }
                        }
                    }else{
                        nama = "Kasubag";
                    }
                }

                tvName.setText(nama);
//                ratingBar.setRating(rating);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        imgTugasSaya.setOnClickListener(v -> startActivity(new Intent(this,TugasSaya.class)));
        imgBuatJadwal.setOnClickListener(v-> startActivity(new Intent(this, FormJadwalMaintenance.class)));
        tvTugasSaya.setOnClickListener(v -> startActivity(new Intent(this,TugasSaya.class)));
        tvBuatJadwal.setOnClickListener(v -> startActivity(new Intent(this,CalendarAPI.class)));

        imgLaporanPengaduan.setOnClickListener(v -> startActivity(new Intent(this,LaporanPengaduan.class)));
        imgLaporanMaintenance.setOnClickListener(v -> startActivity(new Intent(this,LaporanMaintenance.class)));
        imgLaporanRating.setOnClickListener(v -> startActivity(new Intent(this,LaporanRating.class)));
        tvLaporanPengaduan.setOnClickListener(v -> startActivity(new Intent(this,LaporanPengaduan.class)));
        tvLaporanMaintenance.setOnClickListener(v -> startActivity(new Intent(this,LaporanMaintenance.class)));
        tvLaporanRating.setOnClickListener(v -> startActivity(new Intent(this,LaporanRating.class)));

        if (role.equals("pegawai") || role.equals("kasubag")){
            cardTanaman.setOnClickListener(v -> startActivity(new Intent(this,DaftarPengaduanTanaman.class)));
            cardKebersihan.setOnClickListener(v -> startActivity(new Intent(this,DaftarPengaduanKebersihan.class)));
            cardBarang.setOnClickListener(v -> startActivity(new Intent(this,DaftarPengaduanBarang.class)));
            cardRuangan.setOnClickListener(v -> startActivity(new Intent(this,DaftarPengaduanRuang.class)));
        }else{
            cardTanaman.setOnClickListener(v -> startActivity(new Intent(this,FormPengaduanTanaman.class)));
            cardKebersihan.setOnClickListener(v -> startActivity(new Intent(this,FormPengaduanKebersihan.class)));
            cardBarang.setOnClickListener(v -> startActivity(new Intent(this,FormPengaduanBarang.class)));
            cardRuangan.setOnClickListener(v -> startActivity(new Intent(this,FormPengaduanRuang.class)));
        }
        btnDaftarPengaduan.setOnClickListener(v -> startActivity(new Intent(this, DaftarPengaduan.class)));
        btnJadwalMaintenance.setOnClickListener(v -> startActivity(new Intent(this, CalendarAPI.class)));
        btnStatistikPengaduan.setOnClickListener(v -> startActivity(new Intent(this, StatistikPengaduan.class)));
        btnLogout.setOnClickListener(v->{
            OneSignal.deleteTag("pegawai");
            OneSignal.deleteTag("pengadu");
            OneSignal.deleteTag("kasubag");
            OneSignal.setSubscription(false);
            editor.clear();
            editor.apply();
            startActivity(new Intent(this,PilihRoleActivity.class));
            finish();
        });
    }

    private void showRating(String nipnim){
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.child("pengaduan").getChildren()){
                    String idPengadu = data.child("idPengadu").getValue(String.class);
                    if (nipnim.equals(idPengadu)){
                        String idPegawai = data.child("idPegawai").getValue(String.class);
                        String idPengaduan = data.getKey();

                        if (!idPegawai.equals("-")){
                            String pengaduan = String.valueOf(dataSnapshot.child("pegawaiPerkap").child(idPegawai)
                                    .child("idPengaduan").child(idPengaduan).child("status").getValue());

                            if (pengaduan.equals("selesai")){
                                Intent intent = new Intent(getApplicationContext(),RatingPegawai.class);
                                intent.putExtra("idPegawai",idPegawai);
                                intent.putExtra("idPengaduan",idPengaduan);
                                startActivity(intent);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs = getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        String role = prefs.getString("role",null);
        String nipnim = prefs.getString("id",null);

        if (role.equals("pengadu")){
            showRating(nipnim);
        }else if (role.equals("pegawai")){
            mRef.child("pegawaiPerkap").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    float ratinxFix = dataSnapshot.child(nipnim).child("rating").getValue(Float.class);
                    ratingBar.setRating(ratinxFix);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("databaseerror",databaseError.getMessage());
                    ratingBar.setRating(0f);
                }
            });
        }
    }

    protected void setNotifMaintenance(){
        mRef.child("maintenance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    String maintenancePref = prefs.getString(data.getKey(),null);

                    if (maintenancePref == null){
                        editor.putString(data.getKey(),String.valueOf(data.child("nomor").getValue()));
                        String kategori = String.valueOf(data.child("kategori").getValue());
                        String noInventaris = String.valueOf(data.child("nomor").getValue());
                        int skala = Integer.valueOf(String.valueOf(data.child("skala").getValue()));
                        int id = Integer.valueOf(String.valueOf(data.child("id").getValue()));
                        String tanggalMulai = String.valueOf(data.child("tanggalMulai").getValue());

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy",new Locale("ID"));
                        Date date = new Date();
                        try {
                            date = dateFormat.parse(tanggalMulai);
                            Log.d("Date",String.valueOf(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.d("dateerror",e.getMessage());
                        }

                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(System.currentTimeMillis());
                        cal.clear();
                        cal.setTime(date);
                        cal.add(Calendar.DAY_OF_MONTH,skala-1);
                        cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),9,0);
                        Log.d("calendar",cal.get(Calendar.YEAR)+" "+cal.get(Calendar.MONTH)+" "+cal.get(Calendar.DATE));

                        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(getApplicationContext(), NotificationMaintenance.class);
                        intent.putExtra("idMaintenance",data.getKey());
                        intent.putExtra("kategori",kategori);
                        intent.putExtra("nomor",noInventaris);
                        intent.putExtra("kategori",kategori);
                        intent.putExtra("tanggalMulai",tanggalMulai);
                        intent.putExtra("skala",skala);
                        intent.putExtra("id",id);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intent, 0);
//                        alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                                AlarmManager.INTERVAL_DAY*(skala-1), pendingIntent);

                        Log.d("setalarm","done");
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

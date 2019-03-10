package com.example.hanum.skripsi;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

public class HomeActivity extends AppCompatActivity {
    private CardView cardTanaman,cardKebersihan,cardBarang,cardRuangan;
    private ImageView imgTugasSaya,imgBuatJadwal;
    private TextView tvTugasSaya,tvBuatJadwal,tvName;
    private Button btnDaftarPengaduan,btnLogout;
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
        btnLogout = findViewById(R.id.home_logoutBtn);
        ratingBar = findViewById(R.id.ratingBar);

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
            OneSignal.sendTag("User", "Pegawai");
            imgBuatJadwal.setVisibility(View.VISIBLE);
            imgTugasSaya.setVisibility(View.VISIBLE);
            tvBuatJadwal.setVisibility(View.VISIBLE);
            tvTugasSaya.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);
        }else if (role.equals("pengadu")){
            OneSignal.sendTag("User", nipnim);
        }

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nama = "";
                float rating = 0;

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
                    }else {
                        for (DataSnapshot data: dataSnapshot.child("pegawaiPerkap").getChildren()){
                            String id = data.getKey();
                            if (nipnim.equals(id)){
                                nama = data.child("nama").getValue(String.class);
                                rating = data.child("rating").getValue(Float.class);
                            }
                        }
                    }
                }

                tvName.setText(nama);
                ratingBar.setRating(rating);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        imgTugasSaya.setOnClickListener(v -> startActivity(new Intent(this,TugasSaya.class)));
        imgBuatJadwal.setOnClickListener(v-> startActivity(new Intent(this, CalendarAPI.class)));
        tvTugasSaya.setOnClickListener(v -> startActivity(new Intent(this,TugasSaya.class)));
        tvBuatJadwal.setOnClickListener(v -> startActivity(new Intent(this,CalendarAPI.class)));
        cardTanaman.setOnClickListener(v -> startActivity(new Intent(this,FormPengaduanTanaman.class)));
        cardKebersihan.setOnClickListener(v -> startActivity(new Intent(this,FormPengaduanKebersihan.class)));
        cardBarang.setOnClickListener(v -> startActivity(new Intent(this,FormPengaduanBarang.class)));
        cardRuangan.setOnClickListener(v -> startActivity(new Intent(this,FormPengaduanRuang.class)));
        btnDaftarPengaduan.setOnClickListener(v -> startActivity(new Intent(this, DaftarPengaduan.class)));
        btnLogout.setOnClickListener(v->{
            OneSignal.deleteTag("User");
            OneSignal.setSubscription(false);
            editor.clear();
            editor.apply();
            startActivity(new Intent(this,PilihRoleActivity.class));
        });
    }
}

package com.example.hanum.skripsi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RatingPegawai extends AppCompatActivity {
    private TextView tvNama,tvDeskripsi,tvNipNim;
    private Button btnSubmit;
    private RatingBar ratingBar;

    private String mIdPegawai,mIdPengaduan;

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_pegawai);

        tvNama = findViewById(R.id.rating_namaPegawaiTv);
        tvDeskripsi = findViewById(R.id.rating_deskripsiTv);
        tvNipNim = findViewById(R.id.rating_nipnimTv);
        ratingBar = findViewById(R.id.rating_ratingBar);
        btnSubmit = findViewById(R.id.rating_submitBtn);

        mIdPegawai = getIntent().getStringExtra("idPegawai");
        mIdPengaduan= getIntent().getStringExtra("idPengaduan");

        getData();

        btnSubmit.setOnClickListener(v->{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo !=null && networkInfo.isConnected()){
                submitRating();
            }else{
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData(){
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String nama = dataSnapshot.child("pegawaiPerkap").child(mIdPegawai).child("nama").getValue(String.class);
                tvNama.setText(nama);
                tvNipNim.setText(mIdPegawai);
                tvDeskripsi.setText(nama+" telah selesai menangani keluhan Anda. Beri rating untuk "+nama+":");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RatingPegawai.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitRating(){
        float rating = ratingBar.getRating();
        mRef.child("pegawaiPerkap").child(mIdPegawai).child("idPengaduan").child(mIdPengaduan)
                .child("rating").setValue(rating);

        mRef.child("pegawaiPerkap").child(mIdPegawai).child("idPengaduan")
                .child(mIdPengaduan).child("status").setValue("rated")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Rating berhasil diberikan", Toast.LENGTH_SHORT).show();
                    finish();

                    mRef.child("pegawaiPerkap").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int count = 0;
                            float rating = 0f;
                            for (DataSnapshot data: dataSnapshot.child(mIdPegawai).child("idPengaduan").getChildren()){
                                String status = data.child("status").getValue(String.class);
                                if (status.equals("rated")){
                                    float value = data.child("rating").getValue(Float.class);
                                    //                            float ratingValue = Float.valueOf(value);
                                    rating += value;
                                    count++;

                                    Log.d("count",String.valueOf(count));
                                }
                            }

                            float ratinxFix = rating/count;
                            mRef.child("pegawaiPerkap").child(mIdPegawai).child("rating").setValue(ratinxFix)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getApplicationContext(), "Rating berhasil diberikan", Toast.LENGTH_SHORT).show();
                                        finish();
                                    });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(RatingPegawai.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });

    }
}

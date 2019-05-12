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

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

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
                                        sendNotifitcation(mIdPegawai);
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

    private void sendNotifitcation(String id){
        try {
            String jsonResponse;

            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic NzExODUxODAtMTVhNy00OTM3LWI4OWUtY2JjYzJiODIzMDk0");
            con.setRequestMethod("POST");

            String strJsonBody = ("{"
                    + "\"app_id\": \"10d60748-fe76-4739-b4d3-8e4b91743c3a\","
                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"pegawai\", \"relation\": \"=\", \"value\": \""+id+"\"}],"
                    + "\"data\": {\"foo\": \"bar\"},"
                    + "\"contents\": {\"en\": \"Selamat Anda mendapatkan rating\"},"
                    + "\"headings\": {\"en\": \"Anda telah melaksanakan tugas dengan baik\"}"
                    + "}");

            System.out.println("strJsonBody:\n" + strJsonBody);

            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = con.getResponseCode();
            System.out.println("httpResponse: " + httpResponse);

            if (  httpResponse >= HttpURLConnection.HTTP_OK
                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            else {
                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            System.out.println("jsonResponse:\n" + jsonResponse);

        } catch(Throwable t) {
            t.printStackTrace();
            System.out.println("Failure send notif:"+ t.getMessage());
            Toast.makeText(this, "Gagal mengirimkan notifikasi", Toast.LENGTH_SHORT).show();
        }
    }
}

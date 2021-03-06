package com.example.hanum.skripsi;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanum.skripsi.model.Pengaduan;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;

public class DetailPengaduan extends AppCompatActivity {
    private TextView tvNama,tvStatus,tvDeskripsi,tvNamaBarang,tvTanggal,tvPegawai;
    private ImageView imgFoto;
    private Button btnTerima,btnVendor,btnPerbaiki,btnSelesai;

    private String PREF_NAME = "SkripsiPrefs", mStatus;
    SharedPreferences preferences;
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private String idPengaduan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pengaduan);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        tvNama = findViewById(R.id.detailPengaduan_namaTv);
        tvStatus = findViewById(R.id.detailPengaduan_statusTv);
        tvNamaBarang = findViewById(R.id.detailPengaduan_namaBarangTv);
        tvTanggal = findViewById(R.id.detailPengaduan_tanggalTv);
        tvPegawai = findViewById(R.id.detailPengaduan_pegawaiTv);
        tvDeskripsi = findViewById(R.id.detailPengaduan_deskripsiTv);
        imgFoto = findViewById(R.id.detailPengaduan_fotoImg);
        btnTerima = findViewById(R.id.detailPengaduan_terimaBtn);
        btnVendor = findViewById(R.id.detailPengaduan_vendorBtn);
        btnPerbaiki = findViewById(R.id.detailPengaduan_perbaikiBtn);
        btnSelesai = findViewById(R.id.detailPengaduan_selesaiBtn);

        preferences = getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        idPengaduan = getIntent().getStringExtra("idPengaduan");

        showPengaduan();
    }



    public void showPengaduan(){
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String idPengadu = dataSnapshot.child("pengaduan").child(idPengaduan).child("idPengadu").getValue(String.class);
                String idBarang = dataSnapshot.child("pengaduan").child(idPengaduan).child("idBarang").getValue(String.class);
                String tanggal = dataSnapshot.child("pengaduan").child(idPengaduan).child("tanggalMasuk").getValue(String.class);
                String foto = dataSnapshot.child("pengaduan").child(idPengaduan).child("foto").getValue(String.class);
                String kerusakan = dataSnapshot.child("pengaduan").child(idPengaduan).child("kerusakan").getValue(String.class);
                String status = dataSnapshot.child("pengaduan").child(idPengaduan).child("status").getValue(String.class);
                String idPegawai = dataSnapshot.child("pengaduan").child(idPengaduan).child("idPegawai").getValue(String.class);

                String namaPegawai = "-";
                if (!idPegawai.equals("-")){
                    namaPegawai = dataSnapshot.child("pegawaiPerkap").child(idPegawai).child("nama").getValue(String.class);
                }

                Picasso.get().load(foto).into(imgFoto);
                tvStatus.setText(status);
                tvTanggal.setText(tanggal);
                tvDeskripsi.setText("Deskripsi: "+kerusakan);
                tvPegawai.setText("Pegawai yang menangani: "+namaPegawai);
                if (status.equalsIgnoreCase("belum diterima")){
                    tvStatus.setBackgroundResource(R.drawable.bg_belum_diproses);
                }else if (status.equalsIgnoreCase("sedang diproses")){
                    tvStatus.setBackgroundResource(R.drawable.bg_diproses);
                }else if (status.equalsIgnoreCase("diterima")){
                    tvStatus.setBackgroundResource(R.drawable.bg_diterima);
                }else if (status.equalsIgnoreCase("sedang divendor")){
                    tvStatus.setBackgroundResource(R.drawable.bg_divendor);
                }else if (status.equalsIgnoreCase("selesai")){
                    tvStatus.setBackgroundResource(R.drawable.bg_selesai);
                }

                //Get nama
                String nama = "";

                for (DataSnapshot data: dataSnapshot.getChildren()){
                    String key = data.getKey();
                    if (key.equals("dosen")){
                        for (DataSnapshot value: data.getChildren()){
                            String id = value.getKey();
                            if (idPengadu.equals(id)){
                                nama = value.child("nama").getValue(String.class);
                            }
                        }
                    }else if(key.equals("mahasiswa")){
                        for (DataSnapshot value: data.getChildren()){
                            String id = value.getKey();
                            if (idPengadu.equals(id)){
                                nama = value.child("nama").getValue(String.class);
                            }
                        }
                    }
                }
                tvNama.setText(nama+"\n"+idPengadu);

                //Get nama barang
                String namaBarang = "";
                if (idBarang.contains("barang")){
                    String barang = dataSnapshot.child("barang").child(idBarang).child("nama").getValue(String.class);
                    String noInventaris = dataSnapshot.child("barang").child(idBarang).child("noInventaris").getValue(String.class);
                    namaBarang = barang+" "+noInventaris;
                }else if (idBarang.contains("bersih")){
                    namaBarang = dataSnapshot.child("bersih").child(idBarang).child("nama").getValue(String.class);
                }else if (idBarang.contains("ruang")){
                    namaBarang = dataSnapshot.child("ruang").child(idBarang).child("nama").getValue(String.class);
                }else if (idBarang.contains("taman")){
                    namaBarang = dataSnapshot.child("taman").child(idBarang).child("nama").getValue(String.class);
                }

                tvNamaBarang.setText(namaBarang);
                if (idBarang.contains("barang")){
                    tvNamaBarang.setOnClickListener(v->{
                        Intent intent = new Intent(getApplicationContext(),RiwayatKerusakan.class);
                        intent.putExtra("idBarang",idBarang);
                        startActivity(intent);
                    });
                }

                String idPegawaiCurrent = preferences.getString("id",null);
                ubahStatus(idPengadu,idPegawaiCurrent,status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void ubahStatus(String idPengadu, String idPegawai, String status){
        String role = preferences.getString("role",null);
        Log.d("Role",role);
        String idPegawaiCurrent = preferences.getString("id",null);
        if (role.equals("pegawai")){
            if (status.equals("belum diterima")){
                btnTerima.setVisibility(View.VISIBLE);
                btnTerima.setOnClickListener(v->{
                    btnTerima.setVisibility(View.GONE);
                    sendNotifitcation(idPengadu,status,idPegawai,"belum diterima");

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy",new Locale("ID"));
                    String tanggalDiterima = dateFormat.format(calendar.getTime());

                    mRef.child("pengaduan").child(idPengaduan).child("status").setValue("diterima");
                    mRef.child("pengaduan").child(idPengaduan).child("idPegawai").setValue(idPegawaiCurrent);
                    mRef.child("pegawaiPerkap").child(idPegawaiCurrent).child("idPengaduan").child(idPengaduan)
                            .child("status").setValue("diterima");
                    mRef.child("pegawaiPerkap").child(idPegawaiCurrent).child("idPengaduan").child(idPengaduan)
                            .child("tanggalDiterima").setValue(tanggalDiterima);
                    mRef.child("pegawaiPerkap").child(idPegawaiCurrent).child("idPengaduan").child(idPengaduan)
                            .child("rating").setValue(0);

                    btnPerbaiki.setVisibility(View.VISIBLE);
                    btnVendor.setVisibility(View.VISIBLE);
                    tvStatus.setText("diterima");
                    mStatus = "diterima";
                    ubahStatus(idPengadu,idPegawai,mStatus);
                    tvStatus.setBackgroundResource(R.drawable.bg_diterima);
                });
            }else if (status.equals("diterima")){
                if (idPegawaiCurrent.equals(idPegawai)){
                    btnPerbaiki.setVisibility(View.VISIBLE);
                    btnVendor.setVisibility(View.VISIBLE);
                }
                btnPerbaiki.setOnClickListener(v->{
                    btnPerbaiki.setVisibility(View.GONE);
                    btnVendor.setVisibility(View.GONE);
                    sendNotifitcation(idPengadu,status,idPegawai,"sedang diproses");

                    mRef.child("pengaduan").child(idPengaduan).child("status").setValue("sedang diproses");
                    mRef.child("pegawaiPerkap").child(idPegawaiCurrent).child("idPengaduan").child(idPengaduan)
                            .child("status").setValue("sedang diproses");

                    btnSelesai.setVisibility(View.VISIBLE);
                    tvStatus.setText("sedang diproses");
                    mStatus = "sedang diproses";
                    ubahStatus(idPengadu,idPegawai,mStatus);
                    tvStatus.setBackgroundResource(R.drawable.bg_diproses);
                });

                btnVendor.setOnClickListener(v->{
                    btnPerbaiki.setVisibility(View.GONE);
                    btnVendor.setVisibility(View.GONE);
                    sendNotifitcation(idPengadu,status,idPegawai,"sedang divendor");

                    mRef.child("pengaduan").child(idPengaduan).child("status").setValue("sedang divendor");
                    mRef.child("pegawaiPerkap").child(idPegawaiCurrent).child("idPengaduan").child(idPengaduan)
                            .child("status").setValue("sedang divendor");

                    btnSelesai.setVisibility(View.VISIBLE);
                    tvStatus.setText("sedang divendor");
                    mStatus = "sedang divendor";
                    ubahStatus(idPengadu,idPegawai,mStatus);
                    tvStatus.setBackgroundResource(R.drawable.bg_divendor);
                });
            }else if (status.equals("sedang diproses") || status.equals("sedang divendor")){
                if (idPegawaiCurrent.equals(idPegawai)){
                    btnSelesai.setVisibility(View.VISIBLE);
                }

                btnSelesai.setOnClickListener(v->{
                    sendNotifitcation(idPengadu,status,idPegawai,"selesai");

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy",new Locale("ID"));
                    String tanggalSelesai = dateFormat.format(calendar.getTime());

                    mRef.child("pengaduan").child(idPengaduan).child("status").setValue("selesai");
                    mRef.child("pengaduan").child(idPengaduan).child("tanggalSelesai").setValue(tanggalSelesai);
                    mRef.child("pegawaiPerkap").child(idPegawaiCurrent).child("idPengaduan").child(idPengaduan)
                            .child("status").setValue("selesai");

                    btnSelesai.setVisibility(View.GONE);
                    tvStatus.setText("selesai");
                    mStatus = "selesai";
                    tvStatus.setBackgroundResource(R.drawable.bg_selesai);
                });
            }
        }
    }

    private void sendNotifitcation(String id, String status,String idPegawai, String currentStatus){
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

            String strJsonBody = "";
            if (status.equals("belum diterima")){
                strJsonBody = ("{"
                        + "\"app_id\": \"10d60748-fe76-4739-b4d3-8e4b91743c3a\","
                        + "\"filters\": [{\"field\": \"tag\", \"key\": \"pengadu\", \"relation\": \"=\", \"value\": \""+id+"\"}],"
                        + "\"data\": {\"foo\": \"bar\"},"
                        + "\"contents\": {\"en\": \""+idPegawai+" telah menerima pengaduan Anda\"},"
                        + "\"headings\": {\"en\": \""+idPengaduan+" telah diterima\"}"
                        + "}");
            }else if (status.equals("diterima")){
                if (currentStatus.equals("sedang diproses")){
                    strJsonBody = ("{"
                            + "\"app_id\": \"10d60748-fe76-4739-b4d3-8e4b91743c3a\","
                            + "\"filters\": [{\"field\": \"tag\", \"key\": \"pengadu\", \"relation\": \"=\", \"value\": \""+id+"\"}],"
                            + "\"data\": {\"foo\": \"bar\"},"
                            + "\"contents\": {\"en\": \""+idPegawai+" sedang memproses pengaduan Anda\"},"
                            + "\"headings\": {\"en\": \""+idPengaduan+" sedang diproses\"}"
                            + "}");
                }else if (currentStatus.equals("sedang divendor")){
                    strJsonBody = ("{"
                            + "\"app_id\": \"10d60748-fe76-4739-b4d3-8e4b91743c3a\","
                            + "\"filters\": [{\"field\": \"tag\", \"key\": \"pengadu\", \"relation\": \"=\", \"value\": \""+id+"\"}],"
                            + "\"data\": {\"foo\": \"bar\"},"
                            + "\"contents\": {\"en\": \""+idPegawai+" sedang menyerahkan ke vendor pengaduan Anda\"},"
                            + "\"headings\": {\"en\": \""+idPengaduan+" sedang divendor\"}"
                            + "}");
                }
            }else{
                strJsonBody = ("{"
                        + "\"app_id\": \"10d60748-fe76-4739-b4d3-8e4b91743c3a\","
                        + "\"filters\": [{\"field\": \"tag\", \"key\": \"pengadu\", \"relation\": \"=\", \"value\": \""+id+"\"}],"
                        + "\"data\": {\"foo\": \"bar\"},"
                        + "\"contents\": {\"en\": \""+idPegawai+" telah menyelesaikan pengaduan Anda\"},"
                        + "\"headings\": {\"en\": \""+idPengaduan+" telah selesai\"}"
                        + "}");
            }


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
//            t.printStackTrace();
            Toast.makeText(this, "Gagal mengirimkan notifikasi", Toast.LENGTH_SHORT).show();
        }
    }
}

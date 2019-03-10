package com.example.hanum.skripsi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanum.skripsi.model.Pengaduan;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class FormPengaduanBarang extends AppCompatActivity implements IPickResult {
    private ImageView imgPhoto;
    private TextView etNama,etLokasi,etDeskripsi,etNoInventaris;
    private Button btnSubmit;
    private ProgressDialog progressDialog;

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();

    private String PREF_NAME = "SkripsiPrefs";
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pengaduan_barang);

        imgPhoto = findViewById(R.id.pengaduanBarang_selectImg);
        etNama = findViewById(R.id.pengaduanBarang_inputNamaEt);
        etLokasi = findViewById(R.id.pengaduanBarang_inputLokasiEt);
        etDeskripsi = findViewById(R.id.pengaduanBarang_inputDeskripsiEt);
        etNoInventaris = findViewById(R.id.pengaduanBarang_inputNoInventarisEt);
        btnSubmit = findViewById(R.id.pengaduanBarang_submitBtn);

        preferences = getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Harap tunggu sebentar ...");
        progressDialog.setIndeterminate(true);

        etNama.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        etLokasi.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        etDeskripsi.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        imgPhoto.setOnClickListener(v -> PickImageDialog.build(new PickSetup()).show(getSupportFragmentManager()));
    }

    @Override
    public void onPickResult(PickResult pickResult) {
        imgPhoto.setImageBitmap(pickResult.getBitmap());

        btnSubmit.setOnClickListener(v->{
            String id = preferences.getString("id",null);
            String nama = etNama.getText().toString();
            String lokasi = etLokasi.getText().toString();
            String noInventaris = etNoInventaris.getText().toString();
            String deskripsi = etDeskripsi.getText().toString();

            if (nama.isEmpty() || lokasi.isEmpty() || deskripsi.isEmpty()){
                Toast.makeText(this, "Lengkapi form yang kosong!", Toast.LENGTH_SHORT).show();
            }else {
                progressDialog.show();
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int idPengaduan = 1;
                        for (DataSnapshot data: dataSnapshot.child("pengaduan").getChildren()){
                            idPengaduan++;
                            Log.d("IdPengaduan",idPengaduan+"");
                        }

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy",new Locale("ID"));
                        String tanggal = dateFormat.format(calendar.getTime());

                        StorageReference storageRef = mStorage.child("pengaduan/"+pickResult.getUri().getPathSegments());

                        String finalIdPengaduan = "pengaduan_"+idPengaduan;
                        storageRef.putFile(pickResult.getUri()).addOnSuccessListener(taskSnapshot -> {
                            String imageUrl = String.valueOf(taskSnapshot.getDownloadUrl());

                            int idBarang = 1;
                            boolean cekBarang = false;
                            String strIdBarang = "";
                            for (DataSnapshot data: dataSnapshot.child("barang").getChildren()){
                                idBarang++;
                                String key = data.child("noInventaris").getValue(String.class);
                                if (noInventaris.equalsIgnoreCase(key)){
                                    cekBarang = true;
                                    strIdBarang = data.getKey();
                                }
                            }

                            Map<String,Object> barang = new HashMap<>();
                            barang.put("idBarang",idBarang);
                            barang.put("nama",nama);
                            barang.put("lokasi",lokasi);
                            barang.put("noInventaris",noInventaris.toUpperCase());
                            barang.put("deskripsi",deskripsi);

                            if (cekBarang){
                                Pengaduan pengaduan = new Pengaduan(finalIdPengaduan,id,strIdBarang,deskripsi,imageUrl,lokasi,
                                        "belum diterima",tanggal,"-","-");
                                mRef.child("pengaduan").child(finalIdPengaduan).setValue(pengaduan);
                                sendNotifitcation();
                                Toast.makeText(FormPengaduanBarang.this, "Sukses", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Pengaduan pengaduan = new Pengaduan(finalIdPengaduan,id,"barang_"+idBarang,deskripsi,imageUrl,lokasi,
                                        "belum diterima",tanggal,"-","-");
                                mRef.child("pengaduan").child(finalIdPengaduan).setValue(pengaduan);
                                mRef.child("barang").child("barang_"+idBarang).setValue(barang)
                                        .addOnSuccessListener(aVoid -> {
                                            progressDialog.dismiss();
                                            sendNotifitcation();
                                            startActivity(new Intent(getApplicationContext(),DaftarPengaduan.class));
                                            finish();
                                        });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                        Toast.makeText(FormPengaduanBarang.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }

    private void sendNotifitcation(){
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
                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"User\", \"relation\": \"=\", \"value\": \"Pegawai\"}],"
                    + "\"data\": {\"foo\": \"bar\"},"
                    + "\"contents\": {\"en\": \"Pengaduan Anda telah diterima\"},"
                    + "\"headings\": {\"en\": \"Pengaduan dan Maintenance FILKOM\"}"
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
//            t.printStackTrace();
            Toast.makeText(this, "Gagal mengirimkan notifikasi", Toast.LENGTH_SHORT).show();
        }
    }
}
package com.example.hanum.skripsi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanum.skripsi.model.Pengaduan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class FormPengaduanTanaman extends AppCompatActivity implements IPickResult {
    private ImageView imgPhoto;
    private EditText etNama,etLokasi,etDeskripsi;
    private Button btnSubmit;
    private ProgressDialog progressDialog;

    private String PREF_NAME = "SkripsiPrefs";
    SharedPreferences preferences;

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pengaduan_tanaman);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        imgPhoto = findViewById(R.id.pengaduanTanaman_selectImg);
        etNama = findViewById(R.id.pengaduanTanaman_inputNamaEt);
        etLokasi = findViewById(R.id.pengaduanTanaman_inputLokasiEt);
        etDeskripsi = findViewById(R.id.pengaduanTanaman_inputDeskripsiEt);
        btnSubmit = findViewById(R.id.pengaduanTanaman_submitBtn);

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

        submitPengaduanTanaman(pickResult);
    }

    public void submitPengaduanTanaman(PickResult pickResult){
        btnSubmit.setOnClickListener(v->{
            String id = preferences.getString("id",null);
            String nama = etNama.getText().toString();
            String lokasi = etLokasi.getText().toString();
            String deskripsi = etDeskripsi.getText().toString();

            if (nama.isEmpty() || lokasi.isEmpty() || deskripsi.isEmpty()){
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
            }else {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();

                if (networkInfo !=null && networkInfo.isConnected()){
                    progressDialog.show();
                    mRef.child("pengaduan").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long idPengaduan = dataSnapshot.getChildrenCount()+1;

                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy",new Locale("ID"));
                            String tanggal = dateFormat.format(calendar.getTime());

                            StorageReference storageRef = mStorage.child("pengaduan/"+pickResult.getUri().getPathSegments());

                            String finalIdPengaduan = "pengaduan_"+idPengaduan;
                            storageRef.putFile(pickResult.getUri()).addOnSuccessListener(taskSnapshot -> {
                                String imageUrl = String.valueOf(taskSnapshot.getDownloadUrl());

                                long idTaman = dataSnapshot.child("taman").getChildrenCount()+1;

                                Map<String,String> tanaman = new HashMap<>();
                                tanaman.put("idTaman",String.valueOf(idTaman));
                                tanaman.put("nama",nama);
                                tanaman.put("lokasi",lokasi);
                                tanaman.put("deskripsi",deskripsi);

                                Pengaduan pengaduan = new Pengaduan(finalIdPengaduan,id,"taman_"+idTaman,deskripsi,imageUrl,lokasi,
                                        "belum diterima",tanggal,"-","-");
                                mRef.child("pengaduan").child(finalIdPengaduan).setValue(pengaduan);
                                mRef.child("pengaduan").child(finalIdPengaduan).child("id").setValue(idPengaduan);

                                mRef.child("taman").child("taman_"+idTaman).setValue(tanaman)
                                        .addOnSuccessListener(aVoid -> {
                                            progressDialog.dismiss();
                                            for (DataSnapshot data: dataSnapshot.child("pegawaiPerkap").getChildren()){
                                                String id = data.getKey();
                                                sendNotifitcation(id);
                                            }
                                            startActivity(new Intent(getApplicationContext(),DaftarPengaduan.class));
                                            finish();
                                        });
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressDialog.dismiss();
                            Toast.makeText(FormPengaduanTanaman.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Log.d("Connectivity","No network connection");
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendNotifitcation(String idPegawai){
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

            String strJsonBody = "{"
                    +   "\"app_id\": \"10d60748-fe76-4739-b4d3-8e4b91743c3a\","
                    +   "\"filters\": [{\"field\": \"tag\", \"key\": \"pegawai\", \"relation\": \"=\", \"value\": \""+idPegawai+"\"}],"
                    +   "\"data\": {\"foo\": \"bar\"},"
                    +   "\"contents\": {\"en\": \"Terdapat pengaduan baru\"}"
                    + "}";


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
            Log.d("catch",t.getMessage()+"");
            Toast.makeText(this, "Gagal mengirimkan notifikasi", Toast.LENGTH_SHORT).show();
        }
    }
}

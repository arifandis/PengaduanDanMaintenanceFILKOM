package com.example.hanum.skripsi;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hanum.skripsi.model.Maintenance;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FormJadwalMaintenance extends AppCompatActivity {
    private Button btnSubmit;
    private EditText etSkalaWaktu; //etWaktuTerakhir;
    private Spinner spinnerKategori, spinnerNomor;
    private ImageButton btnMinus,btnPlus;
    private ProgressDialog progressDialog;

    private Calendar calendar;
    private List<String> listNoInventaris = new ArrayList<>();

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    SharedPreferences.Editor editor;
    private String PREF_NAME = "SkripsiPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_jadwal_maintenance);

        spinnerKategori = findViewById(R.id.formJadwal_kategoriSpinner);
        spinnerNomor = findViewById(R.id.formJadwal_nomorSpinner);
        etSkalaWaktu = findViewById(R.id.formJadwal_skalaWaktuEt);
//        etWaktuTerakhir = findViewById(R.id.formJadwal_waktuTerakhirEt);
        btnSubmit = findViewById(R.id.formJadwal_submitBtn);
        btnMinus = findViewById(R.id.formJadwal_minusBtn);
        btnPlus = findViewById(R.id.formJadwal_plusBtn);

        editor = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();

        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Harap tunggu sebentar ...");
        progressDialog.setIndeterminate(true);

        String[] array = getResources().getStringArray(R.array.kategori);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,array);
        spinnerKategori.setAdapter(spinnerAdapter);

        listNoInventaris.add("Pilih Nomor Inventaris");
        ArrayAdapter<String> spinnerInventarisAdapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, listNoInventaris);
        spinnerNomor.setAdapter(spinnerInventarisAdapter);

        spinnerKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String kategori = spinnerKategori.getSelectedItem().toString();
                listNoInventaris.clear();
                listNoInventaris.add("Pilih Nomor Inventaris");
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data: dataSnapshot.child("barang").getChildren()){
                            String nama = data.child("nama").getValue(String.class);
                            if (kategori.equalsIgnoreCase(nama)){
                                String noInventaris = data.child("noInventaris").getValue(String.class);
                                listNoInventaris.add(noInventaris);
                                spinnerInventarisAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnMinus.setOnClickListener(v->{
            int skala = Integer.valueOf(etSkalaWaktu.getText().toString());
            if (skala <= 0){
                etSkalaWaktu.setText("0");
            }else {
                skala--;
                etSkalaWaktu.setText(skala+"");
            }
        });

        btnPlus.setOnClickListener(v->{
            int skala = Integer.valueOf(etSkalaWaktu.getText().toString());
            skala++;
            etSkalaWaktu.setText(skala+"");
        });

        btnSubmit.setOnClickListener(v -> {
            progressDialog.show();
            buatJadwalMantenance();
        });
    }

    private void datePicker() {
        calendar = Calendar.getInstance();
        Calendar hMinus1Calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            hMinus1Calendar.set(Calendar.YEAR, year);
            hMinus1Calendar.set(Calendar.MONTH, monthOfYear);
            hMinus1Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            Log.d("Tanggal",year+" "+monthOfYear+" "+dayOfMonth);

            String myFormat = "dd MMMM yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("id", "ID"));

//            etWaktuTerakhir.setText(sdf.format(calendar.getTime()));
        };

//        etWaktuTerakhir.setOnClickListener(v -> new DatePickerDialog(this, date, calendar
//                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void buatJadwalMantenance(){
        String kategori = spinnerKategori.getSelectedItem().toString();
        String noInventaris = spinnerNomor.getSelectedItem().toString();
        int skala = Integer.valueOf(etSkalaWaktu.getText().toString());
//        String tanggalTerakhir = etWaktuTerakhir.getText().toString();

        if (kategori.equalsIgnoreCase("Pilih kategori") || noInventaris.equalsIgnoreCase("Pilih Nomor Inventaris")
                || skala == 0){
            progressDialog.dismiss();
            Toast.makeText(this, "Lengkapi form yang ada", Toast.LENGTH_SHORT).show();
        }else {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo !=null && networkInfo.isConnected()){
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy",new Locale("ID"));
                String tanggal = dateFormat.format(calendar.getTime());

                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long count = dataSnapshot.child("maintenance").getChildrenCount()+1;
                        long i = dataSnapshot.child("maintenance").getChildrenCount()+1;
                        Log.d("countmaintenance",i+"");

                        Maintenance maintenance = new Maintenance("maintenance_"+i,kategori,
                                        noInventaris,skala);

                        for (DataSnapshot data: dataSnapshot.child("maintenance").getChildren()){
                            String inventaris = data.child("nomor").getValue(String.class);
                            Log.d("inventaris",inventaris);

                            if (count == 0){
                                mRef.child("maintenance").child("maintenance_"+i).setValue(maintenance);
                                mRef.child("maintenance").child("maintenance_"+i).child("id").setValue(i);
                                mRef.child("maintenance").child("maintenance_"+i).child("tanggalMulai").setValue(tanggal);

                                for (DataSnapshot data2: dataSnapshot.child("barang").getChildren()){
                                    String inventaris2 = String.valueOf(data2.child("noInventaris").getValue());

                                    if (noInventaris.equalsIgnoreCase(inventaris2)){
                                        String idBarang = data2.getKey();
                                        mRef.child("barang").child(idBarang).child("idMaintenance").setValue("maintenance_"+i);
                                    }
                                }
                            }else{
                                if (noInventaris.equals(inventaris)){
                                    String idMaintenance = data.getKey();
                                    mRef.child("maintenance").child(idMaintenance).child("skala").setValue(skala);
                                    mRef.child("maintenance").child(idMaintenance).child("tanggalMulai").setValue(tanggal);

                                    editor.remove(idMaintenance);
                                    editor.apply();
                                }else{
                                    mRef.child("maintenance").child("maintenance_"+i).setValue(maintenance);
                                    mRef.child("maintenance").child("maintenance_"+i).child("id").setValue(i);
                                    mRef.child("maintenance").child("maintenance_"+i).child("tanggalMulai").setValue(tanggal);

                                    for (DataSnapshot data2: dataSnapshot.child("barang").getChildren()){
                                        String inventaris2 = String.valueOf(data2.child("noInventaris").getValue());

                                        if (noInventaris.equalsIgnoreCase(inventaris2)){
                                            String idBarang = data2.getKey();
                                            mRef.child("barang").child(idBarang).child("idMaintenance").setValue("maintenance_"+i);
                                        }
                                    }
                                }
                            }
                        }

                        progressDialog.dismiss();
                        Toast.makeText(FormJadwalMaintenance.this, "Jadwal telah dibuat", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),CalendarAPI.class));
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                        Toast.makeText(FormJadwalMaintenance.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Log.d("Connectivity","No network connection");
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

package com.example.hanum.skripsi;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class FormJadwalMaintenance extends AppCompatActivity {
    private Button btnSubmit;
//    private EditText etKategori,etNomor,etSkalaWaktu,etWaktuTerakhir,et

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_jadwal_maintenance);

        btnSubmit = findViewById(R.id.formJadwal_submitBtn);

        btnSubmit.setOnClickListener(v-> finish());
    }
}

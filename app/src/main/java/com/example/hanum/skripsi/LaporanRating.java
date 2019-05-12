package com.example.hanum.skripsi;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hanum.skripsi.adapter.LaporanMaintenanceRecyclerAdapter;
import com.example.hanum.skripsi.adapter.LaporanRatingRecyclerAdapter;
import com.example.hanum.skripsi.model.LaporanMaintenanceModel;
import com.example.hanum.skripsi.model.LaporanRatingModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LaporanRating extends AppCompatActivity {
    private Spinner spinnerBulan;
    private Button btnUnduh;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LaporanRatingRecyclerAdapter adapter;
    private List<LaporanRatingModel> laporanRatingModels= new ArrayList<>();

    private File pdfFile;
    private ProgressDialog progressDialog;

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_rating);

        spinnerBulan = findViewById(R.id.laporanRating_bulanSpinner);
        btnUnduh = findViewById(R.id.laporanRating_unduhBtn);
        recyclerView = findViewById(R.id.laporanRating_recyclerView);
        progressBar = findViewById(R.id.laporanRating_progressBar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new LaporanRatingRecyclerAdapter(this, laporanRatingModels);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Harap tunggu sebentar ...");
        progressDialog.setIndeterminate(true);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", new Locale("ID"));
        String currentYear = dateFormat.format(cal.getTime());

        String[] listBulan = {
                "Januari " + currentYear,
                "Februari " + currentYear,
                "Maret " + currentYear,
                "April " + currentYear,
                "Mei " + currentYear,
                "Juni " + currentYear,
                "Juli " + currentYear,
                "Agustus " + currentYear,
                "September " + currentYear,
                "Oktober " + currentYear,
                "November " + currentYear,
                "Desember " + currentYear};

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, listBulan);
        spinnerBulan.setAdapter(spinnerAdapter);

        spinnerBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String bulan = spinnerBulan.getSelectedItem().toString();
                laporanRatingModels.clear();
                showLaporanRating(bulan);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnUnduh.setOnClickListener(v->{
            String bulan = spinnerBulan.getSelectedItem().toString();
            try {
                createPdf(bulan);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    private void showLaporanRating(String bulan) {
        progressBar.setVisibility(View.VISIBLE);
        laporanRatingModels.clear();
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                laporanRatingModels.clear();
                int nomor = 0;
                for (DataSnapshot dataPegawai: dataSnapshot.child("pegawaiPerkap").getChildren()){
                    float rating = 0;
                    int count = 0;
                    nomor++;
                    String nama = dataPegawai.child("nama").getValue(String.class);

                    for (DataSnapshot dataPengaduan: dataPegawai.child("idPengaduan").getChildren()){
                        String tanggalDiterima = dataPengaduan.child("tanggalDiterima").getValue(String.class);
                        String status = dataPengaduan.child("status").getValue(String.class);

                        if (tanggalDiterima.contains(bulan) && status.equals("rated")){
                            rating+=dataPegawai.child("rating").getValue(Float.class);
                            count++;
                        }
                    }

                    laporanRatingModels.add(new LaporanRatingModel(String.valueOf(nomor),nama,rating,count));
                    adapter.notifyDataSetChanged();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("databaseerror", databaseError.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void createPdf(String bulan) throws FileNotFoundException, DocumentException, ParseException {
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else{
            File sd = Environment.getExternalStorageDirectory();
            String file = "Laporan Rating Bulan "+bulan+".pdf";

            File docsFolder = new File(sd.getAbsolutePath() + "/skripsi/Laporan Rating");
            if (!docsFolder.isDirectory()) {
                docsFolder.mkdirs();
                try {
                    docsFolder.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            pdfFile = new File(docsFolder,file);
            OutputStream output = new FileOutputStream(pdfFile);

            Document document = new Document();
            PdfWriter.getInstance(document, output);

            document.open();
            PdfPTable table = new PdfPTable(3); // 3 columns.

            Paragraph title = new Paragraph("Laporan Rating\nBulan "+bulan,
                    new Font(Font.FontFamily.UNDEFINED,20,Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Phrase("\n"));

            PdfPCell cellHeaderNo = new PdfPCell(new Paragraph("Nomor",new Font(Font.FontFamily.UNDEFINED,11,Font.BOLD)));
            cellHeaderNo.setBackgroundColor(new BaseColor(getResources().getColor(R.color.grey)));
            PdfPCell cellHeaderNama = new PdfPCell(new Paragraph("Nama",new Font(Font.FontFamily.UNDEFINED,11,Font.BOLD)));
            cellHeaderNama.setBackgroundColor(new BaseColor(getResources().getColor(R.color.grey)));
            PdfPCell cellHeaderRating = new PdfPCell(new Paragraph("Rating",new Font(Font.FontFamily.UNDEFINED,11,Font.BOLD)));
            cellHeaderRating.setBackgroundColor(new BaseColor(getResources().getColor(R.color.grey)));
            table.addCell(cellHeaderNo);
            table.addCell(cellHeaderNama);
            table.addCell(cellHeaderRating);

            for (LaporanRatingModel model: laporanRatingModels){
                PdfPCell cell1 = new PdfPCell(new Paragraph(model.getNomor()));
                PdfPCell cell2 = new PdfPCell(new Paragraph(model.getNama()));

                float rating = 0;
                if (model.getCount() != 0){
                    rating = model.getRating()/model.getCount();
                }
                PdfPCell cell3 = new PdfPCell(new Paragraph(String.valueOf(rating)));

                table.addCell(cell1);
                table.addCell(cell2);
                table.addCell(cell3);
            }

            document.add(table);
            document.close();
            progressDialog.dismiss();
            previewPdf(sd.getAbsolutePath() + "/skripsi/Laporan Rating/"+file);
        }
    }

    private void previewPdf(String path) {

        File open = new File(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.fromFile(open));
        Intent chooser = Intent.createChooser(intent,"Pilih");
        startActivity(chooser);
    }
}
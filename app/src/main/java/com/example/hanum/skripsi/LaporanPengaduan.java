package com.example.hanum.skripsi;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanum.skripsi.adapter.LaporanPengaduanRecyclerAdapter;
import com.example.hanum.skripsi.model.LaporanPengaduanModel;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class LaporanPengaduan extends AppCompatActivity {
    private Spinner spinnerBulan;
    private Button btnUnduh;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LaporanPengaduanRecyclerAdapter adapter;
    private List<LaporanPengaduanModel> pengaduans = new ArrayList<>();

    private File pdfFile;
    private ProgressDialog progressDialog;

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_pengaduan);

        spinnerBulan = findViewById(R.id.laporanPengaduan_bulanSpinner);
        btnUnduh = findViewById(R.id.laporanPengaduan_unduhBtn);
        recyclerView = findViewById(R.id.laporanPengaduan_recyclerView);
        progressBar = findViewById(R.id.laporanPengaduan_progressBar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new LaporanPengaduanRecyclerAdapter(this, pengaduans);
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
                pengaduans.clear();
                showLaporanPengaduan(bulan);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnUnduh.setOnClickListener(v-> {
            progressDialog.show();
            try {
                String bulan = spinnerBulan.getSelectedItem().toString();
                createPdf(bulan);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            } catch (DocumentException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            } catch (ParseException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        });

    }

    private void showLaporanPengaduan(String bulan) {
        progressBar.setVisibility(View.VISIBLE);
        mRef.child("pengaduan").orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pengaduans.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String tanggalMulai = data.child("tanggalMasuk").getValue(String.class);

                    if (tanggalMulai.contains(bulan)) {
                        String judul = data.child("kerusakan").getValue(String.class);
                        String status = data.child("status").getValue(String.class);
                        String tanggalSelesai = data.child("tanggalSelesai").getValue(String.class);
                        Log.d("tanggalselesai",tanggalSelesai);
                        SimpleDateFormat format1 = new SimpleDateFormat("dd MMMM yyyy", new Locale("ID"));
                        SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yy", new Locale("ID"));

                        Date dateMulai = new Date();
                        Date dateSelesai = new Date();
                        try {
                            dateMulai = format1.parse(tanggalMulai);
                            if (!tanggalSelesai.equals("-")){
                                dateSelesai = format1.parse(tanggalSelesai);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String formatTglMulai = format2.format(dateMulai);
                        String formatTglSelesai = tanggalSelesai;
                        if (!tanggalSelesai.equals("-")){
                            formatTglSelesai = format2.format(dateSelesai);
                        }

                        LaporanPengaduanModel pengaduanModel = new LaporanPengaduanModel(judul, status, formatTglMulai, formatTglSelesai);

                        Log.d("laporanpengaduan", String.valueOf(pengaduanModel));

                        pengaduans.add(pengaduanModel);
                        adapter.notifyDataSetChanged();
                    }

                }

                if (pengaduans.isEmpty()) {
                    pengaduans.clear();
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
            String file = "Laporan Pengaduan Bulan "+bulan+".pdf";

            File docsFolder = new File(sd.getAbsolutePath() + "/skripsi/Laporan Pengaduan");
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
            PdfPTable table = new PdfPTable(4); // 4 columns.

//            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format1 = new SimpleDateFormat("dd MMMM yyyy", new Locale("ID"));
            Paragraph title = new Paragraph("Laporan Pengaduan\nBulan "+bulan,
                    new Font(Font.FontFamily.UNDEFINED,20,Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Phrase("\n"));

            PdfPCell cellHeaderJudul = new PdfPCell(new Paragraph("Judul",new Font(Font.FontFamily.UNDEFINED,11,Font.BOLD)));
            cellHeaderJudul.setBackgroundColor(new BaseColor(getResources().getColor(R.color.grey)));
            PdfPCell cellHeaderStatus = new PdfPCell(new Paragraph("Status",new Font(Font.FontFamily.UNDEFINED,11,Font.BOLD)));
            cellHeaderStatus.setBackgroundColor(new BaseColor(getResources().getColor(R.color.grey)));
            PdfPCell cellHeaderTglMulai = new PdfPCell(new Paragraph("Tanggal Mulai",new Font(Font.FontFamily.UNDEFINED,11,Font.BOLD)));
            cellHeaderTglMulai.setBackgroundColor(new BaseColor(getResources().getColor(R.color.grey)));
            PdfPCell cellHeaderTglSelesai = new PdfPCell(new Paragraph("Tanggal Selesai",new Font(Font.FontFamily.UNDEFINED,11,Font.BOLD)));
            cellHeaderTglSelesai.setBackgroundColor(new BaseColor(getResources().getColor(R.color.grey)));
            table.addCell(cellHeaderJudul);
            table.addCell(cellHeaderStatus);
            table.addCell(cellHeaderTglMulai);
            table.addCell(cellHeaderTglSelesai);

            for (LaporanPengaduanModel model: pengaduans){
                PdfPCell cell1 = new PdfPCell(new Paragraph(model.getJudul()));
                PdfPCell cell2 = new PdfPCell(new Paragraph(model.getStatus()));

                SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yy", new Locale("ID"));

                Date date1 = format2.parse(model.getTanggalMulai());
                Date date2;
                String tanggalSelesai = model.getTanggalSelesai();
                if (!model.getTanggalSelesai().equals("-")){
                    date2 = format2.parse(model.getTanggalSelesai());
                    tanggalSelesai = format1.format(date2);
                }

                String tanggalMulai = format1.format(date1);
                PdfPCell cell3 = new PdfPCell(new Paragraph(tanggalMulai));
                PdfPCell cell4 = new PdfPCell(new Paragraph(tanggalSelesai));

                table.addCell(cell1);
                table.addCell(cell2);
                table.addCell(cell3);
                table.addCell(cell4);
            }

            document.add(table);
            document.close();
            progressDialog.dismiss();
            Toast.makeText(this, "Laporan berhasil di exort", Toast.LENGTH_SHORT).show();
            previewPdf(sd.getAbsolutePath() + "/skripsi/Laporan Pengaduan/"+file);
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

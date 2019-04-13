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
import com.example.hanum.skripsi.model.LaporanMaintenanceModel;
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
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LaporanMaintenance extends AppCompatActivity {
    private Spinner spinnerBulan;
    private Button btnUnduh;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LaporanMaintenanceRecyclerAdapter adapter;
    private List<LaporanMaintenanceModel> laporanMaintenanceModels = new ArrayList<>();

    private File pdfFile;
    private ProgressDialog progressDialog;

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_maintenance);

        spinnerBulan = findViewById(R.id.laporanMaintenance_bulanSpinner);
        btnUnduh = findViewById(R.id.laporanMaintenance_unduhBtn);
        recyclerView = findViewById(R.id.laporanMaintenance_recyclerView);
        progressBar = findViewById(R.id.laporanMaintenance_progressBar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new LaporanMaintenanceRecyclerAdapter(this, laporanMaintenanceModels);
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
                laporanMaintenanceModels.clear();
                showLaporanMaintenance(bulan);
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

    private void showLaporanMaintenance(String bulan) {
        progressBar.setVisibility(View.VISIBLE);
        mRef.child("maintenance").orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    String tanggalMulai = data.child("tanggalMulai").getValue(String.class);
                    int skala = data.child("skala").getValue(Integer.class);
                    SimpleDateFormat format1 = new SimpleDateFormat("dd MMMM yyyy", new Locale("ID"));
                    SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yy", new Locale("ID"));
                    Calendar cal = Calendar.getInstance();
                    try {
                        cal.setTime(format1.parse(tanggalMulai));
                        cal.add(Calendar.DAY_OF_MONTH,skala);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String tanggalPelaksanaan = format1.format(cal.getTime());
                    Log.d("tangalpelaksanaan",tanggalPelaksanaan);

                    if (tanggalPelaksanaan.contains(bulan)){
                        String noInvetaris = data.child("nomor").getValue(String.class);

                        laporanMaintenanceModels.add(new LaporanMaintenanceModel(noInvetaris,skala+" hari",format2.format(cal.getTime())));
                        adapter.notifyDataSetChanged();
                    }

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
            String file = "Laporan Maintenance Bulan "+bulan+".pdf";

            File docsFolder = new File(sd.getAbsolutePath() + "/skripsi/Laporan Maintenance");
            if (!docsFolder.exists()) {
                docsFolder.mkdir();
                Log.i("note", "Created a new directory for PDF");
            }

            pdfFile = new File(docsFolder,file);
            OutputStream output = new FileOutputStream(pdfFile);

            Document document = new Document();
            PdfWriter.getInstance(document, output);

            document.open();
            PdfPTable table = new PdfPTable(3); // 3 columns.

            SimpleDateFormat format1 = new SimpleDateFormat("dd MMMM yyyy", new Locale("ID"));
            Paragraph title = new Paragraph("Laporan Maintenance\nBulan "+bulan,
                    new Font(Font.FontFamily.UNDEFINED,20,Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Phrase("\n"));

            PdfPCell cellHeaderNoInventaris= new PdfPCell(new Paragraph("Nomor Inventaris",new Font(Font.FontFamily.UNDEFINED,11,Font.BOLD)));
            cellHeaderNoInventaris.setBackgroundColor(new BaseColor(getResources().getColor(R.color.grey)));
            PdfPCell cellHeaderSkala = new PdfPCell(new Paragraph("Skala",new Font(Font.FontFamily.UNDEFINED,11,Font.BOLD)));
            cellHeaderSkala.setBackgroundColor(new BaseColor(getResources().getColor(R.color.grey)));
            PdfPCell cellHeaderTglPelaksanaan = new PdfPCell(new Paragraph("Tanggal Pelaksanaan",new Font(Font.FontFamily.UNDEFINED,11,Font.BOLD)));
            cellHeaderTglPelaksanaan.setBackgroundColor(new BaseColor(getResources().getColor(R.color.grey)));
            table.addCell(cellHeaderNoInventaris);
            table.addCell(cellHeaderSkala);
            table.addCell(cellHeaderTglPelaksanaan);

            for (LaporanMaintenanceModel model: laporanMaintenanceModels){
                PdfPCell cell1 = new PdfPCell(new Paragraph(model.getNoInventaris()));
                PdfPCell cell2 = new PdfPCell(new Paragraph(model.getSkala()));

                SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yy", new Locale("ID"));

                Date date1 = format2.parse(model.getWaktuPelaksanaan());
                String tanggalMulai = format1.format(date1);
                PdfPCell cell3 = new PdfPCell(new Paragraph(tanggalMulai));

                table.addCell(cell1);
                table.addCell(cell2);
                table.addCell(cell3);
            }

            document.add(table);
            document.close();
            progressDialog.dismiss();
            Toast.makeText(this, "Laporan berhasil di exort", Toast.LENGTH_SHORT).show();
            previewPdf();
        }
    }

    private void previewPdf() {

        PackageManager packageManager = getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(pdfFile);
            intent.setDataAndType(uri, "application/pdf");

            startActivity(intent);
        }else{
            Toast.makeText(this,"Download a PDF Viewer to see the generated PDF", Toast.LENGTH_SHORT).show();
        }
    }
}

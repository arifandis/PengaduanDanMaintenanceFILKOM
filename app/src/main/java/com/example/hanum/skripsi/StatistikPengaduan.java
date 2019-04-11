package com.example.hanum.skripsi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class StatistikPengaduan extends AppCompatActivity {
    private AnyChartView anyChartView;

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistik_pengaduan);

        anyChartView = findViewById(R.id.statistik_chartView);
        anyChartView.setProgressBar(findViewById(R.id.statistik_progressBar));

        showStatistik();
    }

    protected void showStatistik(){
        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        List<String> lokasis = new ArrayList<>();

        mRef.child("pengaduan").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot value: dataSnapshot.getChildren()){
                    String lokasi = value.child("lokasi").getValue(String.class);

                    lokasis.add(lokasi);
                }

                for (int i = 0; i < lokasis.size(); i++){
                    if (i < 5){
                        data.add(new ValueDataEntry(lokasis.get(i),Collections.frequency(lokasis,lokasis.get(i))));
                    }else {
                        break;
                    }
                }

                Column column = cartesian.column(data);

                column.tooltip()
                        .titleFormat("{%X}")
                        .position(Position.CENTER_BOTTOM)
                        .anchor(Anchor.CENTER_BOTTOM)
                        .offsetX(0d)
                        .offsetY(0d)
                        .format("{%Value}{groupsSeparator: }");

                cartesian.animation(true);
                cartesian.title("Statistik");

                cartesian.yScale().minimum(0d);

                cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                cartesian.interactivity().hoverMode(HoverMode.BY_X);

                cartesian.xAxis(0).title("Pengaduan");
                cartesian.yAxis(0).title("Jumlah");

                anyChartView.setChart(cartesian);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(StatistikPengaduan.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.hanum.skripsi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarAPI extends AppCompatActivity {
    private CalendarView calendarView;
    private List<EventDay> eventDayList = new ArrayList<>();

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_api);

        calendarView = findViewById(R.id.calendarApi_calendarView);

        showJadwal();

        calendarView.setOnDayClickListener(eventDay -> {
            Log.d("Day", String.valueOf(eventDay.getCalendar().getTime()));

            Intent intent = new Intent(this, JadwalMaintenance.class);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("ID"));
            intent.putExtra("date", dateFormat.format(eventDay.getCalendar().getTime()));
            startActivity(intent);

        });
    }

    protected void showJadwal() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("ID"));
        mRef.child("maintenance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String tanggalMulai = data.child("tanggalMulai").getValue(String.class);
                    Log.d("tanggalMulai", tanggalMulai);
                    int skala = data.child("skala").getValue(Integer.class);

                    Calendar calendar = Calendar.getInstance();
                    try {
                        calendar.setTime(dateFormat.parse(tanggalMulai));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.d("exception", e.getMessage());
                    }

                    calendar.add(Calendar.DAY_OF_MONTH, skala);
                    eventDayList.add(new EventDay(calendar, R.drawable.ic_maintenance));
                }

                calendarView.setEvents(eventDayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CalendarAPI.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.hanum.skripsi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarAPI extends AppCompatActivity {
    private CalendarView calendarView;
    private List<EventDay> eventDayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_api);

        calendarView = findViewById(R.id.calendarApi_calendarView);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("ID"));
        String day = dateFormat.format(calendar.getTime());
        eventDayList.add(new EventDay(calendar, R.drawable.ic_maintenance));

        calendarView.setEvents(eventDayList);
        calendarView.setOnDayClickListener(eventDay -> {
            if (!day.equals(dateFormat.format(eventDay.getCalendar().getTime()))){
                startActivity(new Intent(getApplicationContext(),FormJadwalMaintenance.class));
            }

            Log.d("Day",dateFormat.format(eventDay.getCalendar().getTime()));
        });
    }
}

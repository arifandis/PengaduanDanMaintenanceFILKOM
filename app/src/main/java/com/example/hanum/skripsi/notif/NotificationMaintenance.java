package com.example.hanum.skripsi.notif;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.hanum.skripsi.CalendarAPI;
import com.example.hanum.skripsi.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class NotificationMaintenance extends BroadcastReceiver {

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private Calendar cal, calendar;

    @Override
    public void onReceive(Context context, Intent intent) {
        String idMaintenance = intent.getStringExtra("idMaintenance");
        String kategori = intent.getStringExtra("kategori");
        String nomor = intent.getStringExtra("nomor");
        String strTanggalMulai = intent.getStringExtra("tanggalMulai");
        int skala = intent.getIntExtra("skala", 0);
        int id = intent.getIntExtra("id", 0);

//        Toast.makeText(context, "Alarm", Toast.LENGTH_SHORT).show();

        Intent intent1 = new Intent(context, CalendarAPI.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, -1, intent1, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Notifikasi Maintenance")
                        .setContentText("Besok maintenance " + kategori + " " + nomor)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("ID"));
        cal = Calendar.getInstance();
        calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(strTanggalMulai));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DAY_OF_MONTH, skala);
        String tanggal = dateFormat.format(calendar.getTime());
        String tanggalMulai = dateFormat.format(cal.getTime());
        Log.d("gantitanggalmulai", tanggal);

        Intent alarmIntent = new Intent(context, NotificationMaintenance.class);
        intent.putExtra("idMaintenance", idMaintenance);
        intent.putExtra("kategori", kategori);
        intent.putExtra("nomor", nomor);
        intent.putExtra("kategori", kategori);
        intent.putExtra("tanggalMulai", tanggalMulai);
        intent.putExtra("skala", skala);
        intent.putExtra("id", id);
        PendingIntent pendingIntentAlarm = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (skala == 1 || skala == 0) {
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntentAlarm);
        } else {
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * (skala - 1), pendingIntentAlarm);
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            mRef.child("maintenance").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean check = false;
                    for (DataSnapshot data : dataSnapshot.child("riwayat").getChildren()) {
                        String strTanggal = data.child("tanggalTerakhir").getValue(String.class);
                        Log.d("tanggalTerakhir", strTanggal + " " + tanggal);

                        if (tanggal.equals(strTanggal)) {
                            check = true;
                        }
                    }

                    Log.d("checkstatus", check + "");

                    if (!check) {
                        long countRiwayat = dataSnapshot.child(idMaintenance).child("riwayat").getChildrenCount() + 1;

                        mRef.child("maintenance").child(idMaintenance).child("tanggalMulai").setValue(tanggalMulai);
                        mRef.child("maintenance").child(idMaintenance).child("riwayat").child(countRiwayat + "")
                                .child("id").setValue(countRiwayat);
                        mRef.child("maintenance").child(idMaintenance).child("riwayat").child(countRiwayat + "")
                                .child("tanggalTerakhir").setValue(tanggal);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("databaseError", databaseError.getMessage());
                }
            });
        }
    }
}

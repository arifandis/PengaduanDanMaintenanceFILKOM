package com.example.hanum.skripsi.notif;

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

public class NotificationMaintenance extends BroadcastReceiver {

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    @Override
    public void onReceive(Context context, Intent intent) {
        String idMaintenance = intent.getStringExtra("idMaintenance");
        String kategori = intent.getStringExtra("kategori");
        String nomor = intent.getStringExtra("nomor");
        String tanggalMulai = intent.getStringExtra("tanggalMulai");
        int skala = intent.getIntExtra("skala",0);
        int id = intent.getIntExtra("id",0);

        Intent intent1 = new Intent(context, CalendarAPI.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Notifikasi Maintenance")
                        .setContentText("Besok maintenance "+kategori+" "+nomor);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo !=null && networkInfo.isConnected()){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,1);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy",new Locale("ID"));
            String tanggal = dateFormat.format(calendar.getTime());
            Log.d("gantitanggalmulai",tanggal);

            mRef.child("maintenance").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long countRiwayat = dataSnapshot.child(idMaintenance).child("riwayat").getChildrenCount()+1;

                    mRef.child("maintenance").child(idMaintenance).child("tanggalMulai").setValue(tanggal);
                    mRef.child("maintenance").child(idMaintenance).child("riwayat").child(countRiwayat+"")
                            .child("id").setValue(countRiwayat);
                    mRef.child("maintenance").child(idMaintenance).child("riwayat").child(countRiwayat+"")
                            .child("tanggalTerakhir").setValue(tanggalMulai);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("databaseError",databaseError.getMessage());
                }
            });
        }
    }
}

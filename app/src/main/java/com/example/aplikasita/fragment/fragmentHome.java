package com.example.aplikasita.fragment;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.example.aplikasita.Login;
import com.example.aplikasita.MainActivity;
import com.example.aplikasita.R;
import com.example.aplikasita.database.userApps;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class fragmentHome extends Fragment {
    View view;
    TextView tanggal, email;
    TextView vTemp, vSoil, output, vJumlah;
    ArcProgress gTemp, gSoil;
    Button button1;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Boolean checkBackground;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        tanggal = view.findViewById(R.id.tanggaldanwaktu);
        email = view.findViewById(R.id.txtEmail);
        vTemp = view.findViewById(R.id.value_temp);
        vSoil = view.findViewById(R.id.value_soil);
        gTemp = view.findViewById(R.id.gauge_suhu);
        gSoil = view.findViewById(R.id.gauge_kelembaban);
        vJumlah = view.findViewById(R.id.jumlah_data);
        output = view.findViewById(R.id.hasil_fuzzy);
        button1 = view.findViewById(R.id.button);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = firebaseUser.getUid();
        reference.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userApps u = snapshot.getValue(userApps.class);
                if (u != null) {
                    email.setText(u.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle("Log Out")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getActivity().getApplicationContext(), Login.class));
                                getActivity().finish();
                            }
                        }).setNegativeButton("Tidak", null).create();
                dialog.show();
            }
        });

        display_gauge();
        displayTimeStamp();
        displayTotalData();
        return view;
    }

    private void displayTotalData() {
        reference.child("Data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int hitung = (int) snapshot.getChildrenCount();
                vJumlah.setText("Jumlah Data : "+hitung);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayTimeStamp() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                DateFormat format_jam = new SimpleDateFormat("HH:mm:ss");
                DateFormat format_tanggal = new SimpleDateFormat("EEEE, dd MMMM yyyy");
                tanggal.setText(format_tanggal.format(new Date())+", "+format_jam.format(new Date()));
                handler.postDelayed(this ,1000);
            }
        });
    }

    private void display_gauge() {
        reference.child("Data").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String bacasuhu = snapshot1.child("Suhu").getValue().toString();
                    float nilaisuhu = Float.parseFloat(bacasuhu)*100;
                    int suhu = Math.round(nilaisuhu);
                    gTemp.setProgress(suhu);
                    gTemp.setMax(10000);
                    vTemp.setText(bacasuhu+" °C");

                    String kelembaban_tanah = snapshot1.child("Kelembaban_Tanah").getValue().toString();
                    float nilai_tanah = Float.parseFloat(kelembaban_tanah)*100;
                    int progress = Math.round(nilai_tanah);
                    gSoil.setProgress(progress);
                    gSoil.setMax(10000);
                    vSoil.setText(kelembaban_tanah+" %");

                    String durasi = snapshot1.child("Lama_Siram").getValue().toString();
                    String keterangan = snapshot1.child("keterangan").getValue().toString();

                    output.setText(keterangan +" ("+durasi+" detik)");

                    ActivityManager.RunningAppProcessInfo prosesInfo = new ActivityManager.RunningAppProcessInfo();
                    ActivityManager.getMyMemoryState(prosesInfo);
                    checkBackground = prosesInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                    if (checkBackground) {
                        if (Float.parseFloat(durasi) >= 1.00) {
                            Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            PendingIntent pendingIntent = PendingIntent.getActivity(getActivity().getApplicationContext(),
                                    0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder
                                    (getActivity().getApplicationContext(), "CH1")
                                    .setSmallIcon(R.drawable.logo1)
                                    .setContentText("Sistem Melakukan Penyiraman. (Suhu : "+bacasuhu+"°C , " +
                                            "Kelembaban Tanah : "+kelembaban_tanah+" %, \nLama Penyiraman : "
                                            +Float.parseFloat(durasi)+" detik. " +
                                            "\nTap Untuk Membuka Aplikasi")
                                    .setContentTitle("Pemberitahuan Sistem Melakukan Penyiraman")
                                    .setAutoCancel(true)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent)
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                            NotificationManager notificationManager = (NotificationManager) getActivity().getApplicationContext().
                                    getSystemService(Context.NOTIFICATION_SERVICE);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES. O) {
                                NotificationChannel channel = new NotificationChannel("CH1",
                                        "Notifikasi",NotificationManager.IMPORTANCE_DEFAULT);
                                notificationManager.createNotificationChannel(channel);
                            }
                            notificationManager.notify(1, notificationBuilder.build());
                        }
                    } else {
                        Log.d("EcoGarden","Aplikasi sedang berjalan di latar depan");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Gagal Memuat Data",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

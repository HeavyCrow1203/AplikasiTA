package com.example.aplikasita.marker_chart;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.aplikasita.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Marker_Chart_Suhu extends MarkerView {
    TextView tanggal_value, waktu_value, suhu;
    DatabaseReference reference;
    ArrayList<String> label_waktu = new ArrayList<>();
    String waktu;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public Marker_Chart_Suhu(Context context, int layoutResource, ArrayList<String> xLabel) {
        super(context, layoutResource);
        label_waktu = xLabel;

        suhu = findViewById(R.id.txtInfo);
        tanggal_value= findViewById(R.id.txtTitle);
        waktu_value = findViewById(R.id.txtWaktu);

        reference = FirebaseDatabase.getInstance().getReference("Data");
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        String waktu = label_waktu.get((int) e.getX());
        String nilai_suhu = String.valueOf(e.getY());
        reference.orderByChild("Waktu").equalTo(waktu).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    int i = -1;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        i = i+1;
                        String date_chart = snapshot1.child("Tanggal").getValue().toString();
                        suhu.setText("kelembaban Tanah : "+nilai_suhu+" °C");
                        waktu_value.setText(waktu);
                        tanggal_value.setText(date_chart);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Gagal Memuat Data", Toast.LENGTH_SHORT).show();
            }
        });

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}

package com.example.aplikasita.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aplikasita.R;
import com.example.aplikasita.database.getData;
import com.example.aplikasita.marker_chart.Marker_Chart_Kelembaban;
import com.example.aplikasita.marker_chart.Marker_Chart_Lama_Siram;
import com.example.aplikasita.marker_chart.Marker_Chart_Suhu;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class fragmentChart extends Fragment {
    LineChart line_chart_suhu, line_chart_kelembaban, line_chart_siram;
    LineDataSet lineDataSet1, lineDataSet2, lineDataSet3;
    ArrayList<ILineDataSet> iLineDataSets1, iLineDataSets2, iLineDataSets3;
    LineData lineData;
    ArrayList<String> label = new ArrayList<>();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Data");
    DatePickerDialog date;
    SimpleDateFormat dateFormat;
    Button filter_chart;
    EditText tanggal;
    getData dataku;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        setHasOptionsMenu(true);

        line_chart_suhu = view.findViewById(R.id.grafik_suhu);
        line_chart_kelembaban = view.findViewById(R.id.grafik_kelembaban_tanah);
        line_chart_siram = view.findViewById(R.id.grafik_lama_siram);
        filter_chart = view.findViewById(R.id.aksi_filter);
        dataku = new getData();
        filter_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tanggal.getText().toString().isEmpty()) {
                    tanggal.setError("isi dulu");
                    tanggal.requestFocus();
                } else {
                    filter_grafik();
                }
            }
        });

        tampilkan_grafik();

        tanggal = view.findViewById(R.id.filter_grafik);
        tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.US);
        return view;
    }

    private void filter_grafik() {
        lineDataSet1 = new LineDataSet(null, null);
        iLineDataSets1 = new ArrayList<>();
        lineDataSet2 = new LineDataSet(null, null);
        iLineDataSets2 = new ArrayList<>();
        lineDataSet3 = new LineDataSet(null, null);
        iLineDataSets3 = new ArrayList<>();
        reference.orderByChild("Tanggal").equalTo(tanggal.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Entry> entries1 = new ArrayList<>();
                ArrayList<Entry> entries2 = new ArrayList<>();
                ArrayList<Entry> entries3 = new ArrayList<>();

                if (snapshot.hasChildren()) {
                    int i = -1;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        i = i+1;
                        String suhu = snapshot1.child("Suhu").getValue().toString();
                        String kelembaban_tanah = snapshot1.child("Kelembaban_Tanah").getValue().toString();
                        String durasi_siram = snapshot1.child("Lama_Siram").getValue().toString();
                        String timestamp = snapshot1.child("Waktu").getValue().toString();
                        entries1.add(new Entry(i, Float.parseFloat(suhu)));
                        entries2.add(new Entry(i, Float.parseFloat(kelembaban_tanah)));
                        entries3.add(new Entry(i, Float.parseFloat(durasi_siram)));
                        label.add(timestamp);
                    }
                    lineDataSet1.setValues(entries1);
                    display_Chart(line_chart_suhu, lineDataSet1, iLineDataSets1, "Suhu (°C)",
                            new Marker_Chart_Suhu(getActivity().getApplicationContext(),
                                    R.layout.marker_chart_suhu, label), Color.BLUE);
                    lineDataSet2.setValues(entries2);
                    display_Chart(line_chart_kelembaban, lineDataSet2, iLineDataSets2, "Kelembaban Tanah (%)",
                            new Marker_Chart_Kelembaban(getActivity().getApplicationContext(),
                                    R.layout.marker_chart_kelembaban, label), Color.GREEN);
                    lineDataSet3.setValues(entries3);
                    display_Chart(line_chart_siram, lineDataSet3, iLineDataSets3, "Lama Siram (detik)",
                            new Marker_Chart_Lama_Siram(getActivity().getApplicationContext(),
                                    R.layout.custom_mark_chart, label), Color.RED);
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Data pada grafik tidak ditemukan")
                            .setNegativeButton("Kembali", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create();
                    dialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pesanToast("Gagal Memuat Data");
            }
        });
    }

    private void tampilkan_grafik() {
        lineDataSet1 = new LineDataSet(null, null);
        iLineDataSets1 = new ArrayList<>();
        lineDataSet2 = new LineDataSet(null, null);
        iLineDataSets2 = new ArrayList<>();
        lineDataSet3 = new LineDataSet(null, null);
        iLineDataSets3 = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Entry> entries1 = new ArrayList<>();
                ArrayList<Entry> entries2 = new ArrayList<>();
                ArrayList<Entry> entries3 = new ArrayList<>();

                if (snapshot.hasChildren()) {
                    int i = -1;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        i = i+1;
                        String suhu = snapshot1.child("Suhu").getValue().toString();
                        String kelembaban_tanah = snapshot1.child("Kelembaban_Tanah").getValue().toString();
                        String durasi_siram = snapshot1.child("Lama_Siram").getValue().toString();
                        String timestamp = snapshot1.child("Waktu").getValue().toString();
                        entries1.add(new Entry(i, Float.parseFloat(suhu)));
                        entries2.add(new Entry(i, Float.parseFloat(kelembaban_tanah)));
                        entries3.add(new Entry(i, Float.parseFloat(durasi_siram)));
                        label.add(timestamp);
                    }
                    lineDataSet1.setValues(entries1);
                    display_Chart(line_chart_suhu, lineDataSet1, iLineDataSets1, "Suhu (°C)",
                            new Marker_Chart_Suhu(getActivity().getApplicationContext(),
                                    R.layout.marker_chart_suhu, label), Color.BLUE);
                    lineDataSet2.setValues(entries2);
                    display_Chart(line_chart_kelembaban, lineDataSet2, iLineDataSets2, "Kelembaban Tanah (%)",
                            new Marker_Chart_Kelembaban(getActivity().getApplicationContext(),
                                    R.layout.marker_chart_kelembaban, label), Color.GREEN);
                    lineDataSet3.setValues(entries3);
                    display_Chart(line_chart_siram, lineDataSet3, iLineDataSets3, "Lama Siram (detik)",
                            new Marker_Chart_Lama_Siram(getActivity().getApplicationContext(),
                                    R.layout.custom_mark_chart, label), Color.RED);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pesanToast("Gagal Memuat Data");
            }
        });
    }

    private void display_Chart(LineChart lineChart, LineDataSet lineDataSet, ArrayList<ILineDataSet> iLineDataSets,
                               String keterangan, IMarker iMarker, int warna) {
        lineDataSet.setLabel(keterangan);
        lineDataSet.setColor(warna);
        lineDataSet.setCircleRadius(2f);
        lineDataSet.setCircleColor(warna);
        iLineDataSets.clear();
        iLineDataSets.add(lineDataSet);
        lineData = new LineData(iLineDataSets);
        lineChart.clear();
        lineChart.setData(lineData);
        lineChart.invalidate();
        lineChart.setDoubleTapToZoomEnabled(true);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(label));
        lineChart.animateX(2000);
        lineDataSet.setLineWidth(2);
        lineChart.setScaleEnabled(true);
        lineChart.getLegend().setDrawInside(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getDescription().setEnabled(false);
        lineChart.setMarker(iMarker);
    }

    private void pesanToast(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showDateDialog() {
        Calendar newCalendar = Calendar.getInstance();
        date = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tanggal.setText(dateFormat.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        date.show();
    }
}

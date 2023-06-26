package com.example.aplikasita.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplikasita.MainActivity;
import com.example.aplikasita.R;
import com.example.aplikasita.database.getData;
import com.example.aplikasita.etc.AdapterListData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class fragmentDataLogger extends Fragment {
    View view;
    DatabaseReference databaseReference;
    RecyclerView rv_data;
    List<getData> dataFirebaseList;
    AdapterListData adapter;
    private static final int STORAGE_PERMISSION_CODE = 101;
    ValueEventListener eventListener;
    File file;
    Button fileExcel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data_logger, container, false);
        setHasOptionsMenu(true);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        databaseReference = FirebaseDatabase.getInstance().getReference("Data");
        rv_data = view.findViewById(R.id.list_data_2);
        list_view();
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataFirebaseList = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    getData load = snapshot1.getValue(getData.class);
                    load.setKey(snapshot1.getKey());
                    dataFirebaseList.add(load);
                }
                adapter = new AdapterListData((ArrayList<getData>) dataFirebaseList, getContext());
                rv_data.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Gagal Memuat Data",
                        Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.addValueEventListener(eventListener);

        fileExcel = view.findViewById(R.id.generateExcel);
        fileExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateExcel();
            }
        });

        return view;
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), permission)
                == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{permission}, requestCode);
        } else {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                /**Toast.makeText(getActivity().getApplicationContext(),
                        "Storage Permission Granted", Toast.LENGTH_SHORT).show();**/
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // susunan data berdasarkan tanggal terdahulu
    private void list_view() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        rv_data.setHasFixedSize(true);
        rv_data.setLayoutManager(linearLayoutManager);
    }

    // susunan data berdasarkan tanggal terbaru
    private void list_reverse_view() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        rv_data.setHasFixedSize(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rv_data.setLayoutManager(linearLayoutManager);
    }

    // eksport ke file excel
    private void generateExcel() {
        Workbook wb = new HSSFWorkbook();
        Cell cell = null;
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);


        CellStyle cellStyle1 = wb.createCellStyle();
        cellStyle1.setAlignment(HorizontalAlignment.CENTER);

        //creating sheet
        Sheet sheet;
        sheet = wb.createSheet("Sheet 1");

        // create row and column
        Row row = sheet.createRow(0);
        Row row1 = sheet.createRow(2);

        cell = row.createCell(0);
        cell.setCellValue("Data Monitoring Tanaman Daun Mint");
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,5));
        cell.setCellStyle(cellStyle1);

        cell = row1.createCell(0);
        cell.setCellValue("No.");
        cell.setCellStyle(cellStyle);

        cell = row1.createCell(1);
        cell.setCellValue("Tanggal");
        cell.setCellStyle(cellStyle);

        cell = row1.createCell(2);
        cell.setCellValue("Waktu");
        cell.setCellStyle(cellStyle);

        cell = row1.createCell(3);
        cell.setCellValue("Suhu (*C)");
        cell.setCellStyle(cellStyle);

        cell = row1.createCell(4);
        cell.setCellValue("Kelembaban Tanah \n\n (%)");
        cell.setCellStyle(cellStyle);

        cell = row1.createCell(5);
        cell.setCellValue("Lama Siram");
        cell.setCellStyle(cellStyle);

        cell = row1.createCell(6);
        cell.setCellValue("keterangan");
        cell.setCellStyle(cellStyle);

        sheet.setColumnWidth(0,(10*100));
        sheet.setColumnWidth(1,(10*800));
        sheet.setColumnWidth(2,(10*800));
        sheet.setColumnWidth(3,(10*500));
        sheet.setColumnWidth(4,(10*500));
        sheet.setColumnWidth(5,(10*500));
        sheet.setColumnWidth(6,(10*500));

        dataFirebaseList.add(new getData());

        for (int i = 0; i < dataFirebaseList.size(); i++) {
            Row row2 = sheet.createRow(i+3);

            if(dataFirebaseList.get(i) == null) {
                sheet.shiftRows(i+1, dataFirebaseList.size(), -1);
                i--;
                continue;
            }

            cell = row2.createCell(0);
            cell.setCellValue(String.valueOf(i+1));
            cell.setCellStyle(cellStyle1);

            cell = row2.createCell(1);
            cell.setCellValue(dataFirebaseList.get(i).getTanggal());
            cell.setCellStyle(cellStyle1);

            cell = row2.createCell(2);
            cell.setCellValue(dataFirebaseList.get(i).getWaktu());
            cell.setCellStyle(cellStyle1);

            cell = row2.createCell(3);
            cell.setCellValue(String.valueOf(dataFirebaseList.get(i).getSuhu()));
            cell.setCellStyle(cellStyle1);

            cell = row2.createCell(4);
            cell.setCellValue(String.valueOf(dataFirebaseList.get(i).getKelembaban_Tanah()));
            cell.setCellStyle(cellStyle1);

            cell = row2.createCell(5);
            cell.setCellValue(String.valueOf(dataFirebaseList.get(i).getLama_Siram()));
            cell.setCellStyle(cellStyle1);

            cell = row2.createCell(6);
            cell.setCellValue(dataFirebaseList.get(i).getKeterangan());
            cell.setCellStyle(cellStyle1);
        }

        String fileName = "File "+dateFile()+".xls";
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        FileOutputStream outputStream = null;

        try {
            // notifikasi ketika file berhasil di download
            outputStream=new FileOutputStream(file);
            wb.write(outputStream);

            Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(getActivity().getApplicationContext(),STORAGE_PERMISSION_CODE,intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity().getApplicationContext(), "CH1")
                    .setSmallIcon(R.drawable.logo1)
                    .setContentText(fileName+" berhasil disimpan ke folder Downloads")
                    .setContentTitle("Pemberitahuan Sistem")
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = (NotificationManager) getActivity().getApplicationContext().
                    getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES. O) {
                NotificationChannel channel = new NotificationChannel("CH1", "Notifikasi",NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(1, notificationBuilder.build());

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity().getApplicationContext(),"NO OK",Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // membuat tanggal dan waktu untuk penamaan file excel
    private String dateFile () {
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM YYYY HH:MM:ss");
        Date date = new Date();
        String datefile = dateFormat.format(date);
        return  datefile;
    }

    // melakukan pencarian data
    private void filter(String data) {
        List<getData> list = new ArrayList<>();
        for (getData s : dataFirebaseList) {
            if (s.getTanggal().toLowerCase().contains(data.toLowerCase().toLowerCase())) {
                list.add(s);
            }
        }
        adapter.filterlist(list);
    }

    // fitur pencarian
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_data, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText.toLowerCase().toString());
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    // fitur untuk mengurutkan list berdasarkan tanggal
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int pilih = item.getItemId();
        if (pilih == R.id.action_sort) {
            if (pilih == R.id.action_sort_ascending) {
                list_view();
            } else if (pilih == R.id.action_sort_descending) {
                list_reverse_view();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

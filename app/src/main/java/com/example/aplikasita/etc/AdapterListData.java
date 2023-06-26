package com.example.aplikasita.etc;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplikasita.R;
import com.example.aplikasita.database.getData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdapterListData extends RecyclerView.Adapter<AdapterListData.ViewHolder> {
    List<getData> list;
    Context context;

    public AdapterListData(List<getData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_card_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        getData data1 = list.get(position);
        holder.temp.setText("Suhu : "+data1.getSuhu()+"\u00B0C");
        holder.moist.setText("Kelembaban Tanah : "+data1.getKelembaban_Tanah()+" %");
        holder.duration.setText("Lama Siram : "+data1.getLama_Siram()+" detik");
        holder.status.setText("Keterangan : "+data1.getKeterangan());
        holder.timestamp.setText(data1.getTanggal()+", "+list.get(position).getWaktu());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Hapus Item ?").setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onDeleteData(list.get(position), position);
                            }
                        }).setNegativeButton("Batal", null).create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView temp, moist, duration, status, timestamp;
        public ViewHolder(@NonNull View v) {
            super(v);
            temp = v.findViewById(R.id.textsuhu);
            moist = v.findViewById(R.id.textsoil);
            duration = v.findViewById(R.id.textduration);
            status = v.findViewById(R.id.textstatus);
            timestamp = v.findViewById(R.id.textdate);
        }
    }

    private void onDeleteData(getData data2, int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Data");
        if (databaseReference!=null) {
            databaseReference.child(data2.getKey()).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context.getApplicationContext(), "Data Berhasil di hapus", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void filterlist(List<getData> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}

package com.example.aplikasita;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.aplikasita.etc.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Lupa_Passwd extends AppCompatActivity {

    EditText enter_email;
    Button kirim;
    LoadingDialog loadingDialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_passwd);

        kirim = findViewById(R.id.lupa_kirim);
        enter_email = findViewById(R.id.lupa_email);
        loadingDialog = new LoadingDialog(Lupa_Passwd.this);
        auth = FirebaseAuth.getInstance();

        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String mail = enter_email.getText().toString();
        if (mail.isEmpty()) {
            enter_email.setError("Field tidak boleh kosong");
            enter_email.requestFocus();
            return;
        }
        loadingDialog.startLoadingDialog();
        auth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingDialog.dismissDialog();
                if (task.isSuccessful()) {
                    messageAlert("Link Ganti Kata Sandi telah dikirimkan melalui Email Anda, Silakan periksa email Anda");
                } else {
                    messageAlert("Email tidak cocok, Silakan Coba Lagi");
                }
            }
        });
    }

    private void messageAlert(String pesan) {
        AlertDialog dialog = new AlertDialog.Builder(Lupa_Passwd.this)
                .setTitle(pesan)
                .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        loadingDialog.dismissDialog();
                    }
                }).create();
        dialog.show();
    }
}
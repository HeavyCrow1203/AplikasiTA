package com.example.aplikasita;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.aplikasita.database.userApps;
import com.example.aplikasita.etc.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registrasi extends AppCompatActivity implements View.OnClickListener {

    EditText username, email,pass;
    Button daftar_akun, login;
    LoadingDialog loadingDialog;
    FirebaseAuth auth;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

        username = findViewById(R.id.registrasi_username);
        email = findViewById(R.id.registrasi_email);
        pass = findViewById(R.id.registrasi_password);
        daftar_akun = findViewById(R.id.registrasi_tombol_daftar);
        login = findViewById(R.id.registrasi_tombol_login);

        loadingDialog = new LoadingDialog(Registrasi.this);
        db = FirebaseDatabase.getInstance().getReference("Users");
        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(this);
        daftar_akun.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int pilih = view.getId();
        if (pilih == R.id.registrasi_tombol_daftar) {
            signUp();
        } else if (pilih == R.id.registrasi_tombol_login) {
            startActivity(new Intent(Registrasi.this, Login.class));
        }
    }

    private void signUp() {
        String Email = email.getText().toString();
        String Username = username.getText().toString();
        String Password = pass.getText().toString();

        if (Email.isEmpty()) {
            editTextAlert(email, "Field tidak boleh kosong");
            return;
        }
        if (Username.isEmpty()) {
            editTextAlert(username, "Field tidak boleh kosong");
            return;
        }
        if (Password.isEmpty()) {
            editTextAlert(pass, "Field tidak boleh kosong");
            return;
        }
        if (Password.length() < 8) {
            editTextAlert(pass, "Password minimal 8 karakter");
            return;
        }
        loadingDialog.startLoadingDialog();
        auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    userApps users = new userApps(Username, Email);
                    db.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loadingDialog.dismissDialog();
                            if (task.isSuccessful()) {
                                pesanAlertDialog("Proses Registrasi Akun Berhasil, Silakan Menuju Halaman Login");
                            } else {
                                pesanAlertDialog("Proses Registrasi akun mengalami kegagalan, Silakan Coba Lagi");
                            }
                        }
                    });
                } else {
                    pesanAlertDialog("Proses Registrasi akun mengalami kegagalan, Silakan Coba Lagi");
                }
            }
        });
    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
    }

    private void editTextAlert(EditText editText2, String pesanAlert) {
        editText2.setError(pesanAlert);
        editText2.requestFocus();
    }

    private void pesanAlertDialog(String message2) {
        AlertDialog dialog = new AlertDialog.Builder(Registrasi.this)
                .setTitle(message2)
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
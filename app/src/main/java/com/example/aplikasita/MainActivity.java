package com.example.aplikasita;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.aplikasita.fragment.fragmentChart;
import com.example.aplikasita.fragment.fragmentDataLogger;
import com.example.aplikasita.fragment.fragmentHome;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment pilih = null;
            int aksi = item.getItemId();
            if (aksi == R.id.home) {
                pilih = new fragmentHome();
                setTitle("Dashboard");
            } else if (aksi == R.id.datas) {
                pilih = new fragmentDataLogger();
                setTitle("Data Logger");
            } else if (aksi == R.id.grafik) {
                pilih = new fragmentChart();
                setTitle("Chart");
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, pilih).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Dashboard");

        bottomNavigationView = findViewById(R.id.menunya);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new fragmentHome()).commit();
    }
}
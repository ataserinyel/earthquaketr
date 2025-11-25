package com.example.deprem.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.deprem.R;
import com.google.android.material.appbar.MaterialToolbar;

public class DetailActivity extends AppCompatActivity {

    private TextView tvDetailMag, tvDetailTitle, tvDetailCity, tvDetailDate, tvDetailDepth, tvDetailPopulation;

    private Button btnOpenMap;
    private double lat;
    private double lon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvDetailMag = findViewById(R.id.tvDetailMag);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailCity = findViewById(R.id.tvDetailCity);
        tvDetailDate = findViewById(R.id.tvDetailDate);
        tvDetailDepth = findViewById(R.id.tvDetailDepth);
        tvDetailPopulation = findViewById(R.id.tvDetailPopulation);
        btnOpenMap = findViewById(R.id.btnOpenMap);

        // Intent’ten verileri al
        String title = getIntent().getStringExtra("title");
        double mag = getIntent().getDoubleExtra("mag", 0);
        double depth = getIntent().getDoubleExtra("depth", 0);
        String date = getIntent().getStringExtra("date");
        String cityName = getIntent().getStringExtra("cityName");
        double distance = getIntent().getDoubleExtra("distance", -1);
        long population = getIntent().getLongExtra("population", -1);

        lat = getIntent().getDoubleExtra("lat", 0);
        lon = getIntent().getDoubleExtra("lon", 0);

        tvDetailMag.setText(String.format("%.1f", mag));
        tvDetailTitle.setText(title != null ? title : "-");
        tvDetailDate.setText(date != null ? date : "-");

        if (cityName != null) {
            if (distance >= 0) {
                double km = distance / 1000.0;
                tvDetailCity.setText(cityName + String.format(" (%.1f km)", km));
            } else {
                tvDetailCity.setText(cityName);
            }
        } else {
            tvDetailCity.setText("Yakın şehir bilgisi yok");
        }

        tvDetailDepth.setText(String.format("Derinlik: %.1f km", depth));

        if (population > 0) {
            tvDetailPopulation.setText("Nüfus: " + population);
        } else {
            tvDetailPopulation.setText("Nüfus bilgisi yok");
        }

        // Magnitüd’e göre renk
        int colorResId;
        if (mag < 3.0) {
            colorResId = R.color.mag_low;
        } else if (mag < 5.0) {
            colorResId = R.color.mag_mid;
        } else {
            colorResId = R.color.mag_high;
        }
        int color = ContextCompat.getColor(this, colorResId);
        tvDetailMag.setTextColor(color);

        // --- Haritada Aç Butonu ---
        if (lat == 0 && lon == 0) {
            btnOpenMap.setEnabled(false);
            btnOpenMap.setText("Konum bilgisi yok");
        } else {
            btnOpenMap.setEnabled(true);
            btnOpenMap.setOnClickListener(v -> {
                android.util.Log.d("DetailActivity", "MAP CLICK lat=" + lat + " lon=" + lon);
                openInMaps();
            });
        }
    }

    private void openInMaps() {
        if (lat == 0 && lon == 0) {
            android.widget.Toast.makeText(
                    this,
                    "Konum bilgisi yok.",
                    android.widget.Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String url = "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lon;

        android.util.Log.d("DetailActivity", "MAP URL: " + url);

        try {
            android.content.Intent intent = new android.content.Intent(
                    android.content.Intent.ACTION_VIEW,
                    android.net.Uri.parse(url)
            );
            startActivity(intent);
        } catch (Exception e) {
            android.widget.Toast.makeText(
                    this,
                    "Haritayı açarken hata oluştu: " + e.getMessage(),
                    android.widget.Toast.LENGTH_LONG
            ).show();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

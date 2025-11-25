package com.example.deprem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.deprem.model.ApiResponse;
import com.example.deprem.model.ClosestCity;
import com.example.deprem.model.Earthquake;
import com.example.deprem.network.ApiService;
import com.example.deprem.network.RetrofitClient;
import com.example.deprem.ui.DetailActivity;
import com.example.deprem.network.NetworkStatus;
import com.example.deprem.R;
import com.example.deprem.ui.adapter.EarthquakeAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EarthquakeAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvError;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerViewEarthquakes);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EarthquakeAdapter(new EarthquakeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Earthquake earthquake) {
                openDetailScreen(earthquake);
            }
        });

        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadEarthquakes);

        loadEarthquakes();
    }

    private void openDetailScreen(Earthquake eq) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("earthquake_id", eq.getEarthquake_id());
        intent.putExtra("title", eq.getTitle());
        intent.putExtra("mag", eq.getMag());
        intent.putExtra("depth", eq.getDepth());
        intent.putExtra("date", eq.getDate());

        ClosestCity city = null;
        if (eq.getLocation_properties() != null) {
            city = eq.getLocation_properties().getClosestCity();
        }
        if (city != null) {
            intent.putExtra("cityName", city.getName());
            intent.putExtra("distance", city.getDistance());
            intent.putExtra("population", city.getPopulation());
        }
        if (eq.getGeojson() != null && eq.getGeojson().getCoordinates() != null
                && eq.getGeojson().getCoordinates().size() >= 2) {

            // Dikkat: GeoJSON genelde [lon, lat] formatındadır
            double lon = eq.getGeojson().getCoordinates().get(0);
            double lat = eq.getGeojson().getCoordinates().get(1);

            intent.putExtra("lat", lat);
            intent.putExtra("lon", lon);
        }

        startActivity(intent);
    }

    private void loadEarthquakes() {

        if (!NetworkStatus.isInternetAvailable(this)) {
            tvError.setText("İnternet bağlantısı bulunamadı.");
            tvError.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            showLoading(false);
            return; // API çağrısı yapma
        }

        showLoading(true);
        tvError.setVisibility(View.GONE);

        ApiService apiService = RetrofitClient.getApiService();
        apiService.getLatestEarthquakes().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();

                    if (apiResponse.isStatus()) {
                        List<Earthquake> list = apiResponse.getResult();
                        adapter.setEarthquakeList(list);
                        if (list == null || list.isEmpty()) {
                            tvError.setText("Veri bulunamadı.");
                            tvError.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvError.setText("Sunucu hata döndürdü.");
                        tvError.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvError.setText("Cevap alınamadı. Kod: " + response.code());
                    tvError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);
                tvError.setText("Hata: " + t.getMessage());
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}

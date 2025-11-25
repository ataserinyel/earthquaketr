package com.example.deprem.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deprem.R;
import com.example.deprem.model.ClosestCity;
import com.example.deprem.model.Earthquake;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.EqViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Earthquake earthquake);
    }

    private List<Earthquake> earthquakeList = new ArrayList<>();
    private OnItemClickListener listener;

    public EarthquakeAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setEarthquakeList(List<Earthquake> list) {
        this.earthquakeList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_earthquake, parent, false);
        return new EqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EqViewHolder holder, int position) {
        Earthquake eq = earthquakeList.get(position);

        double mag = eq.getMag();
        holder.tvMag.setText(String.format("%.1f", mag));
        holder.tvTitle.setText(eq.getTitle());

        ClosestCity city = null;
        if (eq.getLocation_properties() != null) {
            city = eq.getLocation_properties().getClosestCity();
        }

        if (city != null) {
            double km = city.getDistance() / 1000.0;
            holder.tvCity.setText(city.getName() + String.format(" (%.1f km)", km));
        } else {
            holder.tvCity.setText("Yakın şehir bilgisi yok");
        }

        holder.tvDate.setText(eq.getDate());
        holder.tvDepth.setText(String.format("%.1f km", eq.getDepth()));

        // --- MAGNITÜDE GÖRE RENK ---
        int colorResId;
        if (mag < 3.0) {
            colorResId = R.color.mag_low;
        } else if (mag < 5.0) {
            colorResId = R.color.mag_mid;
        } else {
            colorResId = R.color.mag_high;
        }

        int color = ContextCompat.getColor(holder.itemView.getContext(), colorResId);
        holder.tvMag.setTextColor(color);

        // --- TIKLAMA OLAYI ---
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(eq);
            }
        });
    }

    @Override
    public int getItemCount() {
        return earthquakeList.size();
    }

    static class EqViewHolder extends RecyclerView.ViewHolder {

        TextView tvMag, tvTitle, tvCity, tvDate, tvDepth;

        public EqViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMag = itemView.findViewById(R.id.tvMag);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCity = itemView.findViewById(R.id.tvCity);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDepth = itemView.findViewById(R.id.tvDepth);
        }
    }
}

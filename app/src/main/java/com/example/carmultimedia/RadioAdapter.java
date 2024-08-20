package com.example.carmultimedia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class RadioAdapter extends RecyclerView.Adapter<RadioAdapter.RadioViewHolder> {

    private List<RadioStation> radioStations;
    private Context context;
    private int playingPosition = -1;  // No station is playing initially

    public RadioAdapter(List<RadioStation> radioStations, Context context) {
        this.radioStations = radioStations;
        this.context = context;
    }

    @NonNull
    @Override
    public RadioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_radio_station, parent, false);
        return new RadioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RadioViewHolder holder, @SuppressLint("RecyclerView") int position) {
        RadioStation station = radioStations.get(position);
        holder.stationName.setText(station.getStationName());

        // Load image with a library like Picasso or Glide
        Glide.with(context)
                .load(station.getImageUrl())
                .into(holder.stationImage);

        // Set the correct icon and background color for the button
        if (position == playingPosition) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.primary));
            holder.soundStatus.setImageResource(R.drawable.ic_pause);
        } else {
            holder.itemView.setBackgroundColor(0);
            holder.soundStatus.setImageResource(R.drawable.ic_play);
        }

        holder.itemView.setOnClickListener(v -> {
            if (position == playingPosition) {
                // If this station is already playing, pause it
                controlRadioService("ACTION_PAUSE");
                playingPosition = -1;  // Reset the playing position
            } else {
                // If another station is playing, stop it first
                if (playingPosition != -1) {
                    controlRadioService("ACTION_STOP");
                    notifyItemChanged(playingPosition);  // Update the previous station's icon
                }

                // Start the new station
                controlRadioService("ACTION_PLAY", station.getStreamLink());
                playingPosition = position;
            }
            notifyItemChanged(position);  // Update the new station's icon or the paused icon
        });
    }

    private void controlRadioService(String action, String streamLink) {
        Intent serviceIntent = new Intent(context, RadioService.class);
        serviceIntent.setAction(action);
        serviceIntent.putExtra("STREAM_LINK", streamLink);
        context.startService(serviceIntent);
    }

    private void controlRadioService(String action) {
        Intent serviceIntent = new Intent(context, RadioService.class);
        serviceIntent.setAction(action);
        context.startService(serviceIntent);
    }

    @Override
    public int getItemCount() {
        return radioStations.size();
    }

    public static class RadioViewHolder extends RecyclerView.ViewHolder {
        ImageView stationImage;
        TextView stationName;
        ShapeableImageView soundStatus;

        public RadioViewHolder(@NonNull View itemView) {
            super(itemView);
            stationImage = itemView.findViewById(R.id.stationImage);
            stationName = itemView.findViewById(R.id.stationName);
            soundStatus = itemView.findViewById(R.id.soundStatus);
        }
    }
}

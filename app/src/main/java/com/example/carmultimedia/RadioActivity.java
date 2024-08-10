package com.example.carmultimedia;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class RadioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);

        ImageButton homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.radioRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Hardcoded list of radio stations
        List<RadioStation> radioStations = Arrays.asList(
                new RadioStation("https://www.isramedia.net/images/users/2/ad5ae1f87813719d25c984cbc7cc1d9f.jpg", "https://glzwizzlv.bynetcdn.com/glglz_mp3", "glglz"),
                new RadioStation("https://cdn.instant.audio/images/logos/radios-org-il/kol-israel-reshet-gimel.png", "https://27743.live.streamtheworld.com/KAN_GIMMEL.mp3?dist=radios", "kan gimel"),
                new RadioStation("https://cdn.instant.audio/images/logos/radios-org-il/galei-zahal.png", "https://glzwizzlv.bynetcdn.com/glz_mp3?awCollectionId=misc&awEpisodeId=glz", "Galei Zahal")
                // Add more stations here
        );

        RadioAdapter adapter = new RadioAdapter(radioStations, this);
        recyclerView.setAdapter(adapter);
    }
}

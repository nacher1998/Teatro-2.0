package com.example.teatro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teatro.model.Seat;
import com.example.teatro.model.SeatAdapter;

import java.util.ArrayList;
import java.util.List;

public class SeatSelectionActivity extends AppCompatActivity {

    private static final int COLUMNS = 10;
    private static final int ROWS = 8;

    private SeatAdapter seatAdapter;
    private List<Seat> seatList;
    private List<Seat> currentSelection = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        RecyclerView recyclerSeats = findViewById(R.id.recyclerSeats);
        AppCompatButton buttonConfirm = findViewById(R.id.buttonConfirm);

        recyclerSeats.setHasFixedSize(true);
        recyclerSeats.setLayoutManager(new GridLayoutManager(this, COLUMNS));

        seatList = new ArrayList<>();
        loadSeats();

        seatAdapter = new SeatAdapter(seatList, selected -> {
            currentSelection = selected;
            String label = selected.isEmpty()
                    ? "CONFIRMAR ASIENTOS"
                    : "CONFIRMAR " + selected.size() + " ASIENTO(S)";
            buttonConfirm.setText(label);
        });

        recyclerSeats.setAdapter(seatAdapter);

        buttonConfirm.setOnClickListener(v -> {
            if (currentSelection.isEmpty()) {
                Toast.makeText(this, "Selecciona al menos un asiento", Toast.LENGTH_SHORT).show();
                return;
            }

            // Build seat string e.g. "2-5  3-7  4-1"
            StringBuilder sb = new StringBuilder();
            for (Seat s : currentSelection) {
                sb.append(s.getFila()).append("-").append(s.getNumero()).append("  ");
            }

            // Get event details passed from DetalleEventoActivity (with fallbacks)
            String eventName = getIntent().getStringExtra("EVENT_NAME");
            String eventDate = getIntent().getStringExtra("EVENT_DATE");
            final String finalEventName = (eventName != null) ? eventName : "Obra de Teatro";
            final String finalEventDate = (eventDate != null) ? eventDate : "15 Jul 2025 · 20:00";

            // Navigate to ConfirmBookingActivity
            Intent intent = new Intent(SeatSelectionActivity.this, ConfirmBookingActivity.class);
            intent.putExtra("EVENT_NAME", finalEventName);
            intent.putExtra("EVENT_DATE", finalEventDate);
            intent.putExtra("SELECTED_SEATS", sb.toString().trim());
            intent.putExtra("SEAT_COUNT", currentSelection.size());
            startActivity(intent);
        });
    }

    private void loadSeats() {
        // Seats pre-sold (from DB / backend — hardcoded here for now)
        List<String> soldIds = new ArrayList<>();
        soldIds.add("seat_2_5");
        soldIds.add("seat_2_6");
        soldIds.add("seat_4_3");
        soldIds.add("seat_4_4");
        soldIds.add("seat_6_8");
        soldIds.add("seat_3_1");
        soldIds.add("seat_3_2");
        soldIds.add("seat_5_7");
        soldIds.add("seat_5_8");
        soldIds.add("seat_5_9");

        for (int fila = 1; fila <= ROWS; fila++) {
            for (int numero = 1; numero <= COLUMNS; numero++) {
                String id = "seat_" + fila + "_" + numero;
                String estado = soldIds.contains(id) ? "vendido" : "disponible";
                seatList.add(new Seat(id, fila, numero, estado));
            }
        }
    }
}
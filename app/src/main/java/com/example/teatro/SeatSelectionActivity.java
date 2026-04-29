package com.example.teatro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teatro.model.Seat;
import com.example.teatro.model.SeatAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SeatSelectionActivity extends AppCompatActivity {

    private static final int COLUMNS = 10;
    private static final int ROWS = 8;

    // ── ahora campo de instancia para poder usarlo en onResume ──
    private AppCompatButton buttonConfirm;

    private SeatAdapter seatAdapter;
    private List<Seat> seatList;
    private List<Seat> currentSelection = new ArrayList<>();

    private String funcionId;

    private final OkHttpClient client = new OkHttpClient();

    private Set<String> occupiedSeats = new HashSet<>();
    private Map<String, String> seatMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        RecyclerView recyclerSeats = findViewById(R.id.recyclerSeats);
        buttonConfirm = findViewById(R.id.buttonConfirm); // campo de instancia

        funcionId = getIntent().getStringExtra("FUNCION_ID");
        Log.d("FUNCION_ID", "FUNCION_ID = " + funcionId);

        recyclerSeats.setHasFixedSize(true);
        recyclerSeats.setLayoutManager(new GridLayoutManager(this, COLUMNS));

        seatList = new ArrayList<>();

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

            StringBuilder sb = new StringBuilder();
            for (Seat s : currentSelection) {
                sb.append(s.getFila())
                        .append("-")
                        .append(s.getNumero())
                        .append("  ");
            }

            String eventName = getIntent().getStringExtra("EVENT_NAME");
            String eventDate = getIntent().getStringExtra("EVENT_DATE");

            String finalEventName = (eventName != null) ? eventName : "Obra de Teatro";
            String finalEventDate = (eventDate != null) ? eventDate : "15 Jul 2025 · 20:00";

            ArrayList<String> butacaIds = new ArrayList<>();
            for (Seat s : currentSelection) {
                if (s.getId() != null) butacaIds.add(s.getId());
            }

            Intent intent = new Intent(SeatSelectionActivity.this, ConfirmBookingActivity.class);
            intent.putExtra("EVENT_NAME", finalEventName);
            intent.putExtra("EVENT_DATE", finalEventDate);
            intent.putExtra("SELECTED_SEATS", sb.toString().trim());
            intent.putExtra("SEAT_COUNT", currentSelection.size());
            intent.putExtra("FUNCION_ID", funcionId);
            intent.putStringArrayListExtra("BUTACA_IDS", butacaIds);

            startActivity(intent);
        });
        AppCompatButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());
    }

    // ── CLAVE: recargar siempre que la pantalla vuelve al primer plano ──
    @Override
    protected void onResume() {
        super.onResume();
        currentSelection.clear();
        buttonConfirm.setText("CONFIRMAR ASIENTOS");

        loadSeatsFromDB(funcionId);
    }

    private void loadSeatsFromDB(String funcionId) {

        String url =
                "https://mhofxrmxsegjzssutzru.supabase.co/rest/v1/butaca_funcion"
                        + "?select=estado,butaca_id,butaca:butaca_id(fila,numero)"
                        + "&funcion_id=eq." + funcionId;

        String anonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1ob2Z4cm14c2Vnanpzc3V0enJ1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA3NTEyNzEsImV4cCI6MjA4NjMyNzI3MX0.d48dzdbvgVXw8OtTmYSv7UVWUka0hXq8CYyQexdFdko";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", anonKey)
                .addHeader("Authorization", "Bearer " + anonKey)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(SeatSelectionActivity.this,
                                "Error cargando butacas", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (!response.isSuccessful()) {
                    Log.e("SEATS", "HTTP error: " + response.code());
                    return;
                }

                String body = response.body() != null ? response.body().string() : "[]";
                Log.d("SEATS", "Response body: " + body);

                try {
                    JSONArray array = new JSONArray(body);

                    occupiedSeats.clear();
                    seatMap.clear();

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject obj = array.getJSONObject(i);

                        String estado = obj.getString("estado");
                        String butacaId = obj.getString("butaca_id");

                        JSONObject butaca = obj.getJSONObject("butaca");
                        int fila = butaca.getInt("fila");
                        int numero = butaca.getInt("numero");

                        String key = fila + "_" + numero;

                        seatMap.put(key, butacaId);

                        if ("vendido".equals(estado) || "no_disponible".equals(estado)) {
                            occupiedSeats.add(key);
                        }
                    }

                    runOnUiThread(() -> buildSeats());

                } catch (Exception e) {
                    Log.e("SEATS", "Parse error", e);
                }
            }
        });
    }

    private void buildSeats() {

        seatList.clear();

        for (int fila = 1; fila <= ROWS; fila++) {
            for (int numero = 1; numero <= COLUMNS; numero++) {

                String key = fila + "_" + numero;
                String butacaId = seatMap.get(key);
                String estado = occupiedSeats.contains(key) ? "vendido" : "disponible";

                seatList.add(new Seat(butacaId, fila, numero, estado));
            }
        }

        seatAdapter.notifyDataSetChanged();
    }
}
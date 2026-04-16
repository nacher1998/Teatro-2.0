package com.example.teatro;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConfirmBookingActivity extends AppCompatActivity {

    private static final double PRICE_PER_SEAT = 18.50;

    // Field so updateSeatsAndProceed can re-enable it on error
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);

        String funcionId = getIntent().getStringExtra("FUNCION_ID");

        // --- Bind views ---
        TextView tvEventName  = findViewById(R.id.tvConfirmEventName);
        TextView tvEventDate  = findViewById(R.id.tvConfirmEventDate);
        TextView tvSeats      = findViewById(R.id.tvConfirmSeats);
        TextView tvSeatCount  = findViewById(R.id.tvConfirmSeatCount);
        TextView tvTotalPrice = findViewById(R.id.tvConfirmTotalPrice);
        btnConfirm            = findViewById(R.id.btnConfirmBooking);
        Button btnBack        = findViewById(R.id.btnConfirmBack);

        // --- Retrieve data passed from SeatSelectionActivity ---
        String eventName = getIntent().getStringExtra("EVENT_NAME");
        String eventDate = getIntent().getStringExtra("EVENT_DATE");
        String seatsStr  = getIntent().getStringExtra("SELECTED_SEATS");
        int    seatCount = getIntent().getIntExtra("SEAT_COUNT", 1);

        final String finalEventName = (eventName != null) ? eventName : "Obra de Teatro";
        final String finalEventDate = (eventDate != null) ? eventDate : "15 Jul 2025 · 20:00";
        final String finalSeatsStr  = (seatsStr  != null) ? seatsStr  : "—";
        final int    finalSeatCount = seatCount;

        // --- Populate views ---
        tvEventName.setText(finalEventName);
        tvEventDate.setText(finalEventDate);
        tvSeats.setText(finalSeatsStr);
        tvSeatCount.setText(finalSeatCount + (finalSeatCount == 1 ? " asiento" : " asientos"));

        final double total = finalSeatCount * PRICE_PER_SEAT;
        tvTotalPrice.setText(String.format("%.2f €", total));

        // --- Navigation: back to seat selection ---
        btnBack.setOnClickListener(v -> finish());

        // --- Confirm: patch seats then go to payment ---
        btnConfirm.setOnClickListener(v -> {
            btnConfirm.setEnabled(false);
            updateSeatsAndProceed(funcionId, finalEventName, finalEventDate,
                    finalSeatsStr, finalSeatCount, total);
        });
    }

    private void updateSeatsAndProceed(String funcionId, String eventName,
                                       String eventDate, String seatsStr,
                                       int seatCount, double total) {

        ArrayList<String> butacaIds = getIntent().getStringArrayListExtra("BUTACA_IDS");
        Log.d("PATCH", "butacaIds = " + butacaIds);


        if (butacaIds == null || butacaIds.isEmpty()) {
            proceedToPayment(eventName, eventDate, seatsStr, seatCount, total);
            return;
        }

        String inClause = "(" + TextUtils.join(",", butacaIds) + ")";
        String url = "https://mhofxrmxsegjzssutzru.supabase.co/rest/v1/butaca_funcion"
                + "?funcion_id=eq." + funcionId
                + "&butaca_id=in." + inClause;

        String anonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1ob2Z4cm14c2Vnanpzc3V0enJ1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA3NTEyNzEsImV4cCI6MjA4NjMyNzI3MX0.d48dzdbvgVXw8OtTmYSv7UVWUka0hXq8CYyQexdFdko";

        RequestBody body = RequestBody.create(
                "{\"estado\":\"vendido\"}",
                MediaType.get("application/json")
        );
        Log.d("PATCH", "funcionId = " + funcionId);
        Log.d("PATCH", "url = " + url);

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .addHeader("apikey", anonKey)
                .addHeader("Authorization", "Bearer " + anonKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    btnConfirm.setEnabled(true);
                    Toast.makeText(ConfirmBookingActivity.this,
                            "Error al reservar asientos", Toast.LENGTH_SHORT).show();
                });
            }


            @Override
            public void onResponse(Call call, Response response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        proceedToPayment(eventName, eventDate, seatsStr, seatCount, total);
                    } else {
                        btnConfirm.setEnabled(true);
                        Toast.makeText(ConfirmBookingActivity.this,
                                "Error HTTP: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void proceedToPayment(String eventName, String eventDate,
                                  String seatsStr, int seatCount, double total) {
        Intent intent = new Intent(ConfirmBookingActivity.this, PaymentActivity.class);
        intent.putExtra("EVENT_NAME",     eventName);
        intent.putExtra("EVENT_DATE",     eventDate);
        intent.putExtra("SELECTED_SEATS", seatsStr);
        intent.putExtra("SEAT_COUNT",     seatCount);
        intent.putExtra("TOTAL_PRICE",    String.format("%.2f €", total));
        startActivity(intent);
    }
}
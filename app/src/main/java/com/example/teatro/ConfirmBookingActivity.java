package com.example.teatro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * ConfirmBookingActivity
 * Displays a summary of the selected event and seats before final booking.
 * Receives event name, date, and seat list via Intent from SeatSelectionActivity.
 */
public class ConfirmBookingActivity extends AppCompatActivity {

    // Price per seat in euros (mock value)
    private static final double PRICE_PER_SEAT = 18.50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);

        // --- Bind views ---
        TextView tvEventName  = findViewById(R.id.tvConfirmEventName);
        TextView tvEventDate  = findViewById(R.id.tvConfirmEventDate);
        TextView tvSeats      = findViewById(R.id.tvConfirmSeats);
        TextView tvSeatCount  = findViewById(R.id.tvConfirmSeatCount);
        TextView tvTotalPrice = findViewById(R.id.tvConfirmTotalPrice);
        Button   btnConfirm   = findViewById(R.id.btnConfirmBooking);
        Button   btnBack      = findViewById(R.id.btnConfirmBack);

        // --- Retrieve data passed from SeatSelectionActivity ---
        String eventName = getIntent().getStringExtra("EVENT_NAME");
        String eventDate = getIntent().getStringExtra("EVENT_DATE");
        String seatsStr  = getIntent().getStringExtra("SELECTED_SEATS");
        int    seatCount = getIntent().getIntExtra("SEAT_COUNT", 1);

        // Fallback values — assigned once so they remain effectively final
        final String finalEventName = (eventName != null) ? eventName : "Obra de Teatro";
        final String finalEventDate = (eventDate != null) ? eventDate : "15 Jul 2025 · 20:00";
        final String finalSeatsStr  = (seatsStr  != null) ? seatsStr  : "—";
        final int    finalSeatCount = seatCount;

        // --- Populate views ---
        tvEventName.setText(finalEventName);
        tvEventDate.setText(finalEventDate);
        tvSeats.setText(finalSeatsStr);
        tvSeatCount.setText(finalSeatCount + (finalSeatCount == 1 ? " asiento" : " asientos"));

        // Calculate mock total and display it
        final double total = finalSeatCount * PRICE_PER_SEAT;
        tvTotalPrice.setText(String.format("%.2f €", total));

        // --- Navigation: back to seat selection ---
        btnBack.setOnClickListener(v -> finish());

        // --- Navigation: proceed to TicketActivity on confirm ---
        btnConfirm.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmBookingActivity.this, PaymentActivity.class);
            intent.putExtra("EVENT_NAME",     finalEventName);
            intent.putExtra("EVENT_DATE",     finalEventDate);
            intent.putExtra("SELECTED_SEATS", finalSeatsStr);
            intent.putExtra("SEAT_COUNT",     finalSeatCount);
            intent.putExtra("TOTAL_PRICE",    String.format("%.2f €", total));
            startActivity(intent);
        });
    }
}
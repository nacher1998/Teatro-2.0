package com.example.teatro;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * PaymentActivity
 * Mock payment screen. Accepts any card data and proceeds to TicketActivity.
 * No real payment processing — purely UI.
 *
 * Receives from ConfirmBookingActivity:
 *   EVENT_NAME, EVENT_DATE, SELECTED_SEATS, SEAT_COUNT, TOTAL_PRICE
 */
public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // --- Bind views ---
        TextView tvTotalPrice  = findViewById(R.id.tvPaymentTotal);
        EditText etCardNumber  = findViewById(R.id.etCardNumber);
        EditText etCardHolder  = findViewById(R.id.etCardHolder);
        EditText etExpiry      = findViewById(R.id.etExpiry);
        EditText etCvv         = findViewById(R.id.etCvv);
        Button   btnPay        = findViewById(R.id.btnPay);
        Button   btnBack       = findViewById(R.id.btnPaymentBack);

        // --- Read extras from ConfirmBookingActivity ---
        final String eventName  = getIntent().getStringExtra("EVENT_NAME");
        final String eventDate  = getIntent().getStringExtra("EVENT_DATE");
        final String seatsStr   = getIntent().getStringExtra("SELECTED_SEATS");
        final int    seatCount  = getIntent().getIntExtra("SEAT_COUNT", 1);
        final String totalPrice = getIntent().getStringExtra("TOTAL_PRICE");

        // Show total prominently
        tvTotalPrice.setText(totalPrice != null ? totalPrice : "—");

        // --- Auto-format card number as "XXXX XXXX XXXX XXXX" ---
        etCardNumber.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting = false;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;

                // Strip all spaces first
                String digits = s.toString().replaceAll("\\s", "");
                // Limit to 16 digits
                if (digits.length() > 16) digits = digits.substring(0, 16);

                // Re-insert spaces every 4 digits
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < digits.length(); i++) {
                    if (i > 0 && i % 4 == 0) formatted.append(" ");
                    formatted.append(digits.charAt(i));
                }

                s.replace(0, s.length(), formatted.toString());
                isFormatting = false;
            }
        });

        // --- Auto-format expiry as "MM/YY" ---
        etExpiry.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting = false;
            private int prevLength = 0;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevLength = s.length();
            }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;

                String digits = s.toString().replaceAll("[^\\d]", "");
                if (digits.length() > 4) digits = digits.substring(0, 4);

                String formatted;
                if (digits.length() >= 3) {
                    formatted = digits.substring(0, 2) + "/" + digits.substring(2);
                } else {
                    formatted = digits;
                }

                s.replace(0, s.length(), formatted);
                isFormatting = false;
            }
        });

        // --- Back button ---
        btnBack.setOnClickListener(v -> finish());

        // --- Pay button: validate then go to TicketActivity ---
        btnPay.setOnClickListener(v -> {
            String cardNum    = etCardNumber.getText().toString().replaceAll("\\s", "");
            String cardHolder = etCardHolder.getText().toString().trim();
            String expiry     = etExpiry.getText().toString().trim();
            String cvv        = etCvv.getText().toString().trim();

            // Basic validation — any non-empty values are accepted (mock)
            if (cardNum.length() < 16) {
                etCardNumber.setError("Introduce un número de tarjeta válido");
                etCardNumber.requestFocus();
                return;
            }
            if (cardHolder.isEmpty()) {
                etCardHolder.setError("Introduce el nombre del titular");
                etCardHolder.requestFocus();
                return;
            }
            if (expiry.length() < 5) {
                etExpiry.setError("Introduce la fecha de caducidad");
                etExpiry.requestFocus();
                return;
            }
            if (cvv.length() < 3) {
                etCvv.setError("CVV inválido");
                etCvv.requestFocus();
                return;
            }

            // All good — simulate payment and go to ticket
            Toast.makeText(this, "Procesando pago...", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(PaymentActivity.this, TicketActivity.class);
            intent.putExtra("EVENT_NAME",     eventName);
            intent.putExtra("EVENT_DATE",     eventDate);
            intent.putExtra("SELECTED_SEATS", seatsStr);
            intent.putExtra("SEAT_COUNT",     seatCount);
            intent.putExtra("TOTAL_PRICE",    totalPrice);
            intent.putExtra("BOOKING_REF",    "TT-" + System.currentTimeMillis() % 100000);
            startActivity(intent);
        });
    }
}
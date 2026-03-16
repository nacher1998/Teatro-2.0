package com.example.teatro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * TicketActivity
 * Shown after a successful booking confirmation.
 * Displays event details, a QR code (generated via ZXing), and offers
 * a mock PDF download using Android's built-in PdfDocument API.
 *
 * Dependencies needed in build.gradle (app):
 *   implementation 'com.journeyapps:zxing-android-embedded:4.3.0'
 *   implementation 'com.google.zxing:core:3.5.1'
 */
public class TicketActivity extends AppCompatActivity {

    // Size in pixels of the generated QR bitmap
    private static final int QR_SIZE = 512;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        // --- Bind views ---
        TextView  tvBookingRef  = findViewById(R.id.tvTicketBookingRef);
        TextView  tvEventName   = findViewById(R.id.tvTicketEventName);
        TextView  tvEventDate   = findViewById(R.id.tvTicketEventDate);
        TextView  tvSeats       = findViewById(R.id.tvTicketSeats);
        TextView  tvTotalPrice  = findViewById(R.id.tvTicketTotalPrice);
        ImageView ivQr          = findViewById(R.id.ivTicketQr);
        Button    btnDownload   = findViewById(R.id.btnTicketDownloadPdf);
        Button    btnCartelera  = findViewById(R.id.btnTicketBackCartelera);

        // --- Read extras forwarded from ConfirmBookingActivity ---
        String eventName   = getIntent().getStringExtra("EVENT_NAME");
        String eventDate   = getIntent().getStringExtra("EVENT_DATE");
        String seatsStr    = getIntent().getStringExtra("SELECTED_SEATS");
        String totalPrice  = getIntent().getStringExtra("TOTAL_PRICE");
        String bookingRef  = getIntent().getStringExtra("BOOKING_REF");

        // Fallbacks
        if (eventName  == null) eventName  = "Obra de Teatro";
        if (eventDate  == null) eventDate  = "15 Jul 2025 · 20:00";
        if (seatsStr   == null) seatsStr   = "—";
        if (totalPrice == null) totalPrice = "18.50 €";
        if (bookingRef == null) bookingRef = "TT-00001";

        // --- Populate text views ---
        tvBookingRef.setText("Ref: " + bookingRef);
        tvEventName.setText(eventName);
        tvEventDate.setText(eventDate);
        tvSeats.setText(seatsStr);
        tvTotalPrice.setText(totalPrice);

        // --- Generate QR code ---
        // The QR content encodes all ticket data as a simple string
        String qrContent = "REF:" + bookingRef
                + "|EVENTO:" + eventName
                + "|FECHA:"  + eventDate
                + "|ASIENTOS:" + seatsStr
                + "|TOTAL:"  + totalPrice;

        Bitmap qrBitmap = generateQrCode(qrContent);
        if (qrBitmap != null) {
            ivQr.setImageBitmap(qrBitmap);
        }

        // --- Download PDF (mock) ---
        final String finalEventName  = eventName;
        final String finalEventDate  = eventDate;
        final String finalSeats      = seatsStr;
        final String finalPrice      = totalPrice;
        final String finalRef        = bookingRef;
        final Bitmap finalQr         = qrBitmap;

        btnDownload.setOnClickListener(v ->
                saveMockPdf(finalRef, finalEventName, finalEventDate, finalSeats, finalPrice, finalQr));

        // --- Go back to cartelera, clearing the back stack ---
        btnCartelera.setOnClickListener(v -> {
            Intent intent = new Intent(TicketActivity.this, EventosActivity.class);
            // Clear all activities on top of CarteleraActivity so Back doesn't revisit booking flow
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    // ─────────────────────────────────────────────
    // QR generation using ZXing
    // ─────────────────────────────────────────────
    /**
     * Converts a string into a QR code Bitmap using ZXing's QRCodeWriter.
     * Returns null if encoding fails.
     */
    private Bitmap generateQrCode(String content) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
            Bitmap bmp = Bitmap.createBitmap(QR_SIZE, QR_SIZE, Bitmap.Config.RGB_565);
            for (int x = 0; x < QR_SIZE; x++) {
                for (int y = 0; y < QR_SIZE; y++) {
                    // Black modules → dark pixel; white modules → light pixel
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ─────────────────────────────────────────────
    // PDF generation using Android PdfDocument
    // ─────────────────────────────────────────────
    /**
     * Creates a simple PDF file in the app's Downloads folder containing
     * the ticket details and the QR code bitmap.
     * NOTE: For Android 10+ (API 29+) you may need MediaStore or
     * WRITE_EXTERNAL_STORAGE permission for older APIs.
     */
    private void saveMockPdf(String ref, String eventName, String date,
                             String seats, String price, Bitmap qrBitmap) {

        PdfDocument document = new PdfDocument();
        // A4-ish page at 72 dpi portrait
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // ── Background ──
        paint.setColor(Color.parseColor("#1A1A1A"));
        canvas.drawRect(0, 0, 595, 842, paint);

        // ── Title ──
        paint.setColor(Color.parseColor("#C0392B"));
        paint.setTextSize(36f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Ticket Teatro", 297, 60, paint);

        // ── Booking Ref ──
        paint.setColor(Color.WHITE);
        paint.setTextSize(14f);
        canvas.drawText("Referencia: " + ref, 297, 90, paint);

        // ── Divider ──
        paint.setColor(Color.parseColor("#C0392B"));
        paint.setStrokeWidth(2f);
        canvas.drawLine(40, 105, 555, 105, paint);

        // ── Details ──
        paint.setColor(Color.WHITE);
        paint.setTextSize(13f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Evento:   " + eventName, 40, 135, paint);
        canvas.drawText("Fecha:    " + date,       40, 160, paint);
        canvas.drawText("Asientos: " + seats,      40, 185, paint);
        canvas.drawText("Total:    " + price,       40, 210, paint);

        // ── QR Code ──
        if (qrBitmap != null) {
            // Scale QR to fit nicely centred
            Bitmap scaledQr = Bitmap.createScaledBitmap(qrBitmap, 200, 200, false);
            canvas.drawBitmap(scaledQr, 197, 250, null);
        }

        // ── Footer ──
        paint.setColor(Color.parseColor("#FFCCCC"));
        paint.setTextSize(10f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Presenta este ticket en taquilla · Ticket Teatro", 297, 820, paint);

        document.finishPage(page);

        // Save to Downloads directory
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, "ticket_" + ref + ".pdf");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            document.writeTo(fos);
            document.close();
            Toast.makeText(this,
                    "PDF guardado en Descargas:\n" + file.getName(),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            document.close();
            e.printStackTrace();
            Toast.makeText(this,
                    "Error al guardar el PDF: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}
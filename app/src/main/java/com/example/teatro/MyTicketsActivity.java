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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teatro.model.MyTicketsAdapter;
import com.example.teatro.model.Ticket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MyTicketsActivity
 * Displays all tickets a user has booked in a scrollable RecyclerView list.
 * Each item shows event details, a QR code, and a "Download PDF" button.
 *
 * Mock data is used here. In a real app, replace getMockTickets() with a
 * Room DB query or API call.
 */
public class MyTicketsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        // --- Bind views ---
        RecyclerView recyclerTickets = findViewById(R.id.recyclerMyTickets);
        Button       btnBack         = findViewById(R.id.btnMyTicketsBack);

        // --- Set up RecyclerView ---
        recyclerTickets.setLayoutManager(new LinearLayoutManager(this));
        recyclerTickets.setHasFixedSize(true);

        // Load mock tickets (replace with real data source when ready)
        List<Ticket> tickets = getMockTickets();

        // Create adapter; the lambda handles PDF export for each item
        MyTicketsAdapter adapter = new MyTicketsAdapter(tickets, this::savePdfForTicket);
        recyclerTickets.setAdapter(adapter);

        // --- Navigate back to Cartelera ---
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(MyTicketsActivity.this, EventosActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    // ─────────────────────────────────────────────
    // Mock data
    // ─────────────────────────────────────────────
    /**
     * Returns a hardcoded list of past tickets.
     * Replace this method's body with real data (Room DB, SharedPreferences, etc.)
     */
    private List<Ticket> getMockTickets() {
        List<Ticket> list = new ArrayList<>();
        list.add(new Ticket("TT-11201", "Hamlet",               "12 Jun 2025 · 20:00", "A-3  A-4",     "37.00 €"));
        list.add(new Ticket("TT-23445", "El Principito",        "25 Jun 2025 · 19:30", "C-7",          "18.50 €"));
        list.add(new Ticket("TT-34821", "La Traviata",          "04 Jul 2025 · 21:00", "B-2  B-3  B-4","55.50 €"));
        list.add(new Ticket("TT-40012", "Bodas de Sangre",      "15 Jul 2025 · 20:00", "D-1  D-2",     "37.00 €"));
        list.add(new Ticket("TT-51117", "Don Quijote en Escena","22 Jul 2025 · 18:00", "E-5",          "18.50 €"));
        return list;
    }

    // ─────────────────────────────────────────────
    // PDF generation (reused from TicketActivity)
    // ─────────────────────────────────────────────
    /**
     * Generates and saves a PDF for the given ticket.
     * The qrBitmap is the one already rendered in the RecyclerView item.
     */
    private void savePdfForTicket(Ticket ticket, Bitmap qrBitmap) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // Background
        paint.setColor(Color.parseColor("#1A1A1A"));
        canvas.drawRect(0, 0, 595, 842, paint);

        // Title
        paint.setColor(Color.parseColor("#C0392B"));
        paint.setTextSize(36f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Ticket Teatro", 297, 60, paint);

        // Ref
        paint.setColor(Color.WHITE);
        paint.setTextSize(14f);
        canvas.drawText("Referencia: " + ticket.getBookingRef(), 297, 90, paint);

        // Divider
        paint.setColor(Color.parseColor("#C0392B"));
        paint.setStrokeWidth(2f);
        canvas.drawLine(40, 105, 555, 105, paint);

        // Details
        paint.setColor(Color.WHITE);
        paint.setTextSize(13f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Evento:   " + ticket.getEventName(), 40, 135, paint);
        canvas.drawText("Fecha:    " + ticket.getEventDate(), 40, 160, paint);
        canvas.drawText("Asientos: " + ticket.getSeats(),     40, 185, paint);
        canvas.drawText("Total:    " + ticket.getTotalPrice(),40, 210, paint);

        // QR
        if (qrBitmap != null) {
            Bitmap scaled = Bitmap.createScaledBitmap(qrBitmap, 200, 200, false);
            canvas.drawBitmap(scaled, 197, 250, null);
        }

        // Footer
        paint.setColor(Color.parseColor("#FFCCCC"));
        paint.setTextSize(10f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Presenta este ticket en taquilla · Ticket Teatro", 297, 820, paint);

        document.finishPage(page);

        File downloadsDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, "ticket_" + ticket.getBookingRef() + ".pdf");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            document.writeTo(fos);
            document.close();
            Toast.makeText(this, "PDF guardado: " + file.getName(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            document.close();
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
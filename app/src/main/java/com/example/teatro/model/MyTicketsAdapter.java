package com.example.teatro.model;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teatro.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.List;

/**
 * MyTicketsAdapter
 * Binds a list of Ticket objects to item_ticket.xml cards in MyTicketsActivity's RecyclerView.
 * Each card shows event details, a QR code, and a Download PDF button.
 *
 * The adapter exposes an OnDownloadClickListener so MyTicketsActivity can
 * handle the PDF export logic (keeps Activity/Adapter concerns separated).
 */
public class MyTicketsAdapter extends RecyclerView.Adapter<MyTicketsAdapter.TicketViewHolder> {

    /** Callback interface so the Activity handles the PDF save logic */
    public interface OnDownloadClickListener {
        void onDownload(Ticket ticket, Bitmap qrBitmap);
    }

    private static final int QR_SIZE = 256;

    private final List<Ticket> tickets;
    private final OnDownloadClickListener downloadListener;

    public MyTicketsAdapter(List<Ticket> tickets, OnDownloadClickListener listener) {
        this.tickets          = tickets;
        this.downloadListener = listener;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);
        holder.bind(ticket);
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    // ──────────────────────────────────────────────────────
    // ViewHolder
    // ──────────────────────────────────────────────────────
    class TicketViewHolder extends RecyclerView.ViewHolder {

        private final TextView  tvRef;
        private final TextView  tvName;
        private final TextView  tvDate;
        private final TextView  tvSeats;
        private final TextView  tvPrice;
        private final ImageView ivQr;
        private final Button    btnDownload;

        TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRef       = itemView.findViewById(R.id.tvItemTicketRef);
            tvName      = itemView.findViewById(R.id.tvItemTicketName);
            tvDate      = itemView.findViewById(R.id.tvItemTicketDate);
            tvSeats     = itemView.findViewById(R.id.tvItemTicketSeats);
            tvPrice     = itemView.findViewById(R.id.tvItemTicketPrice);
            ivQr        = itemView.findViewById(R.id.ivItemTicketQr);
            btnDownload = itemView.findViewById(R.id.btnItemDownloadPdf);
        }

        void bind(Ticket ticket) {
            tvRef.setText("Ref: " + ticket.getBookingRef());
            tvName.setText(ticket.getEventName());
            tvDate.setText(ticket.getEventDate());
            tvSeats.setText(ticket.getSeats());
            tvPrice.setText(ticket.getTotalPrice());

            // Generate QR for this ticket
            Bitmap qr = generateQrBitmap(
                    "REF:" + ticket.getBookingRef()
                            + "|EVENTO:" + ticket.getEventName()
                            + "|FECHA:"  + ticket.getEventDate()
                            + "|ASIENTOS:" + ticket.getSeats());

            if (qr != null) {
                ivQr.setImageBitmap(qr);
            }

            // Delegate PDF export to the Activity
            btnDownload.setOnClickListener(v -> {
                if (downloadListener != null) {
                    downloadListener.onDownload(ticket, qr);
                }
            });
        }

        /** Generates a small QR Bitmap using ZXing */
        private Bitmap generateQrBitmap(String content) {
            try {
                BitMatrix matrix = new QRCodeWriter()
                        .encode(content, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
                Bitmap bmp = Bitmap.createBitmap(QR_SIZE, QR_SIZE, Bitmap.Config.RGB_565);
                for (int x = 0; x < QR_SIZE; x++) {
                    for (int y = 0; y < QR_SIZE; y++) {
                        bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
                return bmp;
            } catch (WriterException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
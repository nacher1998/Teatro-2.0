package com.example.teatro.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teatro.R;

import java.util.ArrayList;
import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {

    public interface OnSeatSelectedListener {
        void onSelectionChanged(List<Seat> selectedSeats);
    }

    private final List<Seat> seatList;
    private final OnSeatSelectedListener listener;

    public SeatAdapter(List<Seat> seatList, OnSeatSelectedListener listener) {
        this.seatList = seatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seat, parent, false);

        int screenWidth = parent.getContext().getResources().getDisplayMetrics().widthPixels;
        int cellSize = screenWidth / 10;
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(cellSize, cellSize);
        view.setLayoutParams(params);

        return new SeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        Seat seat = seatList.get(position);
        holder.buttonSeat.setText(seat.getFila() + "\n" + seat.getNumero());

        switch (seat.getEstado()) {
            case "disponible":
                holder.buttonSeat.setBackground(
                        ContextCompat.getDrawable(holder.buttonSeat.getContext(), R.drawable.bg_seat_available));
                holder.buttonSeat.setEnabled(true);
                holder.buttonSeat.setAlpha(1f);
                break;
            case "vendido":
                holder.buttonSeat.setBackground(
                        ContextCompat.getDrawable(holder.buttonSeat.getContext(), R.drawable.bg_seat_sold));
                holder.buttonSeat.setEnabled(false);
                holder.buttonSeat.setAlpha(0.5f);
                break;
            case "seleccionado":
                holder.buttonSeat.setBackground(
                        ContextCompat.getDrawable(holder.buttonSeat.getContext(), R.drawable.bg_seat_selected));
                holder.buttonSeat.setEnabled(true);
                holder.buttonSeat.setAlpha(1f);
                break;
            default:
                holder.buttonSeat.setAlpha(0.3f);
                holder.buttonSeat.setEnabled(false);
                break;
        }

        holder.buttonSeat.setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos == RecyclerView.NO_POSITION) return; // ✅ fix: NO_POSITION no NO_ID

            Seat current = seatList.get(adapterPos); // ✅ usar adapterPos, no la lambda capturada
            if ("disponible".equals(current.getEstado())) {
                current.setEstado("seleccionado");
            } else if ("seleccionado".equals(current.getEstado())) {
                current.setEstado("disponible");
            }
            notifyItemChanged(adapterPos);

            if (listener != null) listener.onSelectionChanged(getSelectedSeats());
        });
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    public List<Seat> getSelectedSeats() {
        List<Seat> selected = new ArrayList<>();
        for (Seat s : seatList) {
            if ("seleccionado".equals(s.getEstado())) selected.add(s);
        }
        return selected;
    }

    public static class SeatViewHolder extends RecyclerView.ViewHolder {
        AppCompatButton buttonSeat;

        public SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonSeat = itemView.findViewById(R.id.buttonSeat);
        }
    }
}
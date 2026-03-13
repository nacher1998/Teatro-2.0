package com.example.teatro.models;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teatro.R;
import com.example.teatro.models.Seat;

import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {

    private List<Seat> seatList;

    public SeatAdapter(List<Seat> seatList) {
        this.seatList = seatList;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seat, parent, false);

        return new SeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {

        Seat seat = seatList.get(position);

        holder.buttonSeat.setText(seat.getFila() + "-" + seat.getNumero());

        switch (seat.getEstado()) {

            case "disponible":
                holder.buttonSeat.setBackgroundColor(Color.GREEN);
                holder.buttonSeat.setEnabled(true);
                break;

            case "vendido":
                holder.buttonSeat.setBackgroundColor(Color.RED);
                holder.buttonSeat.setEnabled(false);
                break;

            case "seleccionado":
                holder.buttonSeat.setBackgroundColor(Color.BLUE);
                holder.buttonSeat.setEnabled(true);
                break;
        }

        holder.buttonSeat.setOnClickListener(v -> {

            if (seat.getEstado().equals("disponible")) {
                seat.setEstado("seleccionado");
            }
            else if (seat.getEstado().equals("seleccionado")) {
                seat.setEstado("disponible");
            }

            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    public static class SeatViewHolder extends RecyclerView.ViewHolder {

        Button buttonSeat;

        public SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonSeat = itemView.findViewById(R.id.buttonSeat);
        }
    }
}
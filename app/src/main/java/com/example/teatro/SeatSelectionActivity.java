package com.example.teatro;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teatro.models.Seat;
import com.example.teatro.models.SeatAdapter;

import java.util.ArrayList;
import java.util.List;

public class SeatSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerSeats;
    private SeatAdapter seatAdapter;
    private List<Seat> seatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        recyclerSeats = findViewById(R.id.recyclerSeats);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 10);
        recyclerSeats.setLayoutManager(gridLayoutManager);

        seatList = new ArrayList<>();

        loadSeats();

        seatAdapter = new SeatAdapter(seatList);
        recyclerSeats.setAdapter(seatAdapter);
    }

    private void loadSeats() {

        for (int fila = 1; fila <= 8; fila++) {

            for (int numero = 1; numero <= 10; numero++) {

                String estado = "disponible";

                if ((fila == 2 && numero == 5) ||
                        (fila == 4 && numero == 3) ||
                        (fila == 6 && numero == 8)) {

                    estado = "vendido";
                }

                Seat seat = new Seat(
                        "seat_" + fila + "_" + numero,
                        fila,
                        numero,
                        estado
                );

                seatList.add(seat);
            }
        }
    }
}

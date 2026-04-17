package com.example.teatro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class DetalleEventoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_evento);

        TextView tvTitulo      = findViewById(R.id.tvTituloDetalle);
        TextView tvFechaHora   = findViewById(R.id.tvFechaHora);
        TextView tvDescripcion = findViewById(R.id.tvDescripcion);
        ImageView ivCartel     = findViewById(R.id.ivCartel);
        Button btnVolverAtras  = findViewById(R.id.btnVolverAtras);
        Button btnElegirAsiento = findViewById(R.id.btnElegirAsiento);

        // Recibir datos de EventosActivity
        String titulo      = getIntent().getStringExtra("TITULO");
        String fechaHora   = getIntent().getStringExtra("FECHA_HORA");
        String descripcion = getIntent().getStringExtra("DESCRIPCION");
        String cartelUrl   = getIntent().getStringExtra("CARTEL_URL");
        String funcionId   = getIntent().getStringExtra("FUNCION_ID");

        if (titulo != null)      tvTitulo.setText(titulo);
        if (fechaHora != null)   tvFechaHora.setText(fechaHora);
        if (descripcion != null) tvDescripcion.setText(descripcion);

        // Cargar imagen del cartel con Glide
        if (cartelUrl != null && !cartelUrl.isEmpty()) {
            Glide.with(this)
                    .load(cartelUrl)
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivCartel);
        }

        // Botón volver
        btnVolverAtras.setOnClickListener(v -> finish());

        // Botón elegir asiento
        btnElegirAsiento.setOnClickListener(v -> {
            Intent intent = new Intent(DetalleEventoActivity.this, SeatSelectionActivity.class);
            intent.putExtra("FUNCION_ID", funcionId);
            intent.putExtra("EVENT_NAME", titulo);
            intent.putExtra("EVENT_DATE", fechaHora);
            startActivity(intent);
        });
    }
}
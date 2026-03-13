package com.example.teatro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DetalleEventoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_evento);

        // 1. Enlazamos los componentes del XML
        TextView tvTitulo = findViewById(R.id.tvTituloDetalle);
        TextView tvFechaHora = findViewById(R.id.tvFechaHora);
        TextView tvDescripcion = findViewById(R.id.tvDescripcion);
        ImageView ivCartel = findViewById(R.id.ivCartel);

        Button btnVolverAtras = findViewById(R.id.btnVolverAtras);
        Button btnElegirAsiento = findViewById(R.id.btnElegirAsiento);
        View btnBackTop = findViewById(R.id.btnBackTop);

        // 2. Recibimos los datos que nos envía la pantalla anterior (EventosActivity)
        if (getIntent().getExtras() != null) {
            String titulo = getIntent().getStringExtra("TITULO");
            String fechaHora = getIntent().getStringExtra("FECHA_HORA");
            String descripcion = getIntent().getStringExtra("DESCRIPCION");

            // Asignamos los textos
            if (titulo != null) tvTitulo.setText(titulo);
            if (fechaHora != null) tvFechaHora.setText(fechaHora);
            if (descripcion != null) tvDescripcion.setText(descripcion);

            // Nota: Para la imagen, si guardas URLs en la BD, tendrás que usar
            // librerías como Glide o Picasso aquí: Glide.with(this).load(url).into(ivCartel);
        }

        // 3. Lógica del botón VOLVER (sirve para el botón de abajo y la flecha de arriba)
        View.OnClickListener volverListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Esto destruye esta pantalla y te devuelve a la lista
            }
        };
        btnVolverAtras.setOnClickListener(volverListener);
        btnBackTop.setOnClickListener(volverListener);

        // 4. Lógica del botón AVANZAR (Elegir Asiento)
        btnElegirAsiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Como aún no la vamos a hacer, mostramos un mensaje temporal (Toast)
                Toast.makeText(DetalleEventoActivity.this, "Pantalla de asientos en construcción", Toast.LENGTH_SHORT).show();

                /* Cuando la tengas, el código será así:
                Intent intent = new Intent(DetalleEventoActivity.this, AsientosActivity.class);
                startActivity(intent);
                */
            }
        });
    }
}

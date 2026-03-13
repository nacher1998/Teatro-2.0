package com.example.teatro; // ¡IMPORTANTE! Cambia esto por el nombre de tu paquete (ej: com.example.teatro2)

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EventosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Vinculamos esta clase con el XML que acabamos de crear
        setContentView(R.layout.activity_eventos);

        // Configuramos el botón de volver
        TextView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Esto cierra esta pantalla y vuelve a la anterior (MainActivity)
                finish();
            }
        });
        // Ejemplo para el día 5
        TextView day5 = findViewById(R.id.day5);
        day5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí podrías cambiar el texto de "Jue, 17 Agosto" por el del día pulsado
            }
        });
        Intent intent = new Intent(EventosActivity.this, DetalleEventoActivity.class);
// "Empaquetamos" los datos del evento específico que el usuario ha tocado
        intent.putExtra("TITULO", "Romeo y Julieta");
        intent.putExtra("FECHA_HORA", "Jue, 17 Agosto • 20:30");
        intent.putExtra("DESCRIPCION", "La clásica tragedia de William Shakespeare adaptada por...");
// Iniciamos la nueva pantalla
        startActivity(intent);
    }
}
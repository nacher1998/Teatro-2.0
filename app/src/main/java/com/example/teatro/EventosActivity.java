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
        setContentView(R.layout.activity_eventos);

        // --- BOTÓN VOLVER ---
        TextView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Vuelve a MainActivity
            }
        });

        // --- TARJETA 1 ---
        View evento1 = findViewById(R.id.evento1);
        evento1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventosActivity.this, DetalleEventoActivity.class);
                intent.putExtra("TITULO", "El lago de los cisnes");
                intent.putExtra("FECHA_HORA", "Jue, 17 Agosto • 14:00");
                intent.putExtra("DESCRIPCION", "La obra maestra de Tchaikovsky interpretada por la compañía New Adventures. Una experiencia clásica e inolvidable en la Sala Gayarre.");
                startActivity(intent);
            }
        });

        // --- TARJETA 2 ---
        View evento2 = findViewById(R.id.evento2);
        evento2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventosActivity.this, DetalleEventoActivity.class);
                intent.putExtra("TITULO", "Ludovico Einaudi");
                intent.putExtra("FECHA_HORA", "Jue, 17 Agosto • 16:00");
                intent.putExtra("DESCRIPCION", "El aclamado compositor y pianista italiano presenta un concierto íntimo en solitario. Sumérgete en su música minimalista en la Sala Principal.");
                startActivity(intent);
            }
        });

        // --- TARJETA 3 ---
        View evento3 = findViewById(R.id.evento3);
        evento3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventosActivity.this, DetalleEventoActivity.class);
                intent.putExtra("TITULO", "Paco Ibáñez");
                intent.putExtra("FECHA_HORA", "Jue, 17 Agosto • 18:30");
                intent.putExtra("DESCRIPCION", "Vivencias. El legendario cantautor repasa los grandes poemas de la literatura hispana a través de su inconfundible voz y guitarra.");
                startActivity(intent);
            }
        });

        // --- TARJETA 4 ---
        View evento4 = findViewById(R.id.evento4);
        evento4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventosActivity.this, DetalleEventoActivity.class);
                intent.putExtra("TITULO", "Romeo y Julieta");
                intent.putExtra("FECHA_HORA", "Jue, 17 Agosto • 20:30");
                intent.putExtra("DESCRIPCION", "La trágica historia de amor de Shakespeare, adaptada en formato de ópera con la música magistral de Charles Gounod.");
                startActivity(intent);
            }
        });
    }
}
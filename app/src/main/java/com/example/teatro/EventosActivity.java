package com.example.teatro; // ¡IMPORTANTE! Cambia esto por el nombre de tu paquete (ej: com.example.teatro2)

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teatro.model.Evento;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EventosActivity extends AppCompatActivity {

    private TextView tvMesActual, tvCabeceraFecha;
    private GridLayout gridCalendario;
    private LinearLayout layoutEventos;
    private Calendar calendarioActual;
    private Calendar fechaSeleccionada;

    private List<Evento> todosLosEventos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);

        tvMesActual = findViewById(R.id.tvMesActual);
        gridCalendario = findViewById(R.id.gridCalendario);
        layoutEventos = findViewById(R.id.layoutEventos);
        tvCabeceraFecha = findViewById(R.id.tvCabeceraFecha); // El texto grande "Jue, 17 Agosto"

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Inicializar calendarios
        calendarioActual = Calendar.getInstance();
        fechaSeleccionada = Calendar.getInstance();

        // 1. Cargar la base de datos simulada
        cargarEventosSimulados();

        // 2. Configurar botones de mes
        findViewById(R.id.btnMesAnterior).setOnClickListener(v -> {
            calendarioActual.add(Calendar.MONTH, -1);
            dibujarCalendario();
        });

        findViewById(R.id.btnMesSiguiente).setOnClickListener(v -> {
            calendarioActual.add(Calendar.MONTH, 1);
            dibujarCalendario();
        });

        // 3. Dibujar pantalla inicial
        dibujarCalendario();
        actualizarEventos(fechaSeleccionada);
    }

    private void cargarEventosSimulados() {
        todosLosEventos = new ArrayList<>();
        // OJO: En Java Calendar, Enero es 0, Agosto es 7, Septiembre es 8...
        int mesActual = calendarioActual.get(Calendar.MONTH);
        int anioActual = calendarioActual.get(Calendar.YEAR);
        int diaHoy = calendarioActual.get(Calendar.DAY_OF_MONTH);

        // Añadimos eventos al día de hoy
        todosLosEventos.add(new Evento("El lago de los cisnes", "New Adventures", "14:00", "Sala Gayarre", "Descripción de prueba", diaHoy, mesActual, anioActual));
        todosLosEventos.add(new Evento("Romeo y Julieta", "Charles Gounod", "20:30", "Sala Principal", "Descripción de prueba", diaHoy, mesActual, anioActual));

        // Añadimos un evento para mañana
        todosLosEventos.add(new Evento("Concierto Acústico", "Artista Local", "19:00", "Sala Pequeña", "Música en vivo", diaHoy + 1, mesActual, anioActual));
    }

    private void dibujarCalendario() {
        gridCalendario.removeAllViews();

        // Formatear el mes y año (Ej: "agosto 2024")
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
        String mesString = sdf.format(calendarioActual.getTime());
        tvMesActual.setText(mesString.substring(0, 1).toUpperCase() + mesString.substring(1));

        Calendar mesCal = (Calendar) calendarioActual.clone();
        mesCal.set(Calendar.DAY_OF_MONTH, 1);

        int primerDiaSemana = mesCal.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Domingo
        int diasEnMes = mesCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Rellenar espacios vacíos antes del primer día
        for (int i = 0; i < primerDiaSemana; i++) {
            View vacio = LayoutInflater.from(this).inflate(R.layout.item_dia_calendario, gridCalendario, false);
            ((TextView) vacio).setText("");
            gridCalendario.addView(vacio);
        }

        // Rellenar los días del mes
        for (int i = 1; i <= diasEnMes; i++) {
            View vistaDia = LayoutInflater.from(this).inflate(R.layout.item_dia_calendario, gridCalendario, false);
            TextView tvDia = vistaDia.findViewById(R.id.tvDiaTexto);
            tvDia.setText(String.valueOf(i));

            final int diaSeleccionado = i;

            // Marcar si es el día seleccionado actualmente
            if (diaSeleccionado == fechaSeleccionada.get(Calendar.DAY_OF_MONTH) &&
                    calendarioActual.get(Calendar.MONTH) == fechaSeleccionada.get(Calendar.MONTH) &&
                    calendarioActual.get(Calendar.YEAR) == fechaSeleccionada.get(Calendar.YEAR)) {

                tvDia.setBackgroundResource(R.drawable.circle_fill); // Tu fondo rojo
                tvDia.setTextColor(Color.WHITE);
            } else {
                tvDia.setBackgroundColor(Color.TRANSPARENT);
                tvDia.setTextColor(Color.parseColor("#AAAAAA"));
            }

            // Al hacer clic en un día
            tvDia.setOnClickListener(v -> {
                fechaSeleccionada.set(Calendar.YEAR, calendarioActual.get(Calendar.YEAR));
                fechaSeleccionada.set(Calendar.MONTH, calendarioActual.get(Calendar.MONTH));
                fechaSeleccionada.set(Calendar.DAY_OF_MONTH, diaSeleccionado);

                dibujarCalendario(); // Redibujar para mover el círculo rojo
                actualizarEventos(fechaSeleccionada); // Filtrar eventos
            });

            gridCalendario.addView(vistaDia);
        }
    }

    private void actualizarEventos(Calendar fecha) {
        layoutEventos.removeAllViews(); // Limpiar lista anterior

        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        int mes = fecha.get(Calendar.MONTH);
        int anio = fecha.get(Calendar.YEAR);

        // Actualizar texto grande de la fecha (Ej: "Jue, 17 Agosto")
        SimpleDateFormat sdf = new SimpleDateFormat("E, d MMMM", new Locale("es", "ES"));
        String fechaGrande = sdf.format(fecha.getTime());
        if(tvCabeceraFecha != null) tvCabeceraFecha.setText(fechaGrande);

        boolean hayEventos = false;

        for (Evento evento : todosLosEventos) {
            // Filtrar si el evento coincide con la fecha seleccionada
            if (evento.dia == dia && evento.mes == mes && evento.anio == anio) {
                hayEventos = true;

                // Inflar la tarjeta del evento (tu layout item_evento.xml)
                View tarjeta = LayoutInflater.from(this).inflate(R.layout.item_evento, layoutEventos, false);

                TextView tvTitulo = tarjeta.findViewById(R.id.tvTitulo);
                TextView tvSubtitulo = tarjeta.findViewById(R.id.tvSubtitulo);
                TextView tvHora = tarjeta.findViewById(R.id.tvHora);
                TextView tvSala = tarjeta.findViewById(R.id.tvSala);

                tvTitulo.setText(evento.titulo);
                tvSubtitulo.setText(evento.subtitulo);
                tvHora.setText(evento.hora);
                tvSala.setText(evento.sala);

                // Al pulsar la tarjeta, ir a Detalle
                tarjeta.setOnClickListener(v -> {
                    Intent intent = new Intent(EventosActivity.this, DetalleEventoActivity.class);
                    intent.putExtra("TITULO", evento.titulo);
                    intent.putExtra("FECHA_HORA", fechaGrande + " • " + evento.hora);
                    intent.putExtra("DESCRIPCION", evento.descripcion);
                    startActivity(intent);
                });

                layoutEventos.addView(tarjeta);
            }
        }

        // Si no hay eventos ese día, mostrar un mensaje
        if (!hayEventos) {
            TextView sinEventos = new TextView(this);
            sinEventos.setText("No hay funciones programadas para este día.");
            sinEventos.setTextColor(Color.WHITE);
            sinEventos.setPadding(0, 50, 0, 0);
            layoutEventos.addView(sinEventos);
        }
    }
}
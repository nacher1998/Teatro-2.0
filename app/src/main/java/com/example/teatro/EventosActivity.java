package com.example.teatro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;

import com.example.teatro.model.Evento;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EventosActivity extends AppCompatActivity {

    // ── Supabase credentials ───────────────────────────────────────────────
    private static final String SUPABASE_URL = "https://mhofxrmxsegjzssutzru.supabase.co";
    private static final String SUPABASE_KEY = "sb_publishable_bGyTBFCc3gCQXl8RwQS4UA_rD0xaua8";

    // ── Views ──────────────────────────────────────────────────────────────
    private TextView     tvMesActual, tvCabeceraFecha;
    private TableLayout       gridCalendario;
    private NestedScrollView  scrollView;
    private LinearLayout layoutEventos;
    private ProgressBar  progressBar;

    // ── State ──────────────────────────────────────────────────────────────
    private Calendar    calendarioActual;
    private Calendar    fechaSeleccionada;
    private List<Evento> todosLosEventos = new ArrayList<>();
    private boolean     eventosListos    = false;

    private final OkHttpClient client = new OkHttpClient();

    // ──────────────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);

        tvMesActual     = findViewById(R.id.tvMesActual);
        gridCalendario  = findViewById(R.id.gridCalendario);
        scrollView      = findViewById(R.id.scrollView);

        // Permitir que el NestedScrollView capture el scroll aunque el dedo esté sobre el TableLayout
        gridCalendario.setOnTouchListener((v, event) -> {
            scrollView.requestDisallowInterceptTouchEvent(false);
            return false;
        });
        layoutEventos   = findViewById(R.id.layoutEventos);
        tvCabeceraFecha = findViewById(R.id.tvCabeceraFecha);
        progressBar     = findViewById(R.id.progressBar); // ver nota al final


        calendarioActual  = Calendar.getInstance();
        fechaSeleccionada = Calendar.getInstance();

        findViewById(R.id.btnMesAnterior).setOnClickListener(v -> {
            calendarioActual.add(Calendar.MONTH, -1);
            dibujarCalendario();
        });
        findViewById(R.id.btnMesSiguiente).setOnClickListener(v -> {
            calendarioActual.add(Calendar.MONTH, 1);
            dibujarCalendario();
        });

        dibujarCalendario();
        mostrarCargando(true);
        cargarEventosDesdeSupabase();
    }

    // ══════════════════════════════════════════════════════════════════════
    // Supabase — fetch eventos JOIN cartelera
    // ══════════════════════════════════════════════════════════════════════

    private void cargarEventosDesdeSupabase() {
        // PostgREST embedded resource: trae evento + datos de cartelera en una sola llamada
        String url = SUPABASE_URL
                + "/rest/v1/funciones"
                + "?select=*,cartelera(titulo,descripcion,cartel_url)"
                + "&order=fecha.asc,hora.asc";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey",        SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .addHeader("Accept",        "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("EVENTOS", "Connection error: " + e.getMessage());
                runOnUiThread(() -> {
                    mostrarCargando(false);
                    Toast.makeText(EventosActivity.this,
                            "Error de conexión: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "[]";
                Log.d("EVENTOS", "Response: " + body);

                runOnUiThread(() -> {
                    mostrarCargando(false);
                    if (response.isSuccessful()) {
                        parsearEventos(body);
                    } else {
                        Toast.makeText(EventosActivity.this,
                                "Error al cargar eventos: " + response.code(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    /**
     * Parsea el JSON de Supabase. Cada fila tiene la forma:
     * {
     *   "id": "081f...",
     *   "cartelera_id": "1111...",
     *   "fecha": "2026-04-10",
     *   "hora":  "18:00:00",
     *   "cartelera": { "titulo": "...", "descripcion": "...", "cartel_url": "..." }
     * }
     */
    private void parsearEventos(String json) {
        todosLosEventos.clear();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject row = array.getJSONObject(i);

                String eventoId    = row.optString("id", "");
                String carteleraId = row.optString("cartelera_id", "");

                // Fecha: "2026-04-10" → dia, mes (0-based), anio
                String fechaStr = row.optString("fecha", "");
                int dia = 0, mes = 0, anio = 0;
                if (!fechaStr.isEmpty()) {
                    String[] parts = fechaStr.split("-");
                    if (parts.length == 3) {
                        anio = Integer.parseInt(parts[0]);
                        mes  = Integer.parseInt(parts[1]) - 1; // Calendar: 0-based
                        dia  = Integer.parseInt(parts[2]);
                    }
                }

                // Hora: "18:00:00" → "18:00"
                String horaStr = row.optString("hora", "");
                if (horaStr.length() > 5) horaStr = horaStr.substring(0, 5);

                // Datos de cartelera (join embebido)
                String titulo      = "Sin título";
                String descripcion = "";
                String cartelUrl   = "";

                if (!row.isNull("cartelera")) {
                    JSONObject cartelera = row.getJSONObject("cartelera");
                    titulo      = cartelera.optString("titulo",      titulo);
                    descripcion = cartelera.optString("descripcion", descripcion);
                    cartelUrl   = cartelera.optString("cartel_url",  cartelUrl);
                }

                // "sala" puede no existir aún en la BD
                String sala = row.optString("sala", "");

                todosLosEventos.add(new Evento(
                        eventoId, carteleraId,
                        titulo, "",
                        horaStr, sala, descripcion, cartelUrl,
                        dia, mes, anio
                ));
            }

            eventosListos = true;
            actualizarEventos(fechaSeleccionada);

        } catch (JSONException e) {
            Log.e("EVENTOS", "JSON parse error: " + e.getMessage());
            Toast.makeText(this, "Error procesando datos", Toast.LENGTH_SHORT).show();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // Dibujar calendario (sin cambios respecto al original)
    // ══════════════════════════════════════════════════════════════════════

    private void dibujarCalendario() {
        gridCalendario.removeAllViews();

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
        String mesString = sdf.format(calendarioActual.getTime());
        tvMesActual.setText(mesString.substring(0, 1).toUpperCase() + mesString.substring(1));

        Calendar mesCal = (Calendar) calendarioActual.clone();
        mesCal.set(Calendar.DAY_OF_MONTH, 1);

        int primerDiaSemana = (mesCal.get(Calendar.DAY_OF_WEEK) - 2 + 7) % 7;
        int diasEnMes       = mesCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int cellHeightPx = (int) (48 * getResources().getDisplayMetrics().density);

        // Construir array de todas las celdas: vacíos + días
        int totalCeldas = primerDiaSemana + diasEnMes;
        int totalFilas  = (int) Math.ceil(totalCeldas / 7.0);

        for (int fila = 0; fila < totalFilas; fila++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    cellHeightPx));

            for (int col = 0; col < 7; col++) {
                int celda = fila * 7 + col;
                int dia   = celda - primerDiaSemana + 1;

                TextView tvDia = new TextView(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(0, cellHeightPx, 1f);
                tvDia.setLayoutParams(lp);
                tvDia.setGravity(android.view.Gravity.CENTER);
                tvDia.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 15);

                if (celda < primerDiaSemana || dia > diasEnMes) {
                    // Celda vacía
                    tvDia.setText("");
                } else {
                    tvDia.setText(String.valueOf(dia));

                    boolean esSeleccionado =
                            dia == fechaSeleccionada.get(Calendar.DAY_OF_MONTH) &&
                                    calendarioActual.get(Calendar.MONTH) == fechaSeleccionada.get(Calendar.MONTH) &&
                                    calendarioActual.get(Calendar.YEAR)  == fechaSeleccionada.get(Calendar.YEAR);

                    if (esSeleccionado) {
                        tvDia.setBackgroundResource(R.drawable.bg_day_selected);
                        tvDia.setTextColor(Color.WHITE);
                    } else {
                        tvDia.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                        tvDia.setTextColor(android.graphics.Color.parseColor("#DDDBE8"));
                    }

                    final int diaFinal = dia;
                    tvDia.setOnClickListener(v -> {
                        fechaSeleccionada.set(Calendar.YEAR,         calendarioActual.get(Calendar.YEAR));
                        fechaSeleccionada.set(Calendar.MONTH,        calendarioActual.get(Calendar.MONTH));
                        fechaSeleccionada.set(Calendar.DAY_OF_MONTH, diaFinal);
                        dibujarCalendario();
                        actualizarEventos(fechaSeleccionada);
                    });
                }
                row.addView(tvDia);
            }
            gridCalendario.addView(row);
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // Lista de eventos para el día seleccionado
    // ══════════════════════════════════════════════════════════════════════

    private void actualizarEventos(Calendar fecha) {
        layoutEventos.removeAllViews();

        int dia  = fecha.get(Calendar.DAY_OF_MONTH);
        int mes  = fecha.get(Calendar.MONTH);
        int anio = fecha.get(Calendar.YEAR);

        SimpleDateFormat sdf = new SimpleDateFormat("E, d MMMM", new Locale("es", "ES"));
        String fechaGrande = sdf.format(fecha.getTime());
        if (tvCabeceraFecha != null) tvCabeceraFecha.setText(fechaGrande);

        if (!eventosListos) {
            TextView cargando = new TextView(this);
            cargando.setText("Cargando eventos...");
            cargando.setTextColor(android.graphics.Color.parseColor("#9896B0"));
            cargando.setPadding(0, 50, 0, 0);
            layoutEventos.addView(cargando);
            return;
        }

        boolean hayEventos = false;

        for (Evento evento : todosLosEventos) {
            if (evento.dia == dia && evento.mes == mes && evento.anio == anio) {
                hayEventos = true;

                View tarjeta = LayoutInflater.from(this)
                        .inflate(R.layout.item_evento, layoutEventos, false);

                TextView  tvTitulo    = tarjeta.findViewById(R.id.tvTitulo);
                TextView  tvSubtitulo = tarjeta.findViewById(R.id.tvSubtitulo);
                TextView  tvHora      = tarjeta.findViewById(R.id.tvHora);
                TextView  tvSala      = tarjeta.findViewById(R.id.tvSala);
                ImageView ivCartel    = tarjeta.findViewById(R.id.ivCartelEvento);

                tvTitulo.setText(evento.titulo);
                tvSubtitulo.setText(evento.subtitulo != null ? evento.subtitulo : "");
                tvHora.setText(evento.hora);
                tvSala.setText(evento.sala != null ? evento.sala : "");

                // Cargar imagen del cartel con Glide
                if (evento.cartelUrl != null && !evento.cartelUrl.isEmpty()) {
                    Glide.with(EventosActivity.this)
                            .load(evento.cartelUrl)
                            .centerCrop()
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_gallery)
                            .into(ivCartel);
                }

                final Evento eventoFinal = evento;
                tarjeta.setOnClickListener(v -> {
                    Intent intent = new Intent(EventosActivity.this, DetalleEventoActivity.class);
                    intent.putExtra("TITULO",      eventoFinal.titulo);
                    intent.putExtra("FECHA_HORA",  fechaGrande + " • " + eventoFinal.hora);
                    intent.putExtra("DESCRIPCION", eventoFinal.descripcion);
                    intent.putExtra("CARTEL_URL",  eventoFinal.cartelUrl);
                    intent.putExtra("EVENT_NAME",  eventoFinal.titulo);
                    intent.putExtra("EVENT_DATE",  fechaGrande + " • " + eventoFinal.hora);
                    intent.putExtra("FUNCION_ID", eventoFinal.id);
                    startActivity(intent);
                });

                layoutEventos.addView(tarjeta);
            }
        }

        if (!hayEventos) {
            TextView sinEventos = new TextView(this);
            sinEventos.setText("No hay funciones programadas para este día.");
            sinEventos.setTextColor(android.graphics.Color.parseColor("#9896B0"));
            sinEventos.setPadding(0, 50, 0, 0);
            layoutEventos.addView(sinEventos);
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // Helpers
    // ══════════════════════════════════════════════════════════════════════

    private void mostrarCargando(boolean visible) {
        if (progressBar != null) {
            progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }
}
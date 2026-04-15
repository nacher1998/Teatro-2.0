package com.example.teatro.model;

public class Evento {
    public String id;           // UUID del evento
    public String carteleraId;  // UUID de la cartelera asociada
    public String titulo;
    public String subtitulo;
    public String hora;
    public String sala;
    public String descripcion;
    public String cartelUrl;
    public int dia;
    public int mes;  // 0 = Enero (formato Calendar de Java)
    public int anio;

    // Constructor completo (datos de BD)
    public Evento(String id, String carteleraId, String titulo, String subtitulo,
                  String hora, String sala, String descripcion, String cartelUrl,
                  int dia, int mes, int anio) {
        this.id = id;
        this.carteleraId = carteleraId;
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.hora = hora;
        this.sala = sala;
        this.descripcion = descripcion;
        this.cartelUrl = cartelUrl;
        this.dia = dia;
        this.mes = mes;
        this.anio = anio;
    }

    // Constructor legacy — mantiene compatibilidad con código existente
    public Evento(String titulo, String subtitulo, String hora, String sala,
                  String descripcion, int dia, int mes, int anio) {
        this(null, null, titulo, subtitulo, hora, sala, descripcion, null, dia, mes, anio);
    }
}

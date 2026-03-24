package com.example.teatro.model;

public class Evento {
    public String titulo;
    public String subtitulo;
    public String hora;
    public String sala;
    public String descripcion;
    public int dia;
    public int mes; // 0 = Enero, 7 = Agosto, etc. (Formato de Calendar de Java)
    public int anio;

    public Evento(String titulo, String subtitulo, String hora, String sala, String descripcion, int dia, int mes, int anio) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.hora = hora;
        this.sala = sala;
        this.descripcion = descripcion;
        this.dia = dia;
        this.mes = mes;
        this.anio = anio;
    }
}

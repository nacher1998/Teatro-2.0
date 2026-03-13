package com.example.teatro.models;

public class Seat {

    private String id;
    private int fila;
    private int numero;
    private String estado;

    public Seat(String id, int fila, int numero, String estado) {
        this.id = id;
        this.fila = fila;
        this.numero = numero;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public int getFila() {
        return fila;
    }

    public int getNumero() {
        return numero;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

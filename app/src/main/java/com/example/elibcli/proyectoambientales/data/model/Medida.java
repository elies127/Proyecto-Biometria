package com.example.elibcli.proyectoambientales.data.model;


import com.google.firebase.database.Exclude;
import com.google.type.Date;

public class Medida {

    private Date datetime;
    private String valor;
    private float latitud;
    private float longitud;
    private String tipo;
    public Medida(){}

    public Medida(Date datetime, String valor, float latitud, float longitud, String tipo) {
        this.datetime = datetime;
        this.valor = valor;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tipo = tipo;
    }
    @Exclude
    public Date getDatetime() {
        return datetime;
    }
    @Exclude
    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public float getLatitud() {
        return latitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}

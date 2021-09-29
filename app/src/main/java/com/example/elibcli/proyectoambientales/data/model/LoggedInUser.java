package com.example.elibcli.proyectoambientales.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String usuarioId;
    private String nombre;
    private String correo;

    public LoggedInUser(String usuarioId, String displayName, String mail) {
        this.usuarioId = usuarioId;
        this.nombre = displayName;
        this.correo = mail;
    }

    public String getUserId() {
        return usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }


}
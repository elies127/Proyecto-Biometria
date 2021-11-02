package com.example.elibcli.proyectoambientales.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String usuarioId;
    private String nombre;
    private String correo;
    private String numbertlf;
    private String urlFoto;

    public LoggedInUser(String usuarioId, String displayName, String mail) {
        this.usuarioId = usuarioId;
        this.nombre = displayName;
        this.correo = mail;
    }
    public LoggedInUser(String usuarioId, String displayName, String mail, String numbertlf, String urlFoto) {
        this.usuarioId = usuarioId;
        this.nombre = displayName;
        this.correo = mail;
        this.numbertlf = numbertlf;
        this.urlFoto = urlFoto;
    }
    public LoggedInUser(String usuarioId, String displayName, String mail, String numbertlf) {
        this.usuarioId = usuarioId;
        this.nombre = displayName;
        this.correo = mail;
        this.numbertlf = numbertlf;
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
    public String getNumbertlf() {
        return numbertlf;

    }
    public String getUrlFoto(){
        return urlFoto;
    }

}
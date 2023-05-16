package com.qpet.models;

import android.net.Uri;

public class DatosUsuarioModel {
    private Uri userPhoto;
    private String userNombres;
    private String userApellidos;
    private int userEdad;
    private String userDireccion;
    private int userPhoneNumber;
    private int userCantidadMascotas;
    private String userCorreo;
    private String userURLPhoto;

    public String getUserURLPhoto() {
        return userURLPhoto;
    }

    public void setUserURLPhoto(String userURLPhoto) {
        this.userURLPhoto = userURLPhoto;
    }

    public String getUserCorreo() {
        return userCorreo;
    }

    public void setUserCorreo(String userCorreo) {
        this.userCorreo = userCorreo;
    }

    public Uri getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(Uri userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserNombres() {
        return userNombres;
    }

    public void setUserNombres(String userNombres) {
        this.userNombres = userNombres;
    }

    public String getUserApellidos() {
        return userApellidos;
    }

    public void setUserApellidos(String userApellidos) {
        this.userApellidos = userApellidos;
    }

    public int getUserEdad() {
        return userEdad;
    }

    public void setUserEdad(int userEdad) {
        this.userEdad = userEdad;
    }

    public String getUserDireccion() {
        return userDireccion;
    }

    public void setUserDireccion(String userDireccion) {
        this.userDireccion = userDireccion;
    }

    public int getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(int userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public int getUserCantidadMascotas() {
        return userCantidadMascotas;
    }

    public void setUserCantidadMascotas(int userCantidadMascotas) {
        this.userCantidadMascotas = userCantidadMascotas;
    }
}

package com.qpet.models;

public class PublicacionDatosModel {

    private String nombre;
    private String raza;
    private String genero;
    private String edad;
    private String direccion;
    private String descripcion;
    private String URLFotoMascota;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getURLFotoMascota() {
        return URLFotoMascota;
    }

    public void setURLFotoMascota(String URLFotoMascota) {
        this.URLFotoMascota = URLFotoMascota;
    }
}

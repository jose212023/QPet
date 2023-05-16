package com.qpet.inicio;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class InicioModel implements Parcelable {

    private String nombre;
    private String raza;
    private String genero;
    private String edad;
    private String direccion;
    private String descripcion;
    private String URLFotoMascota;
    private String userName;
    private String userFotoURL;
    private String idPublicacion;
    private String telefono;
    public InicioModel(String nombre, String raza, String genero, String edad, String direccion,
                       String descripcion, String URLFotoMascota, String userName, String userFotoURL, String idPublicacion,
                       String telefono){
        this.nombre = nombre;
        this.raza = raza;
        this.genero = genero;
        this.edad = edad;
        this.direccion = direccion;
        this.descripcion = descripcion;
        this.URLFotoMascota = URLFotoMascota;
        this.userName = userName;
        this.userFotoURL = userFotoURL;
        this.idPublicacion = idPublicacion;
        this.telefono = telefono;
    }

    protected InicioModel(Parcel in) {
        nombre = in.readString();
        raza = in.readString();
        genero = in.readString();
        edad = in.readString();
        direccion = in.readString();
        descripcion = in.readString();
        URLFotoMascota = in.readString();
        userName = in.readString();
        userFotoURL = in.readString();
        idPublicacion = in.readString();
        telefono = in.readString();
    }

    public static final Creator<InicioModel> CREATOR = new Creator<InicioModel>() {
        @Override
        public InicioModel createFromParcel(Parcel in) {
            return new InicioModel(in);
        }

        @Override
        public InicioModel[] newArray(int size) {
            return new InicioModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(nombre);
        parcel.writeString(raza);
        parcel.writeString(genero);
        parcel.writeString(edad);
        parcel.writeString(direccion);
        parcel.writeString(descripcion);
        parcel.writeString(URLFotoMascota);
        parcel.writeString(userName);
        parcel.writeString(userFotoURL);
        parcel.writeString(idPublicacion);
        parcel.writeString(telefono);
    }

    public String getNombre() {
        return nombre;
    }

    public String getRaza() {
        return raza;
    }

    public String getGenero() {
        return genero;
    }

    public String getEdad() {
        return edad;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getURLFotoMascota() {
        return URLFotoMascota;
    }

    public String getUserName(){
        return userName;
    }

    public String getUserFotoURL(){
        return userFotoURL;
    }

    public String getIdPublicacion(){return idPublicacion;}

    public String getTelefono() {return telefono;}
}

package com.qpet.controladores;

import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.qpet.activities.IngresarDatosNuevoUsuario;
import com.qpet.activities.Login;
import com.qpet.activities.MainActivity;
import com.qpet.activities.Registro;

public class Controller {
    Registro reg;
    Login log;
    IngresarDatosNuevoUsuario dataNewUser;

    MainActivity mA;
    RegistroControlador rcont;
    LoginControlador lcont;
    DatosNuevoUsuarioControlador dncont;

    MainControlador mcont;
    public Controller(Registro reg, Login log, IngresarDatosNuevoUsuario dataNewUser, MainActivity mA){
        rcont = new RegistroControlador(this);
        lcont = new LoginControlador(this);
        mcont = new MainControlador(this);
        if (dataNewUser != null) {
            dncont = new DatosNuevoUsuarioControlador(this, dataNewUser.getApplicationContext());
            this.dataNewUser = dataNewUser;
        } else {
            dncont = null;
            this.dataNewUser = null;
        }
        this.mA = mA;
        this.log = log;
        this.reg = reg;
    }
    public void iniciarSesion(EditText correo, EditText password){
        lcont.IniciarSesion(correo, password);
    }
    public void registrarse(EditText correo, EditText password){
        rcont.Registrar(correo, password);
    }
    public void verificarDatos(ImageView foto, EditText nombre, EditText apellidos, EditText edad, EditText direccion,
                               EditText telefono, EditText numMacotas, String correo, Uri photo){
        dncont.verificarDatos(foto, nombre, apellidos, edad, direccion, telefono, numMacotas, correo, photo);
    }

    public void cargarDatos(ImageView userPhoto, TextView nombres, TextView apellidos, TextView correo){
        mcont.cargarDatos(userPhoto, nombres, apellidos, correo);
    }
    public void accederMain(String correo){
        log.accederMain(correo);
    }
    public void accederMain2(String correo){dataNewUser.accederMain(correo);}
    public void accederDatosNuevoUsuario(String correo){log.accederDatosNuevoUsuario(correo);}
    public void mensajeR(String mensaje){
        reg.mensaje(mensaje);
    }
    public void mensajeL(String mensaje){
        log.mensaje(mensaje);
    }
    public void mensajeDU(String mensaje){dataNewUser.mensaje(mensaje);}
    public void limpiarR(){
        reg.limpiarR();
    }
    public void limpiarL(){
        log.limpiarL();
    }

}

package com.qpet;

import android.widget.EditText;

import com.qpet.activities.Login;
import com.qpet.activities.Registro;
import com.qpet.controladores.LoginControlador;
import com.qpet.controladores.RegistroControlador;

public class Controller {
    Registro reg;
    Login log;
    RegistroControlador rcont;
    LoginControlador lcont;

    public Controller(Registro reg, Login log){
        rcont = new RegistroControlador(this);
        lcont = new LoginControlador(this);
        this.log = log;
        this.reg = reg;
    }

    public void iniciarSesion(EditText correo, EditText password){
        lcont.IniciarSesion(correo, password);
    }

    public void registrarse(EditText correo, EditText password){
        rcont.Registrar(correo, password);
    }

    public void accederMain(){
        log.accederMain();
    }

    public void mensajeR(String mensaje){
        reg.mensaje(mensaje);
    }

    public void mensajeL(String mensaje){
        log.mensaje(mensaje);
    }

    public void limpiarR(){
        reg.limpiarR();
    }

    public void limpiarL(){
        log.limpiarL();
    }

}

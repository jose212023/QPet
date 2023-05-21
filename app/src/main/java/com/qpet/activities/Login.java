package com.qpet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qpet.controladores.Controller;
import com.qpet.R;

public class Login extends AppCompatActivity {

    private boolean sesionIniciada = false;
    Controller cont;
    EditText txtCorreo, txtPassword;
    Button iniciarSesion, registrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Iniciar Sesion");

        SharedPreferences prefs = getSharedPreferences("MiAplicacionPrefs", Context.MODE_PRIVATE);
        sesionIniciada = prefs.getBoolean("sesionIniciada", false);
        if (sesionIniciada) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        cont = new Controller(null, this, null, null);

        txtCorreo = (EditText) findViewById(R.id.editTextCorreo);
        txtPassword = (EditText) findViewById(R.id.editTextPassword);
        iniciarSesion = (Button) findViewById(R.id.btnIniciarSesion);
        registrarse = (Button) findViewById(R.id.btnRegistrarse);

        Listener();
    }

    public void Listener(){
        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cont.iniciarSesion(txtCorreo, txtPassword);
            }
        });

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Registro.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void accederMain(String correo){
        SharedPreferences prefs = getSharedPreferences("MiAplicacionPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("sesionIniciada", true);
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Correo", correo);
        startActivity(intent);
        finish();
    }

    public void accederDatosNuevoUsuario(String correo){
        Intent intent = new Intent(this, IngresarDatosNuevoUsuario.class);
        intent.putExtra("Correo_Electronico", correo);
        startActivity(intent);
        finish();
    }
    public void mensaje(String mensaje){
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
    public void limpiarL(){
        txtCorreo.setText("");
        txtPassword.setText("");
    }

}
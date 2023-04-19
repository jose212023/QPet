package com.qpet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qpet.Controller;
import com.qpet.R;

public class Registro extends AppCompatActivity {

    Controller cont;
    EditText txtCorreo, txtPassword;
    Button registrarR, regresarR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        cont = new Controller(this, null);

        txtCorreo = (EditText) findViewById(R.id.editTextCorreoR);
        txtPassword = (EditText) findViewById(R.id.editTextPasswordR);
        registrarR = (Button) findViewById(R.id.btnRegistrarseR);
        regresarR = (Button) findViewById(R.id.btnRegresarR);

        Listener();
    }

    public void Listener(){
        registrarR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cont.registrarse(txtCorreo, txtPassword);
            }
        });

        regresarR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registro.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void mensaje(String mensaje){
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    public void limpiarR(){
        txtCorreo.setText("");
        txtPassword.setText("");
    }

}
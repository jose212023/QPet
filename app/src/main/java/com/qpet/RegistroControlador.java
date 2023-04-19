package com.qpet;

import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegistroControlador {
    Controller cont;
    Usuario usuario;

    public RegistroControlador(Controller controller) {
        this.cont = controller;
        this.usuario = new Usuario();
    }

    public void Registrar(EditText correo, EditText password){
        usuario.setCorreoElectronico(correo.getText().toString().trim());
        usuario.setPassword(password.getText().toString());

        if(usuario.getCorreoElectronico().isEmpty() && usuario.getPassword().isEmpty()){
            cont.mensajeR("Correo Electronico y Contraseña Necesarios.");
        }else if (usuario.getCorreoElectronico().isEmpty()){
            cont.mensajeR("Correo Electronico Necesario.");
        } else if (usuario.getPassword().isEmpty()) {
            cont.mensajeR("Contraseña Necesaria.");
        }else if (!Patterns.EMAIL_ADDRESS.matcher(usuario.getCorreoElectronico()).matches()){
            cont.mensajeR("Correo Electronico Invalido");
        } else if (Patterns.EMAIL_ADDRESS.matcher(usuario.getCorreoElectronico()).matches()) {
            Log.d("EXITO", "Todos los campos son correctos.");
            autenticar();
        }

    }

    public void autenticar(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(usuario.getCorreoElectronico(), usuario.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String estado = "No Validado";
                            registrarUsuarioFireStore(estado);
                            enviarCorreoDeValidacion();
                        }else{
                            cont.mensajeR("Error de Autenticacion");
                        }
                    }
                });
    }

    public void enviarCorreoDeValidacion() {
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

        usuarioActual.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    cont.mensajeR("Correo de validación enviado");
                } else {
                    cont.mensajeR("Error al enviar correo de validación");
                }
            }
        });
    }

    public void registrarUsuarioFireStore(String estado){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = UUID.randomUUID().toString();
        Map<String, Object> users = new HashMap<>();
        users.put("Id",id);
        users.put("Correo Electronico", usuario.getCorreoElectronico());
        users.put("Password", usuario.getPassword());
        users.put("Estado", estado);

        db.collection("Users").document(id).set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    cont.mensajeR("Usuario Registrado con Exito.");
                    cont.limpiarR();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                cont.mensajeR("Error al Registrar Usuario.");
                Log.d("ERROR", e.getMessage());
            }
        });

    }

}

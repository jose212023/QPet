package com.qpet.controladores;

import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.qpet.Controller;
import com.qpet.UsuarioModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RegistroControlador {
    Controller cont;
    UsuarioModel usuarioModel;

    public RegistroControlador(Controller controller) {
        this.cont = controller;
        this.usuarioModel = new UsuarioModel();
    }

    public void Registrar(EditText correo, EditText password){
        usuarioModel.setCorreoElectronico(correo.getText().toString().trim());
        usuarioModel.setPassword(password.getText().toString());

        if(usuarioModel.getCorreoElectronico().isEmpty() && usuarioModel.getPassword().isEmpty()){
            cont.mensajeR("Correo Electronico y Contraseña Necesarios.");
        }else if (usuarioModel.getCorreoElectronico().isEmpty()){
            cont.mensajeR("Correo Electronico Necesario.");
        } else if (usuarioModel.getPassword().isEmpty()) {
            cont.mensajeR("Contraseña Necesaria.");
        }else if (!Patterns.EMAIL_ADDRESS.matcher(usuarioModel.getCorreoElectronico()).matches()){
            cont.mensajeR("Correo Electronico Invalido");
        } else if (Patterns.EMAIL_ADDRESS.matcher(usuarioModel.getCorreoElectronico()).matches()) {
            Log.d("EXITO", "Todos los campos son correctos.");
            autenticar();
        }

    }

    public void autenticar(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(usuarioModel.getCorreoElectronico(), usuarioModel.getPassword())
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
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(usuarioModel.getCorreoElectronico())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if(task.isSuccessful()){
                            SignInMethodQueryResult result = task.getResult();
                            List<String> signInMethods = result.getSignInMethods();
                            if (signInMethods != null && !signInMethods.isEmpty()) {
                                cont.mensajeR("El correo electrónico ya está registrado");
                            } else {
                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(usuarioModel.getCorreoElectronico(), usuarioModel.getPassword())
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
                        }else{
                            cont.mensajeR("Error al verificar correo electrónico");
                        }
                    }
                });
    }

    public void registrarUsuarioFireStore(String estado){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = UUID.randomUUID().toString();
        Map<String, Object> users = new HashMap<>();
        users.put("Id",id);
        users.put("Correo Electronico", usuarioModel.getCorreoElectronico());
        users.put("Password", usuarioModel.getPassword());
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

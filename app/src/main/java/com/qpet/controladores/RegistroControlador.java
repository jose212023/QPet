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
import com.google.firebase.firestore.QuerySnapshot;
import com.qpet.models.UsuarioModel;

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
                                            enviarCorreoDeValidacion();
                                        }else{
                                            cont.mensajeR("Error de Autenticacion");
                                        }
                                    }
                                });

                    }
                }
            }
        });
    }

    public void enviarCorreoDeValidacion() {
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            String estado = "No Validado";
                            registrarUsuarioFireStore(estado);
                            cont.mensajeR("Correo de validación enviado");
                        } else {
                            cont.mensajeR("Error al enviar correo de validación");
                        }
                    }
                });
    }

    public void registrarUsuarioFireStore(String estado){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").whereEqualTo("Correo Electronico", usuarioModel.getCorreoElectronico())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();

                            if(querySnapshot.isEmpty()){
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
                            }else{
                                cont.mensajeR("Ya existe un usuario con este correo electrónico.");
                            }

                        }else{
                            cont.mensajeR("Error al buscar usuario.");
                            Log.d("ERROR", task.getException().getMessage());
                        }
                    }
                });

    }

}

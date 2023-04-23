package com.qpet.controladores;

import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.qpet.models.UsuarioModel;

public class LoginControlador {
    Controller cont;
    UsuarioModel user;
    public LoginControlador(Controller controller) {
        this.cont = controller;
        this.user = new UsuarioModel();
    }

    public void IniciarSesion(EditText correo, EditText password){
        user.setCorreoElectronico(correo.getText().toString().trim());
        user.setPassword(password.getText().toString());

        if(user.getCorreoElectronico().isEmpty() && user.getPassword().isEmpty()){
            cont.mensajeL("Correo Electronico y Contraseña Necesarios.");
        }else if (user.getCorreoElectronico().isEmpty()){
            cont.mensajeL("Correo Electronico Necesario.");
        } else if (user.getPassword().isEmpty()) {
            cont.mensajeL("Contraseña Necesaria.");
        }else if (!Patterns.EMAIL_ADDRESS.matcher(user.getCorreoElectronico()).matches()){
            cont.mensajeL("Correo Electronico Invalido");
        } else if (Patterns.EMAIL_ADDRESS.matcher(user.getCorreoElectronico()).matches()) {
            Log.d("EXITO", "Todos los campos son correctos.");
            verificarCorreoElectronico();
        }

    }

    public void verificarCorreoElectronico() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .whereEqualTo("Correo Electronico", user.getCorreoElectronico())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (!querySnapshot.isEmpty()) {
                            String contraseña = querySnapshot.getDocuments().get(0).getString("Password");
                            if(contraseña.equals(user.getPassword())){
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(user.getCorreoElectronico(), user.getPassword())
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
                                                if (usuarioActual.isEmailVerified()) {
                                                    Log.d("EXITO", "Correo electronico validado.");
                                                    editarEstadoCorreo();
                                                }else{
                                                    cont.mensajeL("Correo Electronico no Validado.");
                                                }
                                            }
                                        });
                            }else {
                                cont.mensajeL("Contraseña Incorrecta.");
                            }

                        } else {
                            cont.mensajeL("Correo Electronico Incorrecto.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR", e.getMessage());
                    }
                });
    }

    public void editarEstadoCorreo(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .whereEqualTo("Correo Electronico", user.getCorreoElectronico())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (!querySnapshot.isEmpty()) {
                            String id = querySnapshot.getDocuments().get(0).getId();
                            db.collection("Users").document(id).update("Estado", "Validado").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("EXITO", "Estado del correo electronico actualizado.");
                                        verificarUsuario();
                                    }else {
                                        cont.mensajeL("Error al actualizar estado del correo electronico.");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("ERROR", e.getMessage());
                                }
                            });
                        } else {
                            Log.d("Error", "No se encontro un documento relacionado con el correo electronico.");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR", e.getMessage());
                    }
                });

    }

    public void verificarUsuario(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String[] userId = new String[1];

        db.collection("Users").whereEqualTo("Correo Electronico", user.getCorreoElectronico())
                .whereEqualTo("Password", user.getPassword())
                .whereEqualTo("Estado", "Validado")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            int size = task.getResult().size();
                            if(size > 0){
                                for(DocumentSnapshot document : task.getResult().getDocuments()){
                                    userId[0] = document.getId();
                                }
                                db.collection("DataUser").document(userId[0]).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot userDoc = task.getResult();
                                            if(userDoc.exists()){
                                                cont.accederMain(user.getCorreoElectronico());
                                                cont.limpiarL();
                                            }else {
                                                cont.accederDatosNuevoUsuario(user.getCorreoElectronico());
                                                cont.limpiarL();
                                            }
                                        }
                                    }
                                });
                            }else {
                                cont.mensajeL("Correo electrónico o contraseña incorrecto");
                            }
                        }else{
                            Log.w("Error", "Error gettin documents.", task.getException());
                        }
                    }
        });
    }

}

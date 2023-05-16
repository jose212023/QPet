package com.qpet.controladores;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.qpet.models.DatosUsuarioModel;

public class MainControlador {
    Controller cont;
    DatosUsuarioModel dataUser;
    public MainControlador(Controller controller){
        this.cont = controller;
        this.dataUser = new DatosUsuarioModel();
    }

    public void cargarDatos(ImageView userPhoto, TextView nombres, TextView apellidos, TextView correo){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            dataUser.setUserCorreo(currentUser.getEmail());
        }

        db.collection("Users").whereEqualTo("Correo Electronico", dataUser.getUserCorreo())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                            db.collection("DataUser").document(id)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    dataUser.setUserNombres(document.getString("Nombres"));
                                                    dataUser.setUserApellidos(document.getString("Apellidos"));
                                                    dataUser.setUserURLPhoto(document.getString("URL Fotografia"));
                                                    dataUser.setUserCorreo(document.getString("Correo Electronico"));
                                                    dataUser.setUserEdad(document.getLong("Edad").intValue());
                                                    dataUser.setUserDireccion(document.getString("Direccion"));
                                                    dataUser.setUserPhoneNumber(document.getLong("Telefono").intValue());
                                                    dataUser.setUserCantidadMascotas(document.getLong("Cantidad de Mascotas").intValue());

                                                    SharedPreferences preferences = cont.mA.getSharedPreferences("Datos_Usuario", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putString("Id", id);
                                                    editor.putString("URLFoto", dataUser.getUserURLPhoto());
                                                    editor.putString("Nombres", dataUser.getUserNombres());
                                                    editor.putString("Apellidos", dataUser.getUserApellidos());
                                                    editor.putInt("Edad", dataUser.getUserEdad());
                                                    editor.putString("Direccion", dataUser.getUserDireccion());
                                                    editor.putInt("Telefono", dataUser.getUserPhoneNumber());
                                                    editor.putInt("NumMascotas", dataUser.getUserCantidadMascotas());
                                                    editor.putString("Correo", dataUser.getUserCorreo());
                                                    editor.apply();

                                                    nombres.setText(dataUser.getUserNombres());
                                                    apellidos.setText(dataUser.getUserApellidos());
                                                    correo.setText(dataUser.getUserCorreo());

                                                    if (dataUser.getUserURLPhoto() != null) {
                                                        Glide.with(cont.mA.getApplicationContext())
                                                                .load(Uri.parse(dataUser.getUserURLPhoto()))
                                                                .apply(RequestOptions.circleCropTransform())
                                                                .into(userPhoto);
                                                    }

                                                }
                                            } else {
                                                Log.d("Error", "Error getting document: ", task.getException());
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("ERROR", e.getMessage());
                                        }
                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR", e.getMessage());
                    }
                });
    }

}

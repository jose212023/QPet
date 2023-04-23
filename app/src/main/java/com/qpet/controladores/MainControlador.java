package com.qpet.controladores;

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
        if(obtenerDatosFS() == true){
            nombres.setText(dataUser.getUserNombres());
            apellidos.setText(dataUser.getUserApellidos());
            correo.setText(dataUser.getUserCorreo());

            Log.d("Datos: ", dataUser.getUserNombres() +
                    dataUser.getUserApellidos() +
                    dataUser.getUserCorreo());

            if (dataUser.getUserPhoto() != null) {
                Glide.with(cont.mA)
                        .load(dataUser.getUserPhoto())
                        .apply(RequestOptions.circleCropTransform())
                        .into(userPhoto);
            }
        }
    }

    public boolean obtenerDatosFS(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final boolean[] value = {false};

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
                                                    dataUser.setUserPhoto(Uri.parse(document.getString("Fotografia")));
                                                    dataUser.setUserCorreo(document.getString("Correo Electronico"));
                                                    value[0] = true;
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
        return value[0];
    }

}

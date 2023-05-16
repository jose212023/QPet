package com.qpet.inicio;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class InicioViewModel extends ViewModel {

    private MutableLiveData<List<InicioModel>> publicaciones = new MutableLiveData<>();
    public InicioViewModel() {
    }

    public void obtnerPublicacion(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<InicioModel> publicacionesTemp = new ArrayList<>();
        final String[] userFotoURL = new String[1];
        final String[] userName = new String[1];

        db.collection("Publicaciones")
                .orderBy("Fecha Publicacion", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userId = document.getString("Id Usuario");
                                String nombre = document.getString("Nombre Mascota");
                                String raza = document.getString("Raza");
                                String genero = document.getString("Genero");
                                String edad = document.getString("Edad");
                                String direccion = document.getString("Municipio") + ", " + document.getString("Departamento");
                                String descripcion = document.getString("Descripcion Mascota");
                                String urlFoto = document.getString("URL Foto Mascota");
                                String idPublicacion = document.getString("Id Publicacion");
                                String telefono = document.getString("Telefono Contacto");

                                db.collection("DataUser").document(userId)
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot userDocument = task.getResult();
                                                    if (userDocument.exists()) {
                                                        userFotoURL[0] = userDocument.getString("URL Fotografia");
                                                        userName[0] = userDocument.getString("Nombres") + " " + userDocument.getString("Apellidos");

                                                        InicioModel publicacion = new InicioModel(nombre, raza, genero, edad, direccion, descripcion, urlFoto, userName[0], userFotoURL[0], idPublicacion, telefono);
                                                        publicacionesTemp.add(publicacion);
                                                        publicaciones.setValue(publicacionesTemp);
                                                    }
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d("ERROR", "Error al obtener, ordenar y cargar las publicacione.");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public LiveData<List<InicioModel>> getPublicaciones() {
        return publicaciones;
    }

}

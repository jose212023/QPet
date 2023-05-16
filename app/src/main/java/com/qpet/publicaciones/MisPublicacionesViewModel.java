package com.qpet.publicaciones;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.qpet.inicio.InicioModel;

import java.util.ArrayList;
import java.util.List;

public class MisPublicacionesViewModel extends ViewModel {

    private MisPublicacionesFragment misPublicacionesFragment = new MisPublicacionesFragment();
    private MutableLiveData<List<InicioModel>> misPublicaciones = new MutableLiveData<>();
    public MisPublicacionesViewModel() {
    }

    public void eliminar(InicioModel publicacion, Context context){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        InicioModel post = publicacion;

        String idPublicacion = publicacion.getIdPublicacion();
        String imagenURL = publicacion.getURLFotoMascota();

        db.collection("Publicaciones").document(idPublicacion)
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        StorageReference imageRef = storage.getReferenceFromUrl(imagenURL);
                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                misPublicacionesFragment.mensaje("Publicacion Eliminada", context);
                                obtnerPublicacion(context);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("ERROR", "Error al eliminar la imagen");
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR", "Error al eliminar la publicacion");
                    }
                });

    }
    public void obtnerPublicacion(Context context){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<InicioModel> publicacionesTemp = new ArrayList<>();
        final String[] userFotoURL = new String[1];
        final String[] userName = new String[1];

        SharedPreferences sharedPreferences = context.getSharedPreferences("Datos_Usuario", MODE_PRIVATE);
        String id = sharedPreferences.getString("Id", "valor_por_defecto");

        db.collection("Publicaciones").whereEqualTo("Id Usuario", id)
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
                                                        misPublicaciones.setValue(publicacionesTemp);
                                                    }
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d("ERROR", "Error al Encontrar Publicaciones");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public LiveData<List<InicioModel>> getMisPublicaciones() {
        return misPublicaciones;
    }
}

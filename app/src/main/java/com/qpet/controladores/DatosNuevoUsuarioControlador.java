package com.qpet.controladores;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.qpet.models.DatosUsuarioModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatosNuevoUsuarioControlador {

    Controller cont;
    DatosUsuarioModel dataUser;
    private Context context;

    DatosNuevoUsuarioControlador(Controller controller, Context mcontext){
        if (mcontext == null) {
            throw new IllegalArgumentException("Contexto no puede ser nulo");
        }
        this.cont = controller;
        this.dataUser = new DatosUsuarioModel();
        context = mcontext;
    }
    public void verificarDatos(ImageView foto, EditText nombre, EditText apellidos, EditText edad, EditText direccion,
                               EditText telefono, EditText numMacotas, String correo, Uri photo){

        ImageView imageView = foto;
        if (imageView.getDrawable() == null) {
            cont.mensajeDU("Por favor carge una imagen.");
        } else if (nombre.getText().toString().isEmpty()) {
            cont.mensajeDU("Nombres necesarios.");
        } else if (apellidos.getText().toString().isEmpty()) {
            cont.mensajeDU("Apellidos necesarios.");
        } else if (edad.getText().toString().isEmpty()) {
            cont.mensajeDU("Edad necesaria.");
        } else if (direccion.getText().toString().isEmpty()) {
            cont.mensajeDU("Direccion necesaria.");
        } else if (telefono.getText().toString().isEmpty()) {
            cont.mensajeDU("Telefono necesario.");
        } else if (numMacotas.getText().toString().isEmpty()) {
            cont.mensajeDU("Numero de mascotas necesario.");
        } else {
            dataUser.setUserPhoto(photo);
            dataUser.setUserNombres(nombre.getText().toString());
            dataUser.setUserApellidos(apellidos.getText().toString());
            dataUser.setUserEdad(Integer.parseInt(edad.getText().toString()));
            dataUser.setUserDireccion(direccion.getText().toString());
            dataUser.setUserPhoneNumber(Integer.parseInt(telefono.getText().toString()));
            dataUser.setUserCantidadMascotas(Integer.parseInt(numMacotas.getText().toString()));
            dataUser.setUserCorreo(correo);
            gurdarDatosFirestore();
        }
    }

    public void gurdarDatosFirestore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .whereEqualTo("Correo Electronico", dataUser.getUserCorreo())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (!querySnapshot.isEmpty()) {
                            String id = querySnapshot.getDocuments().get(0).getId();

                            Map<String, Object> datauser = new HashMap<>();
                            datauser.put("Id Usuario",id);
                            datauser.put("Fotografia", dataUser.getUserPhoto());
                            datauser.put("Nombres", dataUser.getUserNombres());
                            datauser.put("Apellidos", dataUser.getUserApellidos());
                            datauser.put("Edad", dataUser.getUserEdad());
                            datauser.put("Direccion", dataUser.getUserDireccion());
                            datauser.put("Telefono", dataUser.getUserPhoneNumber());
                            datauser.put("Cantida de Macotas", dataUser.getUserCantidadMascotas());
                            datauser.put("Correo Electronico", dataUser.getUserCorreo());

                            db.collection("DataUser").document(id).set(datauser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        cont.mensajeDU("Datos Guardados con Exito.");
                                        cont.accederMain2(dataUser.getUserCorreo());
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    cont.mensajeDU("Error al Guardar Datos.");
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

}

package com.qpet.editar;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.qpet.models.DatosUsuarioModel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditarInfoViewModel extends ViewModel {
    DatosUsuarioModel dataUser;
    EditarInfoFragment editarInfoFragment = new EditarInfoFragment();
    File localFile;
    SharedPreferences sharedPreferences;
    FirebaseUser currentUser;
    FirebaseStorage storage;
    StorageReference storageRef;
    public EditarInfoViewModel() {
        dataUser = new DatosUsuarioModel();
    }
    public void verificarDatos(Context context, ImageView foto, EditText nombres, EditText apellidos, EditText edad, EditText direccion, EditText telefono,
                               EditText numMascotas, Uri photo) {
        if (foto.getDrawable() == null) {
            editarInfoFragment.mensaje(context, "Por favor carge una imagen.");
        } else if (nombres.getText().toString().isEmpty()) {
            editarInfoFragment.mensaje(context, "Nombres necesarios.");
        } else if (apellidos.getText().toString().isEmpty()) {
            editarInfoFragment.mensaje(context, "Apellidos necesarios.");
        } else if (edad.getText().toString().isEmpty()) {
            editarInfoFragment.mensaje(context, "Edad necesaria.");
        } else if (direccion.getText().toString().isEmpty()) {
            editarInfoFragment.mensaje(context, "Direccion necesaria.");
        } else if (telefono.getText().toString().isEmpty()) {
            editarInfoFragment.mensaje(context, "Telefono necesario.");
        } else if (numMascotas.getText().toString().isEmpty()) {
            editarInfoFragment.mensaje(context, "Numero de mascotas necesario.");
        } else {
            guardarDatos(context, nombres, apellidos, edad, direccion, telefono, numMascotas, photo);
        }
    }
    public void cargarDatos(Context context, ImageView foto, EditText names, EditText lastnames, EditText age, EditText address,
                            EditText phoneNumber, EditText numPets) {

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        sharedPreferences = context.getSharedPreferences("Datos_Usuario", MODE_PRIVATE);
        String id = sharedPreferences.getString("Id", "valor_por_defecto");
        String url = sharedPreferences.getString("URLFoto", "valor_por_defecto");
        String nombres = sharedPreferences.getString("Nombres", "valor_por_defecto");
        String apellidos = sharedPreferences.getString("Apellidos", "valor_por_defecto");
        int edad = sharedPreferences.getInt("Edad", 0);
        String direccion = sharedPreferences.getString("Direccion", "valor_por_defecto");
        int telefono = sharedPreferences.getInt("Telefono", 0);
        int numMascotas = sharedPreferences.getInt("NumMascotas", 0);
        String correo = sharedPreferences.getString("Correo", "valor_por_defecto");

        StorageReference imageRef = storageRef.child("images/" + currentUser.getUid() + "/" + id);

        localFile = null;
        try {
            localFile = File.createTempFile("imagenTemporal", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (localFile != null) {
            File finalLocalFile = localFile;
            imageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                Log.d("Mensaje", "Imagen descargada y guardada temporalmente.");
                Uri fileUri = Uri.fromFile(finalLocalFile);
                dataUser.setUserPhoto(fileUri);
            }).addOnFailureListener(exception -> {
                Log.d("Error", "Error al descargar la imagen.");
            });
        }

        dataUser.setUserURLPhoto(url);
        dataUser.setUserNombres(nombres);
        dataUser.setUserApellidos(apellidos);
        dataUser.setUserEdad(edad);
        dataUser.setUserDireccion(direccion);
        dataUser.setUserPhoneNumber(telefono);
        dataUser.setUserCantidadMascotas(numMascotas);
        dataUser.setUserCorreo(correo);

        setearDatos(context, foto, names, lastnames, age, address, phoneNumber, numPets);
    }
    public void setearDatos(Context context, ImageView photo, EditText names, EditText lastnames, EditText age, EditText address,
                            EditText phoneNumber, EditText numPets){
        if (dataUser.getUserURLPhoto() != null) {
            Glide.with(context)
                    .load(Uri.parse(dataUser.getUserURLPhoto()))
                    .apply(new RequestOptions().circleCrop())
                    .into(photo);
        }

        names.setText(dataUser.getUserNombres());
        lastnames.setText(dataUser.getUserApellidos());
        age.setText(String.valueOf(dataUser.getUserEdad()));
        address.setText(dataUser.getUserDireccion());
        phoneNumber.setText(String.valueOf(dataUser.getUserPhoneNumber()));
        numPets.setText(String.valueOf(dataUser.getUserCantidadMascotas()));
    }
    public void guardarDatos(Context context, EditText names, EditText lastnames, EditText age, EditText address,
                             EditText phoneNumber, EditText numPets, Uri photo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        getDatos(context, names, lastnames, age, address, phoneNumber, numPets);

        if(photo != null){
            dataUser.setUserPhoto(photo);
        }

        db.collection("Users")
                .whereEqualTo("Correo Electronico", dataUser.getUserCorreo())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (!querySnapshot.isEmpty()) {
                            String id = querySnapshot.getDocuments().get(0).getId();

                            StorageReference imageRef = storageRef.child("images/" + currentUser.getUid() + "/" + id);
                            UploadTask uploadTask = imageRef.putFile(dataUser.getUserPhoto());
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            dataUser.setUserURLPhoto(uri.toString());
                                            DocumentReference docRef = db.collection("DataUser").document(id);
                                            Map<String, Object> datauser = new HashMap<>();
                                            datauser.put("URL Fotografia", dataUser.getUserURLPhoto());
                                            datauser.put("Nombres", dataUser.getUserNombres());
                                            datauser.put("Apellidos", dataUser.getUserApellidos());
                                            datauser.put("Edad", dataUser.getUserEdad());
                                            datauser.put("Direccion", dataUser.getUserDireccion());
                                            datauser.put("Telefono", dataUser.getUserPhoneNumber());
                                            datauser.put("Cantidad de Mascotas", dataUser.getUserCantidadMascotas());
                                            docRef.update(datauser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    editarInfoFragment.mensaje(context, "Datos Editados con Exito.");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    editarInfoFragment.mensaje(context, "Error al Editar los Datos.");
                                                }
                                            });
                                        }
                                    });
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

    public void getDatos(Context context, EditText names, EditText lastnames, EditText age, EditText address,
                         EditText phoneNumber, EditText numPets){
        dataUser.setUserNombres(names.getText().toString());
        dataUser.setUserApellidos(lastnames.getText().toString());
        dataUser.setUserEdad(Integer.parseInt(age.getText().toString()));
        dataUser.setUserDireccion(address.getText().toString());
        dataUser.setUserPhoneNumber(Integer.parseInt(phoneNumber.getText().toString()));
        dataUser.setUserCantidadMascotas(Integer.parseInt(numPets.getText().toString()));

        sharedPreferences = context.getSharedPreferences("Datos_Usuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("URLFoto", dataUser.getUserURLPhoto());
        editor.putString("Nombres", dataUser.getUserNombres());
        editor.putString("Apellidos", dataUser.getUserApellidos());
        editor.putInt("Edad", dataUser.getUserEdad());
        editor.putString("Direccion", dataUser.getUserDireccion());
        editor.putInt("Telefono", dataUser.getUserPhoneNumber());
        editor.putInt("NumMascotas", dataUser.getUserCantidadMascotas());
        editor.apply();
    }

}

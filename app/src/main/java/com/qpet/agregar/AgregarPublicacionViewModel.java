package com.qpet.agregar;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.qpet.R;
import com.qpet.models.PublicacionDatosModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AgregarPublicacionViewModel extends ViewModel {
    PublicacionDatosModel publicacionDatosModel;
    public AgregarPublicacionViewModel(){publicacionDatosModel = new PublicacionDatosModel();}
    AgregarPublicacionFragment agregarPublicacionFragment = new AgregarPublicacionFragment();
    public void verificarCampos(EditText nombre, EditText raza, EditText edad, EditText descripcion, Spinner genero,
                                Spinner formatoEdad, Spinner departamento, Spinner municipio, boolean nombreSN,
                                ImageView fotoMascota, Context context, Uri fotoM){
        if (nombreSN == true && nombre.getText().toString().isEmpty()){
            agregarPublicacionFragment.mensaje(context,"Campo Nombre Mascota Requerido");
        } else if (raza.getText().toString().isEmpty()){
            agregarPublicacionFragment.mensaje(context,"Campo Raza requerido");
        } else if (genero.getSelectedItem().toString().equals("Seleccionar")) {
            agregarPublicacionFragment.mensaje(context,"Seleccione el genero de la mascota");
        } else if (edad.getText().toString().isEmpty()) {
            agregarPublicacionFragment.mensaje(context,"Campo Edad requerido");
        } else if (formatoEdad.getSelectedItem().toString().equals("Seleccionar")) {
            agregarPublicacionFragment.mensaje(context,"Seleccione un formato para edad");
        } else if (departamento.getSelectedItem().toString().equals("Seleccionar")) {
            agregarPublicacionFragment.mensaje(context,"Seleccione un departamento");
        } else if (municipio.getSelectedItem().toString().equals("Seleccionar")) {
            agregarPublicacionFragment.mensaje(context,"Seleccione un municipio");
        } else if (descripcion.getText().toString().isEmpty()) {
            agregarPublicacionFragment.mensaje(context,"Agregue una descripcion");
        } else if (fotoMascota.getDrawable() == null){
            agregarPublicacionFragment.mensaje(context,"Agregue una foto de la mascota");
        } else {
            if(nombreSN == true){
                publicacionDatosModel.setNombre(nombre.getText().toString());
            }else{
                publicacionDatosModel.setNombre("Sin Nombre");
            }
            publicacionDatosModel.setRaza(raza.getText().toString());
            publicacionDatosModel.setGenero(genero.getSelectedItem().toString());
            publicacionDatosModel.setEdad(edad.getText().toString() + " " + formatoEdad.getSelectedItem().toString());
            publicacionDatosModel.setDireccion(municipio.getSelectedItem().toString() + ", " +
                    departamento.getSelectedItem().toString());
            publicacionDatosModel.setDescripcion(descripcion.getText().toString());
            guardarFotoMascota(fotoM, context);
        }
    }

    public void cargarFotoNombre(Context context, ImageView fotoUser, TextView nombreUser){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Datos_Usuario", MODE_PRIVATE);
        String url = sharedPreferences.getString("URLFoto", "valor_por_defecto");
        String nombres = sharedPreferences.getString("Nombres", "valor_por_defecto");
        String apellidos = sharedPreferences.getString("Apellidos", "valor_por_defecto");

        if (!url.equals("valor_por_defecto")) {
            Glide.with(context)
                    .load(Uri.parse(url))
                    .apply(new RequestOptions().circleCrop().override(50, 50))
                    .into(fotoUser);
        }

        nombreUser.setText(nombres + " " + apellidos);
    }

    public void cargarSpinnerGenero(Spinner spinnerGenero, Context context){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.genero,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenero.setAdapter(adapter);
    }

    public void cargarSpinnerDepartamentos(Spinner spinnerDepartamentos, Context context){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.departamentos,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartamentos.setAdapter(adapter);
    }

    public void cargarSpinnerMunicipios(Spinner spinnerMunicipios, Context context, String departamento){
        int municipiosArrayId = obtenerMunicipiosArrayId(departamento);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, municipiosArrayId,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMunicipios.setAdapter(adapter);
    }

    public void cargarSpinnerEdad(Spinner spinnerEdad, Context context){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.edad,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEdad.setAdapter(adapter);
    }

    private int obtenerMunicipiosArrayId(String departamento) {
        int municipiosArrayId = 0;
        switch (departamento) {
            case "Petén":
                municipiosArrayId = R.array.Petén;
            break;

            default:
                municipiosArrayId = R.array.default_municipios;
                break;
        }
        return municipiosArrayId;
    }

    public void guardarPublicacion(Context context, String ID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = context.getSharedPreferences("Datos_Usuario", MODE_PRIVATE);
        String id = sharedPreferences.getString("Id", "valor_por_defecto");
        String telefono = String.valueOf(sharedPreferences.getInt("Telefono", 0));
        String direccion = publicacionDatosModel.getDireccion();
        String[] partesDireccion = direccion.split(",");

        String municipio = partesDireccion[0].trim();
        String departamento = partesDireccion[1].trim();

        Timestamp fechaActual = Timestamp.now();

        Map<String, Object> mascota = new HashMap<>();
        mascota.put("Id Usuario", id);
        mascota.put("Nombre Mascota", publicacionDatosModel.getNombre());
        mascota.put("Raza", publicacionDatosModel.getRaza());
        mascota.put("Genero", publicacionDatosModel.getGenero());
        mascota.put("Edad", publicacionDatosModel.getEdad());
        mascota.put("Departamento", departamento);
        mascota.put("Municipio", municipio);
        mascota.put("Descripcion Mascota", publicacionDatosModel.getDescripcion());
        mascota.put("URL Foto Mascota", publicacionDatosModel.getURLFotoMascota());
        mascota.put("Fecha Publicacion", fechaActual);
        mascota.put("Id Publicacion", ID);
        mascota.put("Telefono Contacto", telefono);

        db.collection("Publicaciones").document(ID).set(mascota).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    agregarPublicacionFragment.mensaje(context, "Se ha realizado la publicacion.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                agregarPublicacionFragment.mensaje(context, "Error al realizar la publicacion.");
                Log.d("ERROR", e.getMessage());
            }
        });
    }

    public void guardarFotoMascota(Uri fotoM, Context context){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String id = UUID.randomUUID().toString();

        StorageReference imageRef = storageRef.child("ImagenPublicaciones/" + id);
        UploadTask uploadTask = imageRef.putFile(fotoM);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        publicacionDatosModel.setURLFotoMascota(uri.toString());
                        guardarPublicacion(context, id);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
}

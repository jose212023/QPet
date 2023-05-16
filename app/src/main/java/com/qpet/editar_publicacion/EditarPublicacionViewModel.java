package com.qpet.editar_publicacion;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.qpet.R;
import com.qpet.inicio.InicioModel;
import com.qpet.models.PublicacionDatosModel;

import java.util.HashMap;
import java.util.Map;

public class EditarPublicacionViewModel extends ViewModel {

    PublicacionDatosModel publicacionDatosModel;

    public EditarPublicacionViewModel(){publicacionDatosModel = new PublicacionDatosModel();}

    EditarPublicacionFragment editarPublicacionFragment = new EditarPublicacionFragment();

    public void cargarDatos(InicioModel publicacion, Context context, EditText nombre, EditText raza, EditText edad, EditText descripcion, Spinner genero,
                            Spinner formatoEdad, Spinner departamento, Spinner municipio, ImageView fotoMascota,
                            Uri fotoM, CheckBox checkBox, LinearLayout linearLayout){

        int positionG, positionEF, positionD;

        if(!publicacion.getNombre().equals("Sin Nombre")){
            checkBox.setChecked(true);
            linearLayout.setVisibility(View.VISIBLE);
            nombre.setText(publicacion.getNombre());
        }

        raza.setText(publicacion.getRaza());
        ArrayAdapter<String> adapterG = (ArrayAdapter<String>) genero.getAdapter();
        if(adapterG != null){
            positionG = adapterG.getPosition(publicacion.getGenero());
            if(positionG != -1){
                genero.setSelection(positionG);
            }
        }
        String[] edadSeparada = publicacion.getEdad().split(" ");
        edad.setText(edadSeparada[0].trim());
        ArrayAdapter<String> adapterEF = (ArrayAdapter<String>) formatoEdad.getAdapter();
        if(adapterEF != null){
            positionD = adapterEF.getPosition(edadSeparada[1].trim());
            if(positionD != -1){
                formatoEdad.setSelection(positionD);
            }
        }
        String[] direccionSeparada = publicacion.getDireccion().split(", ");

        ArrayAdapter<String> adapterD = (ArrayAdapter<String>) departamento.getAdapter();
        if(adapterD != null){
            positionEF = adapterD.getPosition(direccionSeparada[1].trim());
            if(positionEF != -1){
                departamento.setSelection(positionEF);
            }
        }
        cargarSpinnerMunicipios(municipio, context, departamento.getSelectedItem().toString());

        ArrayAdapter<String> adapterM = (ArrayAdapter<String>) municipio.getAdapter();
        if(adapterM != null){
            String municipioSeleccionado = direccionSeparada[0].trim();
            for (int i = 0; i < adapterM.getCount(); i++) {
                if (adapterM.getItem(i).equalsIgnoreCase(municipioSeleccionado)) {
                    municipio.setSelection(i);
                    break;
                }
            }
        }

        descripcion.setText(publicacion.getDescripcion());
        if (publicacion.getURLFotoMascota() != null) {
            Glide.with(context)
                    .load(Uri.parse(publicacion.getURLFotoMascota()))
                    .fitCenter()
                    .into(fotoMascota);
        }
        publicacionDatosModel.setURLFotoMascota(publicacion.getURLFotoMascota());
    }

    public void verificarCampos(EditText nombre, EditText raza, EditText edad, EditText descripcion, Spinner genero,
                                Spinner formatoEdad, Spinner departamento, Spinner municipio,
                                ImageView fotoMascota, Context context, Uri fotoM, String ID, CheckBox checkBox){
        if (checkBox.isChecked() && nombre.getText().toString().isEmpty()){
            editarPublicacionFragment.mensaje(context,"Campo Nombre Mascota Requerido");
        } else if (raza.getText().toString().isEmpty()){
            editarPublicacionFragment.mensaje(context,"Campo Raza requerido");
        } else if (genero.getSelectedItem().toString().equals("Seleccionar")) {
            editarPublicacionFragment.mensaje(context,"Seleccione el genero de la mascota");
        } else if (edad.getText().toString().isEmpty()) {
            editarPublicacionFragment.mensaje(context,"Campo Edad requerido");
        } else if (formatoEdad.getSelectedItem().toString().equals("Seleccionar")) {
            editarPublicacionFragment.mensaje(context,"Seleccione un formato para edad");
        } else if (departamento.getSelectedItem().toString().equals("Seleccionar")) {
            editarPublicacionFragment.mensaje(context,"Seleccione un departamento");
        } else if (municipio.getSelectedItem().toString().equals("Seleccionar")) {
            editarPublicacionFragment.mensaje(context,"Seleccione un municipio");
        } else if (descripcion.getText().toString().isEmpty()) {
            editarPublicacionFragment.mensaje(context,"Agregue una descripcion");
        } else if (fotoMascota.getDrawable() == null){
            editarPublicacionFragment.mensaje(context,"Agregue una foto de la mascota");
        } else {
            if(checkBox.isChecked()){
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
            if(fotoM == null){
                editarPublicacion(context, ID);
            }else{
                editarFotoMascota(fotoM, context, ID);
            }
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

    public void editarPublicacion(Context context, String ID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String direccion = publicacionDatosModel.getDireccion();
        String[] partesDireccion = direccion.split(",");

        String municipio = partesDireccion[0].trim();
        String departamento = partesDireccion[1].trim();

        Map<String, Object> mascota = new HashMap<>();
        mascota.put("Nombre Mascota", publicacionDatosModel.getNombre());
        mascota.put("Raza", publicacionDatosModel.getRaza());
        mascota.put("Genero", publicacionDatosModel.getGenero());
        mascota.put("Edad", publicacionDatosModel.getEdad());
        mascota.put("Departamento", departamento);
        mascota.put("Municipio", municipio);
        mascota.put("Descripcion Mascota", publicacionDatosModel.getDescripcion());
        mascota.put("URL Foto Mascota", publicacionDatosModel.getURLFotoMascota());

        NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment_content_main);

        db.collection("Publicaciones").document(ID).update(mascota).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    editarPublicacionFragment.mensaje(context, "Se ha editado la publicacion.");
                    navController.navigate(R.id.nave_publicaciones);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                editarPublicacionFragment.mensaje(context, "Error al editar la publicacion.");
                Log.d("ERROR", e.getMessage());
            }
        });
    }

    public void editarFotoMascota(Uri fotoM, Context context, String ID){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference imageRef = storageRef.child("ImagenPublicaciones/" + ID);
        UploadTask uploadTask = imageRef.putFile(fotoM);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        publicacionDatosModel.setURLFotoMascota(uri.toString());
                        editarPublicacion(context, ID);
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

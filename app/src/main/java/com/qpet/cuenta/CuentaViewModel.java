package com.qpet.cuenta;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.qpet.models.DatosUsuarioModel;

public class CuentaViewModel extends ViewModel {
    DatosUsuarioModel dataUser;
    public CuentaViewModel() {
        dataUser = new DatosUsuarioModel();
    }

    public void obtenerDatos(Context context, ImageView photo, TextView nombres, TextView apellidos, TextView edad,
                             TextView direccion, TextView telefono, TextView numMascotas, TextView correo){

        sharadedDatos(context);

        if (dataUser.getUserURLPhoto() != null) {
            Glide.with(context)
                    .load(Uri.parse(dataUser.getUserURLPhoto()))
                    .apply(new RequestOptions().circleCrop())
                    .into(photo);
        }

        nombres.setText(dataUser.getUserNombres());
        apellidos.setText(dataUser.getUserApellidos());
        edad.setText(String.valueOf(dataUser.getUserEdad()));
        direccion.setText(dataUser.getUserDireccion());
        telefono.setText(String.valueOf(dataUser.getUserPhoneNumber()));
        numMascotas.setText(String.valueOf(dataUser.getUserCantidadMascotas()));
        correo.setText(dataUser.getUserCorreo());
    }

    public void sharadedDatos(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Datos_Usuario", MODE_PRIVATE);
        String url = sharedPreferences.getString("URLFoto", "valor_por_defecto");
        String nombres = sharedPreferences.getString("Nombres", "valor_por_defecto");
        String apellidos = sharedPreferences.getString("Apellidos", "valor_por_defecto");
        int edad = sharedPreferences.getInt("Edad", 0);
        String direccion = sharedPreferences.getString("Direccion", "valor_por_defecto");
        int telefono = sharedPreferences.getInt("Telefono", 0);
        int numMascotas = sharedPreferences.getInt("NumMascotas", 0);
        String correo = sharedPreferences.getString("Correo", "valor_por_defecto");

        dataUser.setUserURLPhoto(url);
        dataUser.setUserNombres(nombres);
        dataUser.setUserApellidos(apellidos);
        dataUser.setUserEdad(edad);
        dataUser.setUserDireccion(direccion);
        dataUser.setUserPhoneNumber(telefono);
        dataUser.setUserCantidadMascotas(numMascotas);
        dataUser.setUserCorreo(correo);
    }

}

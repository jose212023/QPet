package com.qpet.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.qpet.R;
import com.qpet.controladores.Controller;
import com.qpet.models.DatosUsuarioModel;

import java.io.FileDescriptor;
import java.io.IOException;

public class IngresarDatosNuevoUsuario extends AppCompatActivity implements ActivityResultCallback<Uri>{

    Controller cont;
    EditText nombres, apellidos, edad, direccion, telefono, numMascotas;
    ImageView userPhoto;
    Button guardar, addPhoto;
    String correo;
    Uri photo;

    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_datos_nuevo_usuario);

        Intent intent = getIntent();
        correo = intent.getStringExtra("Correo_Electronico");

        cont = new Controller(null, null, this);

        userPhoto = (ImageView) findViewById(R.id.imageView_userPhoto);

        nombres = (EditText) findViewById(R.id.editTextText_userNames);
        apellidos = (EditText) findViewById(R.id.editTextText_userLastNames);
        edad = (EditText) findViewById(R.id.editTextNumber_userEdad);
        direccion = (EditText) findViewById(R.id.editTextText_userDireccion);
        telefono = (EditText) findViewById(R.id.editTextNumber_userTelefono);
        numMascotas = (EditText) findViewById(R.id.editTextNumber_userCantidadMascotas);

        guardar = (Button) findViewById(R.id.button_guardarDatos);
        addPhoto = (Button) findViewById(R.id.button_add_userPhoto);

        Listener();
    }

    public void Listener(){
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cont.verificarDatos(userPhoto, nombres, apellidos, edad, direccion, telefono, numMascotas, correo, photo);
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });
    }

    private ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                            userPhoto.setImageBitmap(getRoundedBitmap(bitmap, 200));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        photo = result;
                    }
                }
            });

    private Bitmap getRoundedBitmap(Bitmap bitmap, int radius) {
        Bitmap finalBitmap;
        if(bitmap.getWidth() != radius || bitmap.getHeight() != radius)
            finalBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius, false);
        else
            finalBitmap = bitmap;
        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(), finalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(new RectF(rect), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);

        return output;
    }

    @Override
    public void onActivityResult(Uri result) {
        if (result != null) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                userPhoto.setImageBitmap(getRoundedBitmap(bitmap, 200));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void accederMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void mensaje(String mensaje){
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

}
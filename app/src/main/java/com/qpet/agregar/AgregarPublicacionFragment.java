package com.qpet.agregar;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qpet.R;
import com.qpet.activities.MainActivity;
import com.qpet.databinding.FragmentAgregarPublicacionBinding;
import com.qpet.inicio.InicioModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AgregarPublicacionFragment extends Fragment {
    private FragmentAgregarPublicacionBinding binding;
    CheckBox checkBox;
    LinearLayout linearLayout;
    Spinner spinnerGenero, spinnerDepartamentos, spinnerMunicipios, spinnerEdad;
    EditText nombre, raza, edad, descripcion;
    TextView nombreUser;
    ImageView fotoMascota, userPhoto;
    Button publicar, agregarFoto;
    boolean nombreSN = false;
    Uri photo;
    AgregarPublicacionViewModel agregarPublicacionViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         agregarPublicacionViewModel =
                new ViewModelProvider(this).get(AgregarPublicacionViewModel.class);

        binding = FragmentAgregarPublicacionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        nombre = binding.editTextTextMascotaName;
        raza = binding.editTextTextMacostaRaza;
        edad = binding.editTextNumberEdad;
        descripcion = binding.editTextDescripcion;
        checkBox = binding.checkBoxNombreMascota;
        linearLayout = binding.linearLayoutNombreMascota;
        spinnerGenero = binding.spinnerGenero;
        spinnerDepartamentos = binding.spinnerDepartamento;
        spinnerMunicipios = binding.spinnerMunicipio;
        spinnerEdad = binding.spinnerEdad;
        publicar = binding.btnPublicar;
        agregarFoto = binding.btnAddImagen;
        fotoMascota = binding.imageViewFotoMascota;
        userPhoto = binding.imageViewUserCuentaPhoto;
        nombreUser = binding.txtNombreUser;

        agregarPublicacionViewModel.cargarSpinnerGenero(spinnerGenero, requireContext());
        agregarPublicacionViewModel.cargarSpinnerDepartamentos(spinnerDepartamentos, requireContext());
        agregarPublicacionViewModel.cargarSpinnerEdad(spinnerEdad, requireContext());
        agregarPublicacionViewModel.cargarFotoNombre(requireContext(), userPhoto, nombreUser);

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            ((AppCompatActivity) mainActivity).getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_menu);

            DrawerLayout drawerLayout = mainActivity.findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(mainActivity, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }

        listener();

        return root;
    }

    public void listener(){
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    linearLayout.setVisibility(View.VISIBLE);
                    nombreSN = true;
                }else{
                    linearLayout.setVisibility(View.GONE);
                    nombreSN = false;
                }
            }
        });

        spinnerDepartamentos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String departamento = parent.getSelectedItem().toString();
                agregarPublicacionViewModel.cargarSpinnerMunicipios(spinnerMunicipios, requireContext(), departamento);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        publicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarPublicacionViewModel.verificarCampos(nombre,raza, edad, descripcion, spinnerGenero, spinnerEdad,
                        spinnerDepartamentos, spinnerMunicipios, nombreSN, fotoMascota, requireContext(), photo);
            }
        });

        agregarFoto.setOnClickListener(new View.OnClickListener() {
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
                        int imageViewWidth = fotoMascota.getWidth();
                        int imageViewHeight = fotoMascota.getHeight();
                        if (imageViewWidth == 0 && imageViewHeight == 0) {
                            Glide.with(requireContext())
                                    .load(result)
                                    .into(fotoMascota);
                            photo = result;
                        } else {
                            openCropImageActivity(result);
                        }
                    }
                }
            });

    private void openCropImageActivity(Uri imageUri) {
        Intent cropIntent = CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .getIntent(requireContext());
        cropActivityResultLauncher.launch(cropIntent);
    }

    private ActivityResultLauncher<Intent> cropActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    CropImage.ActivityResult cropResult = CropImage.getActivityResult(result.getData());
                    if (cropResult != null && cropResult.getUri() != null) {
                        // Obtener la imagen recortada y cargarla con Glide
                        Uri croppedImageUri = cropResult.getUri();
                        Glide.with(requireContext())
                                .load(croppedImageUri)
                                .into(fotoMascota);
                        photo = croppedImageUri;
                    }
                }else if (result.getResultCode() == RESULT_CANCELED) {
                    Log.d("CropImage", "La actividad de recorte de imágenes se canceló");
                }
            }
    );
    public void mensaje(Context context, String mensaje){
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
package com.qpet.editar_publicacion;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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

import com.bumptech.glide.Glide;
import com.qpet.R;
import com.qpet.activities.MainActivity;
import com.qpet.databinding.FragmentEditarPublicacionBinding;
import com.qpet.databinding.FragmentMisPublicacionesBinding;
import com.qpet.inicio.InicioModel;
import com.qpet.publicaciones.MisPublicacionesAdapter;
import com.qpet.publicaciones.MisPublicacionesViewModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class EditarPublicacionFragment extends Fragment {

    private FragmentEditarPublicacionBinding binding;

    CheckBox checkBox;
    LinearLayout linearLayout;
    Spinner spinnerGenero, spinnerDepartamentos, spinnerMunicipios, spinnerEdad;
    EditText nombre, raza, edad, descripcion;
    TextView nombreUser;
    ImageView fotoMascota, userPhoto;
    Button editar, agregarFoto;
    boolean nombreSN = false;
    Uri photo;
    String idPublicacion;
    EditarPublicacionViewModel editarPublicacionViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        editarPublicacionViewModel =
                new ViewModelProvider(this).get(EditarPublicacionViewModel.class);

        binding = FragmentEditarPublicacionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        nombre = binding.editTextTextMascotaNameE;
        raza = binding.editTextTextMacostaRazaE;
        edad = binding.editTextNumberEdadE;
        descripcion = binding.editTextDescripcionE;
        checkBox = binding.checkBoxNombreMascotaE;
        linearLayout = binding.linearLayoutNombreMascotaE;
        spinnerGenero = binding.spinnerGeneroE;
        spinnerDepartamentos = binding.spinnerDepartamentoE;
        spinnerMunicipios = binding.spinnerMunicipioE;
        spinnerEdad = binding.spinnerEdadE;
        editar = binding.btnEditarEP;
        agregarFoto = binding.btnAddImagenE;
        fotoMascota = binding.imageViewFotoMascotaE;
        userPhoto = binding.imageViewUserCuentaPhotoE;
        nombreUser = binding.txtNombreUserE;

        editarPublicacionViewModel.cargarSpinnerGenero(spinnerGenero, requireContext());
        editarPublicacionViewModel.cargarSpinnerDepartamentos(spinnerDepartamentos, requireContext());
        editarPublicacionViewModel.cargarSpinnerEdad(spinnerEdad, requireContext());
        editarPublicacionViewModel.cargarFotoNombre(requireContext(), userPhoto, nombreUser);

        Bundle args = getArguments();
        if (args != null && args.containsKey("publicacion")) {
            InicioModel publicacion = args.getParcelable("publicacion");
            idPublicacion = publicacion.getIdPublicacion();
            editarPublicacionViewModel.cargarDatos(publicacion, requireContext(), nombre,raza, edad, descripcion, spinnerGenero, spinnerEdad,
                    spinnerDepartamentos, spinnerMunicipios, fotoMascota, photo, checkBox, linearLayout);
        }

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
                int municipioSelection = spinnerMunicipios.getSelectedItemPosition();
                editarPublicacionViewModel.cargarSpinnerMunicipios(spinnerMunicipios, requireContext(), departamento);
                spinnerMunicipios.setSelection(municipioSelection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarPublicacionViewModel.verificarCampos(nombre,raza, edad, descripcion, spinnerGenero, spinnerEdad,
                        spinnerDepartamentos, spinnerMunicipios,fotoMascota, requireContext(), photo, idPublicacion, checkBox);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void mensaje(Context context, String mensaje){
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
    }

}
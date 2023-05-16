package com.qpet.editar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.qpet.R;
import com.qpet.activities.MainActivity;
import com.qpet.cuenta.CuentaFragment;
import com.qpet.databinding.ActivityMainBinding;
import com.qpet.databinding.FragmentEditarInfoBinding;

import java.io.IOException;

public class EditarInfoFragment extends Fragment {
    private FragmentEditarInfoBinding binding;
    private ActivityMainBinding mBinding;
    private ActionBarDrawerToggle toggle;
    Uri photo;
    ImageView foto;

    private NavController navController;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EditarInfoViewModel editarInfoViewModel =
                new ViewModelProvider(this).get(EditarInfoViewModel.class);

        binding = FragmentEditarInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        View view = inflater.inflate(R.layout.activity_main, container, false);
        mBinding = ActivityMainBinding.bind(view);

        final Button btnCancelar = binding.buttonCancelar;
        final Button btnGuardar = binding.buttonGuardarDatos;
        final Button btnAddFoto = binding.buttonAddUserPhoto;
        foto = binding.imageViewEditPhoto;
        final EditText nombres = binding.editTextTextEditNames, apellidos = binding.editTextTextEditLastNames,
                edad = binding.editTextNumberEditEdad, direccion = binding.editTextTexteEditDireccion,
                telefono = binding.editTextNumberEditTelefono, numMascotas = binding.editTextNumberEditCantidadMascotas;

        if (getActivity() != null && getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            ((AppCompatActivity) mainActivity).getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_menu);

            DrawerLayout drawerLayout = mainActivity.findViewById(R.id.drawer_layout);
            toggle = new ActionBarDrawerToggle(mainActivity, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }

        editarInfoViewModel.cargarDatos(requireContext(), foto, nombres, apellidos, edad, direccion, telefono, numMascotas);

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_inicio, R.id.nav_cuenta, R.id.nave_publicaciones, R.id.nave_editar_informacion_cuenta, R.id.nav_log_out)
                .setOpenableLayout(mBinding.drawerLayout)
                .build();
        NavigationUI.setupActionBarWithNavController((AppCompatActivity) requireActivity(), navController, appBarConfiguration);
        NavigationUI.setupWithNavController(mBinding.navView, navController);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.popBackStack();
                getActivity().getSupportFragmentManager().executePendingTransactions();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarInfoViewModel.verificarDatos(requireContext(), foto, nombres, apellidos, edad, direccion, telefono, numMascotas, photo);
                navController.popBackStack();
                getActivity().getSupportFragmentManager().executePendingTransactions();
            }
        });

        btnAddFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        return root;
    }

    private ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), result);
                            foto.setImageBitmap(getRoundedBitmap(bitmap, 200));
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
    public void onDestroyView() {
        super.onDestroyView();
        if (toggle != null) {
            DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
            drawerLayout.removeDrawerListener(toggle);
        }
        binding = null;
    }

    public void mensaje(Context context, String mensaje){
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
    }

}

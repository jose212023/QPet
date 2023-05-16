    package com.qpet.cuenta;

    import android.net.Uri;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.TextView;

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

    import com.bumptech.glide.Glide;
    import com.bumptech.glide.request.RequestOptions;
    import com.qpet.R;
    import com.qpet.activities.MainActivity;
    import com.qpet.databinding.FragmentCuentaBinding;
    import com.qpet.editar.EditarInfoFragment;

    public class CuentaFragment extends Fragment {
        private FragmentCuentaBinding binding;
        private ActionBarDrawerToggle toggle;
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            CuentaViewModel cuentaViewModel =
                    new ViewModelProvider(this).get(CuentaViewModel.class);

            binding = FragmentCuentaBinding.inflate(inflater, container, false);
            View root = binding.getRoot();

            final ImageView photo = binding.imageViewUserCuentaPhoto;
            final TextView nombres = binding.textViewUserCuentaNames, apellidos = binding.textViewUserCuentaLastNames,
                    edad = binding.textViewUserCuentaAge, direccion = binding.textViewUserCuentaAddress,
                    telefono = binding.textViewUserCuentaPhone, numMascotas = binding.textViewUserCuentaNumPets,
                    correo = binding.textViewUserCuentaCorreo;
            final Button btnEditarInformacionCuenta = binding.buttonEditarDatos;

            cuentaViewModel.obtenerDatos(getContext(), photo, nombres, apellidos, edad, direccion, telefono, numMascotas, correo);

            if (getActivity() != null && getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();
                ((AppCompatActivity) mainActivity).getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_menu);

                DrawerLayout drawerLayout = mainActivity.findViewById(R.id.drawer_layout);
                toggle = new ActionBarDrawerToggle(mainActivity, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawerLayout.addDrawerListener(toggle);
                toggle.syncState();
            }

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            btnEditarInformacionCuenta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navController.navigate(R.id.nave_editar_informacion_cuenta);
                }
            });

            return root;
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

    }
package com.qpet.publicaciones;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qpet.R;
import com.qpet.activities.MainActivity;
import com.qpet.databinding.FragmentMisPublicacionesBinding;
import com.qpet.inicio.InicioModel;
import com.qpet.inicio.MarginItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MisPublicacionesFragment extends Fragment{
    private FragmentMisPublicacionesBinding binding;
    private RecyclerView recyclerViewUser;
    public RecyclerView.Adapter mAdapter;

    private MisPublicacionesAdapter adapter;
    public List<InicioModel> inicioList = new ArrayList<>();
    private int numberOfColumns = 1;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MisPublicacionesViewModel misPublicacionesViewModel =
                new ViewModelProvider(this).get(MisPublicacionesViewModel.class);

        binding = FragmentMisPublicacionesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            ((AppCompatActivity) mainActivity).getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_menu);

            DrawerLayout drawerLayout = mainActivity.findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(mainActivity, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }

        misPublicacionesViewModel.obtnerPublicacion(requireContext());

        misPublicacionesViewModel.getMisPublicaciones().observe(getViewLifecycleOwner(), publicaciones -> {
            // Actualiza la lista de publicaciones del adaptador
            inicioList.clear();
            inicioList.addAll(publicaciones);
            adapter.notifyDataSetChanged();
        });

        recyclerViewUser = binding.recyclerViewMisPublicaciones;

        int espacioEntreItemsEnPixeles = getResources().getDimensionPixelSize(R.dimen.espacio_publicacion);
        Drawable divider = ContextCompat.getDrawable(requireContext(), R.drawable.divider);

        MarginItemDecoration itemDecoration = new MarginItemDecoration(divider, espacioEntreItemsEnPixeles);
        recyclerViewUser.addItemDecoration(itemDecoration);

        recyclerViewUser.setHasFixedSize(true);

        recyclerViewUser.setLayoutManager(new GridLayoutManager(requireContext(), numberOfColumns));

        adapter = new MisPublicacionesAdapter(inicioList, requireContext(), misPublicacionesViewModel);
        recyclerViewUser.setAdapter(adapter);

        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void mensaje(String mensaje, Context context){
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
    }

}
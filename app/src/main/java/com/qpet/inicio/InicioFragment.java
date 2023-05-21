package com.qpet.inicio;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;
import com.qpet.R;
import com.qpet.databinding.FragmentInicioBinding;

import java.util.ArrayList;
import java.util.List;

public class InicioFragment extends Fragment {

    private FragmentInicioBinding binding;
    private RecyclerView recyclerViewUser;
    public RecyclerView.Adapter mAdapter;
    public List<InicioModel> inicioList = new ArrayList<>();
    private int numberOfColumns = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InicioViewModel inicioViewModel =
                new ViewModelProvider(this).get(InicioViewModel.class);

        binding = FragmentInicioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        inicioViewModel.obtnerPublicacion();

        inicioViewModel.getPublicaciones().observe(getViewLifecycleOwner(), publicaciones -> {
            // Actualiza la lista de publicaciones del adaptador
            inicioList.clear();
            inicioList.addAll(publicaciones);
            mAdapter.notifyDataSetChanged();
        });

        recyclerViewUser = binding.recyclerViewInicio;

        int espacioEntreItemsEnPixeles = getResources().getDimensionPixelSize(R.dimen.espacio_publicacion);
        Drawable divider = ContextCompat.getDrawable(requireContext(), R.drawable.divider);

        MarginItemDecoration itemDecoration = new MarginItemDecoration(divider, espacioEntreItemsEnPixeles);
        recyclerViewUser.addItemDecoration(itemDecoration);

        recyclerViewUser.setHasFixedSize(true);

        recyclerViewUser.setLayoutManager(new GridLayoutManager(requireContext(), numberOfColumns));

        InicioAdapter.OnContactarClickListener contactarClickListener = new InicioAdapter.OnContactarClickListener() {
            @Override
            public void onContactarClick(String phoneNumber, String message) {
                sendWhatsAppMessage(phoneNumber, message);
            }
        };

        mAdapter = new InicioAdapter(inicioList, requireContext(), contactarClickListener);
        recyclerViewUser.setAdapter(mAdapter);

        return root;
    }

    private void sendWhatsAppMessage(String phoneNumber, String message) {
        try {
            Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + message);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            requireContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "WhatsApp no está instalado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
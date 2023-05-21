package com.qpet.publicaciones;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.qpet.R;
import com.qpet.inicio.InicioModel;

import java.util.List;

public class MisPublicacionesAdapter extends RecyclerView.Adapter<MisPublicacionesAdapter.ViewHolder>{
    private List<InicioModel> inicioModelList;
    private Context context;

    private MisPublicacionesViewModel misPublicacionesViewModel;
    public MisPublicacionesAdapter(List<InicioModel> inicioModelList, Context context, MisPublicacionesViewModel misPublicacionesViewModel){
        this.inicioModelList = inicioModelList;
        this.context = context;
        this.misPublicacionesViewModel = misPublicacionesViewModel;
    }
    @Override
    public MisPublicacionesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mi_publicacion_view, parent, false);
        return new MisPublicacionesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MisPublicacionesAdapter.ViewHolder holder, int position) {
        Glide.with(context)
                .load(inicioModelList.get(position).getUserFotoURL())
                .apply(new RequestOptions().circleCrop().override(50, 50))
                .into(holder.userFoto);
        holder.userName.setText(inicioModelList.get(position).getUserName());
        holder.nombre.setText(inicioModelList.get(position).getNombre());
        holder.raza.setText(inicioModelList.get(position).getRaza());
        holder.genero.setText(inicioModelList.get(position).getGenero());
        holder.edad.setText(inicioModelList.get(position).getEdad());
        holder.direccion.setText(inicioModelList.get(position).getDireccion());
        holder.descripcion.setText(inicioModelList.get(position).getDescripcion());
        holder.telefono.setText(inicioModelList.get(position).getTelefono());
        Glide.with(context).load(inicioModelList.get(position).getURLFotoMascota()).into(holder.fotoMascota);
        holder.btnPostOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.menu_post_option);
                NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment_content_main);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit_post:
                                // Lógica para editar la publicación
                                int clickedPosition1 = holder.getBindingAdapterPosition();
                                if (clickedPosition1 != RecyclerView.NO_POSITION) {
                                    InicioModel publicacion = inicioModelList.get(clickedPosition1);
                                    Bundle args = new Bundle();
                                    args.putParcelable("publicacion", publicacion);
                                    navController.navigate(R.id.nav_editar_publicacion, args);
                                }
                                return true;
                            case R.id.delete_post:
                                // Lógica para eliminar la publicación
                                int clickedPosition2 = holder.getBindingAdapterPosition();
                                if (clickedPosition2 != RecyclerView.NO_POSITION) {
                                    InicioModel publicacion = inicioModelList.get(clickedPosition2);
                                    misPublicacionesViewModel.eliminar(publicacion, context);
                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return inicioModelList.size();
    }
    public static class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView nombre, raza, genero, edad, direccion, descripcion, userName, telefono;
        private ImageView fotoMascota, userFoto;

        private Button btnPostOptions;
        public ViewHolder(View v){
            super(v);
            nombre = (TextView) v.findViewById(R.id.textView_mascotaNombreMV);
            raza = (TextView) v.findViewById(R.id.textView_mascotaRazaMV);
            genero = (TextView) v.findViewById(R.id.textView_mascotaGeneroMV);
            edad = (TextView) v.findViewById(R.id.textView_mascotaEdadMV);
            direccion = (TextView) v.findViewById(R.id.textView_mascotaDireccionMV);
            descripcion = (TextView) v.findViewById(R.id.textView_descripcionMV);
            fotoMascota = (ImageView) v.findViewById(R.id.imageView_fotoMascotaMV);
            userName = (TextView) v.findViewById(R.id.txtNombreUserMV);
            userFoto = (ImageView) v.findViewById(R.id.imageView_userCuentaPhotoMV);
            btnPostOptions = (Button) v.findViewById(R.id.btn_post_options);
            telefono = (TextView) v.findViewById(R.id.textView_mascotaTelefonoV);
        }
    }
}

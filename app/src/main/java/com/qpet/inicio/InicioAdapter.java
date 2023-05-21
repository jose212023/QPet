package com.qpet.inicio;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.qpet.R;

import java.util.List;

public class InicioAdapter extends RecyclerView.Adapter<InicioAdapter.ViewHolder>{
    private final OnContactarClickListener contactarClickListener;
    private List<InicioModel> inicioModelList;
    private Context context;
    public InicioAdapter(List<InicioModel> inicioModelList, Context context, OnContactarClickListener contactarClickListener){
        this.inicioModelList = inicioModelList;
        this.context = context;
        this.contactarClickListener = contactarClickListener;
    }
    @Override
    public InicioAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.publicacion_view, parent, false);
        return new InicioAdapter.ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull InicioAdapter.ViewHolder holder, int position) {
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
        holder.contactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = inicioModelList.get(holder.getAdapterPosition()).getTelefono();
                InicioModel publicacion = inicioModelList.get(holder.getAdapterPosition());
                String message = "¡Hola! Estoy interesado/a en una de tus publicaciones en QPet.";
                contactarClickListener.onContactarClick(phoneNumber, message);
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
        private Button contactar;
        public ViewHolder(View v){
            super(v);
            nombre = (TextView) v.findViewById(R.id.textView_mascotaNombreV);
            raza = (TextView) v.findViewById(R.id.textView_mascotaRazaV);
            genero = (TextView) v.findViewById(R.id.textView_mascotaGeneroV);
            edad = (TextView) v.findViewById(R.id.textView_mascotaEdadV);
            direccion = (TextView) v.findViewById(R.id.textView_mascotaDireccionV);
            descripcion = (TextView) v.findViewById(R.id.textView_descripcionV);
            fotoMascota = (ImageView) v.findViewById(R.id.imageView_fotoMascotaV);
            userName = (TextView) v.findViewById(R.id.txtNombreUserV);
            userFoto = (ImageView) v.findViewById(R.id.imageView_userCuentaPhotoV);
            contactar = (Button) v.findViewById(R.id.btnContactar);
            telefono = (TextView) v.findViewById(R.id.textView_mascotaTelefonoV);
        }
    }

    public interface OnContactarClickListener {
        void onContactarClick(String phoneNumber, String message);
    }
}

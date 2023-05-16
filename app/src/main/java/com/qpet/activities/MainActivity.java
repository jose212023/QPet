package com.qpet.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.qpet.R;
import com.qpet.controladores.Controller;
import com.qpet.cuenta.CuentaFragment;
import com.qpet.cuenta.CuentaViewModel;
import com.qpet.databinding.ActivityMainBinding;
import com.qpet.databinding.FragmentCuentaBinding;
import com.qpet.databinding.NavHeaderMainBinding;
import com.qpet.inicio.InicioFragment;
import com.qpet.publicaciones.MisPublicacionesFragment;

public class MainActivity extends AppCompatActivity {
    Controller cont;
    ImageView userPhoto;
    TextView nombres, apellidos, correo;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private NavHeaderMainBinding navHeader;
    private MenuItem logoutItem;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_inicio, R.id.nav_cuenta, R.id.nave_publicaciones, R.id.nave_editar_informacion_cuenta, R.id.nav_log_out)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        DrawerLayout drawerLayout = MainActivity.this.findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        logoutItem = binding.navView.getMenu().findItem(R.id.nav_log_out);
        logoutItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                SharedPreferences prefs = MainActivity.this.getSharedPreferences("MiAplicacionPrefs", Context.MODE_PRIVATE);
                prefs.edit().remove("sesionIniciada").apply();

                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
                return true;
            }
        });

        cont = new Controller(null, null, null, this);

        navHeader = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0));
        userPhoto = (ImageView) navHeader.imageViewNavUserPhoto;
        nombres = (TextView) navHeader.textViewNavUserName;
        apellidos = (TextView) navHeader.textViewNavUserLastName;
        correo = (TextView) navHeader.textViewNavUserEmail;

        cont.cargarDatos(userPhoto, nombres, apellidos, correo);
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){

        return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void mensaje(String mensaje){
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

}
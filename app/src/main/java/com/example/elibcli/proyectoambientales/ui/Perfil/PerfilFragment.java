package com.example.elibcli.proyectoambientales.ui.Perfil;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.elibcli.proyectoambientales.R;
import com.example.elibcli.proyectoambientales.ui.login.LoginActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class PerfilFragment extends Fragment {

    private PerfilViewModel PerfilViewModel;

    @Override public View onCreateView(LayoutInflater inflador, ViewGroup contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_usuario, contenedor, false);
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        String uid = usuario.getUid();
        FirebaseFirestore ref = FirebaseFirestore.getInstance();

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        TextView nombre = (TextView) vista.findViewById(R.id.nombre);
        TextView correo = (TextView) vista.findViewById(R.id.correo);
        correo.setText(usuario.getEmail());
        Button editarPerfil = (Button) vista.findViewById(R.id.editarPerfil);
        TextView numero = (TextView) vista.findViewById(R.id.telefono);
//

        //
        PerfilViewModel =
                ViewModelProviders.of(this).get(PerfilViewModel.class);

        Button cerrarSesion = (Button) vista.findViewById(R.id.btn_cerrar_sesion);

        final TextView textView = vista.findViewById(R.id.text_share);

        PerfilViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
      /*  editarPerfil.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View view) {
                                                Intent i = new Intent(getContext(), PerfilActivity.class);
                                                startActivity(i);
                                            }
                                        });

                                        */

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AuthUI.getInstance().signOut(getActivity())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent i = new Intent(getActivity(), LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                getActivity().finish();
                            }
                        });
            }
        });
        // Inicialización Volley

        RequestQueue colaPeticiones = Volley.newRequestQueue(getActivity()
                .getApplicationContext());
        ImageLoader lectorImagenes = new ImageLoader(colaPeticiones,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache =
                            new LruCache<String, Bitmap>(10);
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }
                });
        // Foto de perfil de usuario
        Uri urlImagen = usuario.getPhotoUrl();
        if (urlImagen != null) {
            NetworkImageView fotoUsuario = (NetworkImageView)
                    vista.findViewById(R.id.imagen);
            fotoUsuario.setImageUrl(urlImagen.toString(), lectorImagenes);
        }
        return vista;
    }
}

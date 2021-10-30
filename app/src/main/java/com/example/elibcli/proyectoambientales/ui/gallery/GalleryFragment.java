package com.example.elibcli.proyectoambientales.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elibcli.proyectoambientales.R;
import com.example.elibcli.proyectoambientales.data.model.FirebaseLogicaNegocio;
import com.example.elibcli.proyectoambientales.data.model.LoggedInUser;
import com.example.elibcli.proyectoambientales.data.model.nodeSensor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class GalleryFragment extends Fragment {
    RecyclerView recyclerView;
    DatabaseReference database;
    MyAdapter myAdapter;
    ArrayList<nodeSensor> list;
    LoggedInUser usuarioLogged;
    private String TAG = "LISTA DE SENSORES";
    private GalleryViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        //final TextView textView = root.findViewById(R.id.text_gallery);
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser(); //Obtenemos los datos del usuario que ha iniciado sesion
        if(usuario != null) { //vemos si el usuario ha iniciado sesión..


            usuarioLogged = new LoggedInUser(usuario.getUid(), usuario.getDisplayName(), usuario.getEmail()); //transformamos los datos de FirebaseAuth a los nuestros
            /* CREANDO RECYCLERVIEW PARA LA LISTA DE SENSORES ------------  */

            database = FirebaseDatabase.getInstance(FirebaseLogicaNegocio.urlDatabase).getReference().child(usuarioLogged.getUserId() + "/sensores/");
            Log.d(TAG, "Intentando obtener los datos de " + FirebaseLogicaNegocio.urlDatabase + " con este child: " + usuarioLogged.getUserId() + "/sensores/" );
            recyclerView = root.findViewById(R.id.userList);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

            list = new ArrayList<>();
            myAdapter = new MyAdapter(root.getContext(),list);
            recyclerView.setAdapter(myAdapter);

            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Log.d(TAG, "Nodo obtenido: " +  dataSnapshot.getValue(nodeSensor.class).toString());
                        nodeSensor node = dataSnapshot.getValue(nodeSensor.class); //adaptamos el resultado de Firebase al nuestro
                        list.add(node); //Añadimos nodo detectado


                    }
                    myAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {


        }


        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }
}
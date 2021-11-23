package com.example.elibcli.proyectoambientales.data.model;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.elibcli.proyectoambientales.ui.gallery.MyAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


//----------------------
//---------------------- Gestor de conexiones entre el Backend-Android a través de firebase
//----------------------


public class FirebaseLogicaNegocio {
    HashMap<String, Medida> listaMedidas = new HashMap<>();
    public static String urlDatabase = "https://medioambiente-c564b-default-rtdb.europe-west1.firebasedatabase.app";
    DatabaseReference database;
    ArrayList<Nodo> lista;
    private FirebaseDatabase mDatabase; //Real time database - Menor cantidad de datos a tiempo real.

    private FirebaseFirestore nDatabase = FirebaseFirestore.getInstance(); //Firestore database - Más lenta, mayor cantidad de datos y mejores queries

    private String TAG = "LOGICA";
    /*
     Diseño lógico:
        [Sensor],[usuario] -> guardarDispositivo()

        @param usuario

     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void enlazarDispositivo(Nodo nodo, LoggedInUser usuario) {
        HashMap<String, Object> docData = new HashMap<>();
        docData.put(nodo.getUuid(), nodo.toMap());
        docData.put("medidas", nodo.getMedidas());
        mDatabase = FirebaseDatabase.getInstance(urlDatabase);
        Log.d("SENSOR", "Intentando guardar dispositivo en Firebase... -> " + nodo.toMap().toString());
        mDatabase.getReference().child("usuarios/" + usuario.getUserId() + "/nodos/" + nodo.getUuid()).setValue(nodo.toMap());

        nDatabase.collection("usuarios").document(usuario.getUserId()).collection("nodos").document(nodo.getUuid())
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Nuevo nodo enlazado");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "ERROR en: Nuevo nodo enlazado", e);
                    }
                });

    }


    /*
     Diseño lógico:
        [Sensor],[usuario] -> eliminarDispositivos()

        @param usuario

     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void desenlazarDispositivos(Nodo nodo, LoggedInUser usuario) {


        nDatabase.collection("usuarios").document(usuario.getUserId()).collection("nodos").document(nodo.getUuid())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Nodo borrado");
                        Log.d(TAG, "NODO eliminado de Firebase -> " + nodo.toMap().toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "ERROR en: Nodo borrado", e);
                    }
                });


    }


     /*
     Diseño lógico:
        [Medicion] -> guardarMediciones()

        @param usuario

     */

    public void guardarMediciones(Medida medida, LoggedInUser usuario, Nodo nodo) {

        //Generamos numeros aleatorios cada tiempoDeEspera. CAMBIAR por datos REALES del SENSOR
        HashMap<String, Object> docData = new HashMap<>();

        docData.put("valor", medida.getValor());
       // docData.put("datetime", new Timestamp(new Date()).toDate());
        docData.put("latitud", medida.getLatitud());
        docData.put("longitud", medida.getLongitud());
        docData.put("tipo", medida.getTipo());

        mDatabase = FirebaseDatabase.getInstance(urlDatabase);

        mDatabase.getReference().child("usuarios/" + usuario.getUserId() + "/nodos/" + nodo.getUuid() + "/medidas/" + new Date()).setValue(docData);

        nDatabase.collection("usuarios").document(usuario.getUserId()).collection("nodos").document(nodo.getUuid()).collection("medidas").document(new Date().toString())
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Nueva medida guardada");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "ERROR en: Nueva medida guardada", e);
                    }
                });


    }
 /*
     Diseño lógico:
        [Usuario] -> obtenerListaNodos() -> [Lista mediciones]

        @param usuario

     */
    // Esta funcion devolvera la lista de sensores para que el usuario pueda adminitrar los sensores

    public ArrayList<Nodo> obtenerListaNodos(LoggedInUser usuarioLogged) {

        database = FirebaseDatabase.getInstance(FirebaseLogicaNegocio.urlDatabase).getReference().child("usuarios/" + usuarioLogged.getUserId() + "/nodos/");
        Log.d(TAG, "Intentando obtener los datos de " + FirebaseLogicaNegocio.urlDatabase + " con este child: usuarios/" + usuarioLogged.getUserId() + "/nodos/");


        ArrayList<Nodo> lista = new ArrayList<>();




        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d(TAG, "Nodo obtenido: " + dataSnapshot.getValue().toString());
                    Log.d(TAG, "Nodo obtenido: " + dataSnapshot.getValue(Nodo.class).toString());

                    Nodo node = dataSnapshot.getValue(Nodo.class); //adaptamos el resultado de Firebase al nuestro
                    lista.add(node); //Añadimos nodo detectado


                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "No se ha encontrado ningun nodo... error... " + error);
            }
        });


        return lista;
    }

    public ArrayList<Medida> obtenerUltimasMediciones(Nodo nodo, LoggedInUser usuarioLogged) throws InterruptedException {

        Thread.sleep(1000);
        database = FirebaseDatabase.getInstance(FirebaseLogicaNegocio.urlDatabase).getReference().child("usuarios/" + usuarioLogged.getUserId() + "/nodos/" + nodo.getUuid() + "/medidas/");
        Log.d(TAG, "Intentando obtener los datos de " + FirebaseLogicaNegocio.urlDatabase + " con este child: usuarios/" + usuarioLogged.getUserId() + "/nodos/" + obtenerListaNodos(usuarioLogged).get(obtenerListaNodos(usuarioLogged).size()-1) + "/medidas/");


        ArrayList<Medida> lista = new ArrayList<>(); //Esta lista es distinta puesto que devuelve medidas.


        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d(TAG, "Medida obtenido: " + dataSnapshot.getValue(Medida.class).toString());
                    Medida medida = dataSnapshot.getValue(Medida.class); //adaptamos el resultado de Firebase al nuestro
                    lista.add(medida); //Añadimos nodo detectado


                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return lista;
    }
    public HashMap<String, Medida> obtenerMedidas(Nodo nodo, LoggedInUser usuarioLogged)
    {
        database = FirebaseDatabase.getInstance(FirebaseLogicaNegocio.urlDatabase).getReference().child("usuarios/" + usuarioLogged.getUserId() + "/nodos/" + nodo.getUuid());
        Log.d(TAG, "Intentando obtener los datos de " + FirebaseLogicaNegocio.urlDatabase + " con este child: usuarios/" + usuarioLogged.getUserId() + "/nodos/");


        //Esta lista es distinta puesto que devuelve medidas.


        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d(TAG, "Nodo obtenido: " + dataSnapshot.getValue(Nodo.class).toString());
                    Nodo nodo = dataSnapshot.getValue(Nodo.class); //adaptamos el resultado de Firebase al nuestro
                    listaMedidas.put(dataSnapshot.getKey(), nodo.getMedidaFromUUID(dataSnapshot.getKey())); //Añadimos nodo detectado
                    Log.d(TAG, "Medida obtenida " + nodo.getMedidaFromUUID(dataSnapshot.getKey()).toString());
                    Log.d(TAG, "Mediciones de lista.toString() - " + listaMedidas.toString());

                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        Log.d(TAG, "Mediciones de lista.toString() - " + lista.toString());
        return listaMedidas;
    }
}

//--------------------------------
//-------------------------------- Esto lo usaremos probablemente en un futuro cuand osea necesario guardar datos mas complejos
//--------------------------------
/* Map<String, Object> nestedData = new HashMap<>();
     nestedData.put("a", 5);
     nestedData.put("b", true);

     docData.put("objectExample", nestedData);

     */
        /* BASE DE DATOS NO REAL TIME.
        FirebaseFirestore db = FirebaseFirestore.getInstance();



        db.collection("sensor").document("datos")
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "Datos enviados con exito");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase", "Error writing document", e);
                    }
                });
       */
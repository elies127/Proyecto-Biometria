package com.example.elibcli.proyectoambientales.data.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;


//----------------------
//---------------------- Gestor de conexiones entre el Backend-Android a través de firebase
//----------------------


public class FirebaseLogicaNegocio {

    private FirebaseDatabase mDatabase; //Real time database - Menor cantidad de datos a tiempo real.
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); //Firestore database - Más lenta, mayor cantidad de datos
    public static String urlDatabase = "https://medioambiente-c564b-default-rtdb.europe-west1.firebasedatabase.app";
    /*
     Diseño lógico:
        [Sensor],[usuario] -> guardarDispositivo()

        @param usuario

     */

    public void guardarDispositivo(nodeSensor sensor, LoggedInUser usuario){
        HashMap<String, Object> docData = new HashMap<>();
        docData.put(sensor.getUuid(), sensor.toMap());
        mDatabase = FirebaseDatabase.getInstance(urlDatabase);
        Log.d("SENSOR", "Intentando guardar dispositivo en Firebase... -> " + sensor.toMap().toString());
        mDatabase.getReference().child(usuario.getUserId() + "/sensores/").setValue(docData);
    }
    public void eliminarDispositivo(nodeSensor sensor, LoggedInUser usuario){
        String child = usuario.getUserId() + "/sensores/" + sensor.getUuid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child(usuario.getUserId() + "/sensores/").orderByChild(sensor.getUuid()).equalTo(sensor.getUuid());
        Log.d("SENSOR", "Intentando ELIMINAR dispositivo en Firebase... -> " + sensor.toMap().toString());
        ref = FirebaseDatabase.getInstance().getReference()
                .child(child);
        ref.removeValue();
        Log.d("SENSOR", "Sensor eliminado de Firebase -> " + sensor.toMap().toString());

    }


     /*
     Diseño lógico:
        [Medicion] -> guardarMediciones()

        @param usuario

     */

    public void guardarMediciones(float datos, LoggedInUser usuario){

        //Generamos numeros aleatorios cada tiempoDeEspera. CAMBIAR por datos REALES del SENSOR
        HashMap<String, Object> docData = new HashMap<>();
        docData.put("valor", datos);
        docData.put("momento", new Timestamp(new Date()).toDate());
        docData.put("usuario", usuario);

        mDatabase = FirebaseDatabase.getInstance(urlDatabase);

        mDatabase.getReference().child(usuario.getUserId() + "/datos/").setValue(docData);




    }

    // Esta funcion devolvera en un futuro los datos de la database.

    public String[] obtenerMediciones(){

            return null;
    }
    public String[] obtenerUltimasMediciones(int n){

            return null;
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
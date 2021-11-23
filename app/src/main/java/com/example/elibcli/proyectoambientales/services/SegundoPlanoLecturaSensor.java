package com.example.elibcli.proyectoambientales.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.elibcli.proyectoambientales.MainActivity;
import com.example.elibcli.proyectoambientales.R;
import com.example.elibcli.proyectoambientales.data.model.FirebaseLogicaNegocio;
import com.example.elibcli.proyectoambientales.data.model.LoggedInUser;
import com.example.elibcli.proyectoambientales.data.model.Medida;
import com.example.elibcli.proyectoambientales.data.model.Nodo;
import com.example.elibcli.proyectoambientales.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.type.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SegundoPlanoLecturaSensor extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private LoggedInUser usuarioLogged;
    private String tituloNoti = "";
    FirebaseLogicaNegocio logica;
    private DatabaseReference database;
    private HandlerThread handlerThread;
    private Handler handler;
    private HashMap<String, Medida> medidas;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String textoNoti;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void actualizarNotificacion(String texto, String titulo) {


        Intent notificationIntent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Recopilando datos").setStyle(new Notification.BigTextStyle()
                        .bigText(textoNoti))
                .setContentText(texto)
                .setSmallIcon(R.drawable.ic_menu_slideshow)
                .setContentIntent(pendingIntent).setOnlyAlertOnce(true)
                .setDefaults( Notification.PRIORITY_LOW)

                .build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, notification);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Runnable lecturaDatos() throws InterruptedException {

         handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
         handler = new Handler(looper);
        handler.post(() -> {
            Medida medida;
            textoNoti = "Descargando datos...";
            tituloNoti = "Buscando nodos...";

           /* try {
                handlerThread.getLooper().wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            */
            database = FirebaseDatabase.getInstance(FirebaseLogicaNegocio.urlDatabase).getReference().child("usuarios/" + usuarioLogged.getUserId() + "/nodos/");

            database.limitToLast(1).orderByKey().addChildEventListener(new ChildEventListener() {
                                          @Override
                                          public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                                              HashMap<String, Medida> listaMedidas = new HashMap<>();
                                              Log.d("NOTIFICACIONES", "Key: " + dataSnapshot.getKey() + " Nodo obtenido: " + dataSnapshot.getValue(Nodo.class).getMedidas().toString());
                                              Nodo nodo = dataSnapshot.getValue(Nodo.class); //adaptamos el resultado de Firebase al nuestro
                                              Log.d("NOTIFICACIONES", "Medida obtenida " + nodo.getMedidas());
                                              listaMedidas = nodo.getMedidas();

                                              Log.d("NOTIFICACIONES", "Mediciones de listaMedidas.toString() - " + listaMedidas.toString());
                                              textoNoti = "Hay un total de "+ listaMedidas.size() + " mediciones.";
                                              tituloNoti = "Nodo: "+ nodo.getBeaconName() + " conectado";
                                              actualizarNotificacion(textoNoti,tituloNoti);


                                          }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }


            });

/*
            try {
                handler.postDelayed(lecturaDatos(), 1000);

            } catch (InterruptedException e) {
                Log.d("NOTIFICACIONES", "Error en el thread secundario" + e);
            }


*/      Looper.loop();
        });

/*
    database = FirebaseDatabase.getInstance(FirebaseLogicaNegocio.urlDatabase).getReference().child("usuarios/" + usuarioLogged.getUserId() + "/nodos/");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Log.d("NODO", "Nodo obtenido: " +  dataSnapshot.getValue().toString());
                    Log.d("NODO", "Nodo obtenido: " +  dataSnapshot.getValue(Nodo.class).toString());
                    dataSnapshot.getValue().toString();
                    Nodo node = dataSnapshot.getValue(Nodo.class); //adaptamos el resultado de Firebase al nuestro
                    listaNodos.add(node); //Añadimos nodo detectado

                    Nodo nodoTemporal = new Nodo(2, 40, 40, "Nodo Hardcoded en ServicioEscucharBeacons",
                            "NodoHardcoded", 60);
                    logica.guardarMediciones(new Medida(Date.newBuilder().build(), "Hardcoded1", 1f, 1f, "1" ), usuarioLogged, nodoTemporal);
                    Log.d("NOTIFICACIONES", "Intentando descargar nodos... " + listaNodos.toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        */

    /* */


        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser(); //Obtenemos los datos del usuario que ha iniciado sesion
        if(usuario != null) { //vemos si el usuario ha iniciado sesión..

            usuarioLogged = new LoggedInUser(usuario.getUid(), usuario.getDisplayName(), usuario.getEmail()); //transformamos los datos de FirebaseAuth a los nuestros
            logica = new FirebaseLogicaNegocio(); //instanciamos una nueva logica para usar los métodos.
            String input = intent.getStringExtra("inputExtra");
            createNotificationChannel();
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);

            Notification notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Recopilando datos")
                    .setContentText(input).setStyle(new Notification.BigTextStyle()
                            .bigText(textoNoti))
                    .setSmallIcon(R.drawable.ic_menu_slideshow)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);
            Nodo nodoTemporal = new Nodo(2, 40, 40, "-89249499",
                    "NodoHardcoded", 60);
            logica.guardarMediciones(new Medida(Date.newBuilder().build(), "Hardcoded1", 1f, 1f, "1", UUID.randomUUID().toString()), usuarioLogged, nodoTemporal);
            try {
                lecturaDatos();
            } catch (InterruptedException e) {
                Log.d("LecturaDatos", "Error GRABE: " + e);
                Log.d("LecturaDatos", "---------------------");
                e.printStackTrace();
            }


            //do heavy work on a background thread

        } else { //Se ha cerrado la sesion!! Notificamos al usurio
            createNotificationChannel();
            Intent notificationIntent = new Intent(this, LoginActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);

            Notification notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Sesión no iniciada")
                    .setContentText("Se ha cerrado la sesión, no podemos recopilar datos")
                    .setSmallIcon(R.drawable.ic_menu_slideshow)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1, notification);
            stopSelf();
        }


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
package com.example.elibcli.proyectoambientales.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;

import com.example.elibcli.proyectoambientales.MainActivity;
import com.example.elibcli.proyectoambientales.R;
import com.example.elibcli.proyectoambientales.data.model.FirebaseLogicaNegocio;
import com.example.elibcli.proyectoambientales.data.model.LoggedInUser;
import com.example.elibcli.proyectoambientales.data.model.Nodo;
import com.example.elibcli.proyectoambientales.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class SegundoPlanoLecturaSensor extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private LoggedInUser usuarioLogged;
    FirebaseLogicaNegocio logica;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void actualizarNotificacion(String texto) {


        Intent notificationIntent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Recopilando datos")
                .setContentText(texto)
                .setSmallIcon(R.drawable.ic_menu_slideshow)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void lecturaDatos() throws InterruptedException {

    ArrayList<Nodo> lista = logica.obtenerListaNodos(usuarioLogged);
    actualizarNotificacion(lista.toString());
    Thread.sleep(1000);
    lecturaDatos();
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
                    .setContentText(input)
                    .setSmallIcon(R.drawable.ic_menu_slideshow)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);

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
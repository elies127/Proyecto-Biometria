package com.example.elibcli.proyectoambientales.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.elibcli.proyectoambientales.BTLEActivity;
import com.example.elibcli.proyectoambientales.R;
import com.example.elibcli.proyectoambientales.ServicioEscucharBeacons;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.SuperToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

public class HomeFragment extends Fragment {
   //Home principal. Ejecutado por default por main activity.
    private HomeViewModel homeViewModel;
    private static final String ETIQUETA_LOG = "DATOS ----";
    private SuperActivityToast superActivityToast;
    private Intent elIntentDelServicio = null;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        //---
        // Declaramos los botones aquí dado que estamos en un fragment.
        Button a = (Button) root.findViewById(R.id.botonBuscarDispositivosBTLE);
        Button b = (Button) root.findViewById(R.id.botonDetenerBusquedaDispositivosBTLE);
        Button c = (Button) root.findViewById(R.id.botonIniciarAjustesBluetooth);
        a.setOnClickListener(mButtonClickListener);
        b.setOnClickListener(mButtonClickListenerDetener);
        c.setOnClickListener(mButtonClickListenerAjustes);

        superActivityToast = new SuperActivityToast(getContext(), Style.TYPE_PROGRESS_CIRCLE);
        //--
        return root;
    }
    private View.OnClickListener mButtonClickListener = v -> botonArrancarServicioPulsado();

    private View.OnClickListener mButtonClickListenerDetener = v -> botonDetenerServicioPulsado();
    private View.OnClickListener mButtonClickListenerAjustes = v -> iniciarAjustesBTL ();

    private void iniciarAjustesBTL() {

        startActivity(new Intent(this.getActivity(), BTLEActivity.class));
    }

    public void botonArrancarServicioPulsado() {
        Log.d(ETIQUETA_LOG, " boton arrancar servicio Pulsado" );

        if ( this.elIntentDelServicio != null ) {
            // ya estaba arrancado
            return;
        }

        Log.d(ETIQUETA_LOG, " MainActivity.constructor : voy a arrancar el servicio");

        this.elIntentDelServicio = new Intent(getContext(), ServicioEscucharBeacons.class);

        this.elIntentDelServicio.putExtra("tiempoDeEspera", (long) 5000);
        getActivity().startService( this.elIntentDelServicio );

        //Toast message para representar feedback -- El servicio se ha ejecutado
        superActivityToast.setText("Escuchando beacons...");
        superActivityToast.setDuration(Style.DURATION_LONG);
        superActivityToast.setColor(1);
        superActivityToast.setTextColor(Color.WHITE);
       // superActivityToast.setTouchToDismiss(true);
        superActivityToast.show();
        superActivityToast.setIndeterminate(false);

        superActivityToast.setProgressMax(10);
    } // ()

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public void botonDetenerServicioPulsado() {

        if ( this.elIntentDelServicio == null ) {
            // no estaba arrancado
            return;
        }

        getActivity().stopService( this.elIntentDelServicio );

        this.elIntentDelServicio = null;

        Log.d(ETIQUETA_LOG, " boton detener servicio Pulsado" );
        superActivityToast.setProgress(10);
        superActivityToast.setProgressIndeterminate(false);

    } // ()

    // ---------------------------------------------------------------------------------------------
    // ---
}
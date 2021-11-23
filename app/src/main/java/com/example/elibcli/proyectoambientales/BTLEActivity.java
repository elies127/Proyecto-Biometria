package com.example.elibcli.proyectoambientales;
// ------------------------------------------------------------------
// ------------------------------------------------------------------

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.elibcli.proyectoambientales.data.model.FirebaseLogicaNegocio;
import com.example.elibcli.proyectoambientales.data.model.LoggedInUser;
import com.example.elibcli.proyectoambientales.data.model.Medida;
import com.example.elibcli.proyectoambientales.data.model.Nodo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.type.Date;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// ------------------------------------------------------------------
// ------------------------------------------------------------------

public class BTLEActivity extends AppCompatActivity {

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private static final String ETIQUETA_LOG = "BTLE Beacons >>";
    Dialog myDialog;
    private static final int CODIGO_PETICION_PERMISOS = 11223344;
    private Nodo dispositivoUsuario;
    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private LoggedInUser usuarioLogged;
    private FirebaseLogicaNegocio logica;
    private BluetoothLeScanner elEscanner;
    private String TAG = "BTLE ---";
    private final int TIEMPO = 2500;
    Handler handler = new Handler();
    private ScanCallback callbackDelEscaneo = null;
    private String nuestroBeacon; //Valor major de nuestro beacon para identificarlo
    private ArrayList<Medida> medidasParaSubir = new ArrayList<>();
    private boolean primeraVezMostrado = true;

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empieza ");

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): instalamos scan callback ");

        this.callbackDelEscaneo = new ScanCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanResult() ");

                mostrarInformacionDispositivoBTLE(resultado);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanFailed() ");

            }
        };

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empezamos a escanear ");

        this.elEscanner.startScan(this.callbackDelEscaneo);

    } // ()


    // --------------------------------------------------------------
    // --------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {

        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " toString = " + bluetoothDevice.toString());

        /*
        ParcelUuid[] puuids = bluetoothDevice.getUuids();
        if ( puuids.length >= 1 ) {
            //Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].getUuid());
           // Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].toString());
        }*/

        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi);

        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        //Mostrar datos en el TextView de la app
        TextView beaconslist = findViewById(R.id.beacons);
        EditText filtro = findViewById(R.id.filtroBeacon);

        TramaIBeacon tib = new TramaIBeacon(bytes);
        Log.d(ETIQUETA_LOG, " Major = " + Utilidades.bytesToInt(tib.getMajor()));
        Log.d(ETIQUETA_LOG, " Minor = " + Utilidades.bytesToInt(tib.getMinor()));
        Log.d(ETIQUETA_LOG, " ESTABLECIENDO TEXTVIEWS...----------");
        if (filtro.getText().length() == 0) {

            beaconslist.setText(beaconslist.getText() + System.getProperty("line.separator")
                    + "-----------" + System.getProperty("line.separator")
                    + "Nombre: " + bluetoothDevice.getName()
                    + "  |  uuid:" + Utilidades.bytesToString(tib.getUUID())
                    + " | Major: " + Utilidades.bytesToInt(tib.getMajor()));

        } else {
            String logfiltro = filtro.getText().toString() + " || " + bluetoothDevice.getName();
            Log.d("-------", logfiltro);
            if (bluetoothDevice.getName() != null) {
                if (bluetoothDevice.getName().equals(filtro.getText().toString())) {// - DISPOSITIVO ENCONTRADO
                    beaconslist.setText(beaconslist.getText() + System.getProperty("line.separator")
                            + "-----------" + System.getProperty("line.separator")
                            + "Nombre: " + bluetoothDevice.getName()
                            + "  |  uuid:" + Utilidades.bytesToString(tib.getUUID())
                            + " | Major: " + Utilidades.bytesToInt(tib.getMajor())
                            + " | Minor: " + Utilidades.bytesToInt(tib.getMinor()));
                    Log.d(ETIQUETA_LOG, "¡DISPOSITIVO ENCONTRADO!");
                    Toast.makeText(getApplicationContext(), "¡He encontrado el dispositivo filtrado: " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
                    if (primeraVezMostrado) {
                        dispositivoUsuario = new Nodo(resultado.getRssi(), Utilidades.bytesToInt(tib.getMajor()), Utilidades.bytesToInt(tib.getMinor()),
                                resultado.getDevice().getName(), Utilidades.bytesToString(tib.getUUID()), tib.getTxPower());
                        Log.d(TAG, "Intentando crear objeto dispositivoUsuario - " + dispositivoUsuario.toString());
                        detenerBusquedaDispositivosBTLE();
                        ShowPopup();
                        primeraVezMostrado = false;
                    }


                    Log.d("SENSOR", "Dispositivo a almacenar: " + " -- " + dispositivoUsuario.toMap().toString());


                    return;
                } else {

                    beaconslist.setText("No se ha encontrado ningún beacon con el valor prefijo del filtro <<" + filtro.getText() + " Valores detectados: " + bluetoothDevice.getName() + " || >> - Último beacon encontrado:" + System.getProperty("line.separator")
                            + "-----------" + System.getProperty("line.separator")
                            + "Nombre: " + bluetoothDevice.getName()
                            + "  |  uuid: " + Utilidades.bytesToString(tib.getUUID())
                            + " | Major: " + Utilidades.bytesToInt(tib.getMajor()));
                }

            }

        }


        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, "Beacon mostrado en la App mediante el filtro: " + filtro.getEditableText() + " | Nombre obtenido: " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( "
                + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( "
                + Utilidades.bytesToInt(tib.getMajor()) + " ) ");
        Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( "
                + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void buscarEsteDispositivoBTLE() {

        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): empieza ");

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): instalamos scan callback ");
        EditText filtro = findViewById(R.id.filtroBeacon);
        String dispositivoBuscado = String.valueOf(filtro.getText());
        if (dispositivoBuscado.length() == 0) {
            buscarTodosLosDispositivosBTLE();

            return;
        }

        // super.onScanResult(ScanSettings.SCAN_MODE_LOW_LATENCY, result); para ahorro de energÃ­a

        this.callbackDelEscaneo = new ScanCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanResult() ");

                byte[] bytes = resultado.getScanRecord().getBytes();

                TramaIBeacon tib = new TramaIBeacon(bytes);
                Log.d("Tratando de enlazar arduino", Utilidades.bytesToString(tib.getUUID()));
                if (Utilidades.bytesToString(tib.getUUID()).equals(filtro.getText())) {
                //if (Utilidades.bytesToString(tib.getUUID()).equals("EPSG-GTI-PROY-3A")) {
                    dispositivoUsuario = new Nodo(resultado.getRssi(), Utilidades.bytesToInt(tib.getMajor()), Utilidades.bytesToInt(tib.getMinor()),
                            resultado.getDevice().getName(), Utilidades.bytesToString(tib.getUUID()), tib.getTxPower());
                    Log.d(TAG, "Intentando crear objeto dispositivoUsuario - " + dispositivoUsuario.toString());
                    //Creamos un objeto calendar que guardara la fecha

                    Date date = Date.newBuilder().build();
                    // Instanciamos las variables que usaremos para recopilar los datos
                    float valorCO;
                    float valorCO2;

                    float valorO3;
                    float valorTemperatura;

                    String texto;
                    String valor;

                    // Deconstruimos el major
                    texto = Integer.toString(Utilidades.bytesToInt(tib.getMajor()));

                    valorCO = recogerDatos(texto, 1);
                    Log.e(TAG, "ValorCO " + valorCO);
                    valorCO2 = recogerDatos(texto, 2);
                    Log.e(TAG, "ValorCO2 " + valorCO2);

                    // Deconstruimos el minor
                    texto = Integer.toString(Utilidades.bytesToInt(tib.getMinor()));

                    valorO3 = recogerDatos(texto, 1);
                    Log.e(TAG, "ValorO3 " + valorO3);
                    valorTemperatura = recogerDatos(texto, 2);
                    Log.e(TAG, "ValorTemperatura " + valorTemperatura);

                    Medida medicionCO2 = new Medida(date, String.valueOf(valorCO2), MainActivity.getLatitud(), MainActivity.getLongitud(), "co2", UUID.randomUUID().toString());
                    Medida medicionCO = new Medida(date, String.valueOf(valorCO), MainActivity.getLatitud(), MainActivity.getLongitud(), "co", UUID.randomUUID().toString());
                    Medida medicionO3 = new Medida(date, String.valueOf(valorO3), MainActivity.getLatitud(), MainActivity.getLongitud(), "o3", UUID.randomUUID().toString());
                    Medida medicionTemperatura = new Medida(date, String.valueOf(valorTemperatura), MainActivity.getLatitud(), MainActivity.getLongitud(), "temperatura", UUID.randomUUID().toString());

                    medidasParaSubir.add(medicionCO2);

                    medidasParaSubir.add(medicionCO);
                    medidasParaSubir.add(medicionO3);
                    medidasParaSubir.add(medicionTemperatura);
                    Log.d("COLA", medidasParaSubir.get(0).getTipo().toString());
                    //ms
                }
                mostrarInformacionDispositivoBTLE(resultado);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanFailed() ");

            }
        };

        List<ScanFilter> filters = new ArrayList<>();
        ScanFilter sf = new ScanFilter.Builder().setDeviceName(dispositivoBuscado).build();
        filters.add(sf);


        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado);
        //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado
        //      + " -> " + Utilidades.stringToUUID( dispositivoBuscado ) );

        this.elEscanner.startScan(filters, settings, this.callbackDelEscaneo);
    } // ()


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void ShowPopup() {


        myDialog.setContentView(R.layout.custompopup);
        TextView txtclose, txtmajor, txtminor, txtnoise, txtname;
        txtmajor = myDialog.findViewById(R.id.major_value);
        txtminor = myDialog.findViewById(R.id.minor_value);
        txtnoise = myDialog.findViewById(R.id.noise_value);
        txtname = myDialog.findViewById(R.id.name_value);
        Log.d(TAG, dispositivoUsuario.toString());
        txtmajor.setText(String.valueOf(dispositivoUsuario.getMajor()));
        txtminor.setText(String.valueOf(dispositivoUsuario.getMinor()));
        txtnoise.setText(String.valueOf(dispositivoUsuario.getNoise()));
        txtname.setText(dispositivoUsuario.getBeaconName());
        txtclose = (TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setText("M");

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void detenerBusquedaDispositivosBTLE() {

        if (this.callbackDelEscaneo == null) {
            return;
        }

        this.elEscanner.stopScan(this.callbackDelEscaneo);
        this.callbackDelEscaneo = null;

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton buscar dispositivos BTLE Pulsado");
        this.buscarTodosLosDispositivosBTLE();
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton nuestro dispositivo BTLE Pulsado");
        //this.buscarEsteDispositivoBTLE( Utilidades.stringToUUID( "EPSG-GTI-PROY-3A" ) );

        //this.buscarEsteDispositivoBTLE( "EPSG-GTI-PROY-3A" );
        this.buscarEsteDispositivoBTLE();

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton detener busqueda dispositivos BTLE Pulsado");
        this.detenerBusquedaDispositivosBTLE();
    } // ()

    public void borrarTextViewBusqueda(View v) {
        TextView a = findViewById(R.id.beacons);
        a.setText("Búsqueda borrada...");
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos adaptador BT ");

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitamos adaptador BT ");

        bta.enable();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitado =  " + bta.isEnabled());

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): estado =  " + bta.getState());

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos escaner btle ");

        this.elEscanner = bta.getBluetoothLeScanner();

        if (this.elEscanner == null) {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): Socorro: NO hemos obtenido escaner btle  !!!!");

        }

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): voy a perdir permisos (si no los tuviera) !!!!");

        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    BTLEActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_PETICION_PERMISOS);
        } else {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): parece que YA tengo los permisos necesarios !!!!");

        }
    } // ()


    // --------------------------------------------------------------
    // --------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btle);
        myDialog = new Dialog(this);
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) {
            usuarioLogged = new LoggedInUser(usuario.getUid(), usuario.getDisplayName(), usuario.getEmail());
            logica = new FirebaseLogicaNegocio();

        } else {
            Toast.makeText(getApplicationContext(), "¡Ha habido un problema con tu sesión! Puede que no funcione bien el vínculo de dispositivos", Toast.LENGTH_SHORT).show();
        }

        Log.d(ETIQUETA_LOG, " onCreate(): empieza ");

        inicializarBlueTooth();

        Log.d(ETIQUETA_LOG, " onCreate(): termina ");


        ejecutarTareaGuardarMediciones();
    } // onCreate()

    public void ejecutarTareaGuardarMediciones() {

        handler.postDelayed(new Runnable() {
            public void run() {

                if (medidasParaSubir.size() > 0) {

                    Medida medicion = medidasParaSubir.get(0);
                    Log.d("COLA-thread", medidasParaSubir.get(0).getTipo().toString());
                    medidasParaSubir.remove(0);
                    Log.d("COLA-thread-medicion", medicion.getTipo().toString());
                    logica.guardarMediciones(medicion, usuarioLogged, dispositivoUsuario);

                }


                handler.postDelayed(this, TIEMPO);

            }

        }, TIEMPO);

    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CODIGO_PETICION_PERMISOS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): permisos concedidos  !!!!");
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                } else {

                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): Socorro: permisos NO concedidos  !!!!");

                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    } // ()

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void btnLink(View view) {
        logica.enlazarDispositivo(dispositivoUsuario, usuarioLogged);
        Toast.makeText(getApplicationContext(),
                "Acabas de añadir a la lista de sensores: " + dispositivoUsuario.getBeaconName() + ". Por tanto... ¡Dispositivo enlazado correctamente!",
                Toast.LENGTH_LONG).show();
    }

    private int recogerDatos(String datos, int tipo) {

        int valor = 0;
        String texto = "";
        char cadaDato[] = datos.toCharArray();
        if (tipo == 1) {
            if ('-' == cadaDato[0]) {
                texto = cadaDato[0] + "" + cadaDato[1] + "" + cadaDato[2];
                valor = Integer.parseInt(texto);
                return valor;
            } else {
                texto = cadaDato[0] + "" + cadaDato[1];
                valor = Integer.parseInt(texto);
                return valor;
            }
        } else {
            if ('-' == cadaDato[2]) {
                texto = cadaDato[2] + "" + cadaDato[3] + "" + cadaDato[4];
                valor = Integer.parseInt(texto);
                return valor;
            } else {
                if (datos.length() == 4) {
                    texto = cadaDato[2] + "" + cadaDato[3];
                    valor = Integer.parseInt(texto);
                    return valor;
                } else {
                    texto = cadaDato[3] + "" + cadaDato[4];
                    valor = Integer.parseInt(texto);
                    return valor;
                }
            }
        }
    }
}
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------



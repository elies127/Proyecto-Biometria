package com.example.elibcli.proyectoambientales.data.model;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.type.Date;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.LogManager;

public class Nodo {


    private int noise;
    private int major;
    private int minor;
    private int txPower;
    private String beaconName;
    private String uuid;
    private Date a;
    private ArrayList<Medida> medidas;

    public Nodo(){


    }
    public Nodo(int noise, int major, int minor, String beaconName, String uuidIncome, int txPower) {
        this.noise = noise;
        this.major = major;
        this.minor = minor;
        this.beaconName = beaconName;
        this.txPower = txPower;
        this.medidas = new ArrayList<Medida>();

        Medida medida = new Medida(Date.newBuilder().build(), "69", 1.2f, 1.5f, "1");
        this.medidas.add(medida) ;//Simulamos una medicion, el constructor no nos introdujo una
        try{
            UUID uuid = UUID.fromString(uuidIncome);
            this.uuid = uuid.toString();

        } catch (IllegalArgumentException exception){
            //Es una UUID irreconocible, probablemente beacon virtual
            Random r = new Random();

           // this.uuid = "VirtualUUID_" + r.nextInt();
            this.uuid = "VirtualUUID_" + -89249499;
            Log.d("SENSOR", "UUID desconocida ¿Es un Beacon virtual?");
        }

    }
    @Exclude
    //Metodo para añadir medidas
    public void addMedida(Date date, String valor, String lat, String lon, String tipo){
        this.medidas.add(new Medida(date, valor, Float.parseFloat(lat), Float.parseFloat(lon), tipo));
    }

    public Nodo(int noise, int major, int minor, String beaconName, String uuidIncome, int txPower, ArrayList<Medida> medidas) {
        this.noise = noise;
        this.major = major;
        this.minor = minor;
        this.beaconName = beaconName;
        this.txPower = txPower;
        this.medidas = medidas;
        try{
            UUID uuid = UUID.fromString(uuidIncome);
            this.uuid = uuid.toString();

        } catch (IllegalArgumentException exception){
            //Es una UUID irreconocible, probablemente beacon virtual
            Random r = new Random();

            this.uuid = "VirtualUUID_" + r.nextInt();
            Log.d("SENSOR", "UUID desconocida ¿Es un Beacon virtual?");
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Exclude
    public Map toMap() {
        HashMap result = new HashMap<>();
        result.put("noise", noise);
        result.put("beaconName", beaconName);
        result.put("uuid", uuid);
        result.put("beaconName", beaconName);
        result.put("txPower", txPower);
        result.put("medidas", medidas);



        return result;

    }
    @Exclude
    public ArrayList<Medida> getMedidas(){
        return medidas;
    }
    public int getNoise() {
        return noise;
    }

    public void setNoise(int noise) {
        this.noise = noise;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getBeaconName() {
        return beaconName;
    }

    public void setBeaconName(String beaconName) {
        this.beaconName = beaconName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public float calcularDistancia(int txPower, double rssi) {
        if (rssi == 0) {
            return (float) -1.0; // if we cannot determine accuracy, return -1.
        }

        Log.d("SENSOR", "Calculando distancia");


        double ratio = rssi*1.0/txPower;
        double distancia;
        if (ratio < 1.0) {
            distancia =  Math.pow(ratio,10);
        }
        else {
            //distance =  (mCoefficient1)*Math.pow(ratio,mCoefficient2) + mCoefficient3;
            distancia =  -1;
        }
        DecimalFormat df = new DecimalFormat("#.##########"); //Cuantos decimales va a tener el ratio de distancia?

        try{
            return Float.parseFloat(df.format(distancia));
        } catch(NumberFormatException ex){ // handle your exception
            Log.d("DISTANCIA", "Error al parsear el float: " + ex);
            return -69;
        }

    }

    public double getTxPower() {

        return txPower;
    }
}
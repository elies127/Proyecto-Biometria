package com.example.elibcli.proyectoambientales.data.model;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.LogManager;

public class nodeSensor {


    private int noise;
    private int major;
    private int minor;
    private int txPower;
    private String beaconName;
    private String uuid;

    public nodeSensor(){


    }
    public nodeSensor(int noise, int major, int minor, String beaconName, String uuidIncome, int txPower) {
        this.noise = noise;
        this.major = major;
        this.minor = minor;
        this.beaconName = beaconName;
        this.txPower = txPower;
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


    @Exclude
    public Map toMap() {
        HashMap result = new HashMap<>();
        result.put("noise", noise);
        result.put("major", major);
        result.put("minor", minor);
        result.put("beaconName", beaconName);
        result.put("uuid", uuid);
        result.put("beaconName", beaconName);
        result.put("txPower", txPower);

        return result;

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
        DecimalFormat df = new DecimalFormat("#.######"); //Cuantos decimales va a tener el ratio de distancia?
        return Float.parseFloat(df.format(distancia));
    }

    public double getTxPower() {

        return txPower;
    }
}
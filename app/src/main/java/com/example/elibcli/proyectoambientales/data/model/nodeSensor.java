package com.example.elibcli.proyectoambientales.data.model;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class nodeSensor {


    private int noise;
    private int major;
    private int minor;
    private String beaconName;
    private String uuid;

    public nodeSensor(int noise, int major, int minor, String beaconName, String uuidIncome) {
        this.noise = noise;
        this.major = major;
        this.minor = minor;
        this.beaconName = beaconName;
        try{
            UUID uuid = UUID.fromString(uuidIncome);
            this.uuid = uuid.toString();

        } catch (IllegalArgumentException exception){
            //handle the case where string is not valid UUID
            this.uuid = "UUID desconocida ¿Es un Beacon virtual?";
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

}
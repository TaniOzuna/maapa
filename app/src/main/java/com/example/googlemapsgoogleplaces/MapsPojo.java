package com.example.googlemapsgoogleplaces;

public class MapsPojo {
    private double latitud;
    private double longitud;
    private String nombre;

    //constructor
    public MapsPojo() {
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getNombre(){return nombre;}

    public void setNombre(String nombre){this.nombre = nombre;}
}

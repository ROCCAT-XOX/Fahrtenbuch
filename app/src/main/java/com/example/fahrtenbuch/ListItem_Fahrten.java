package com.example.fahrtenbuch;

public class ListItem_Fahrten {

    private Integer fahrt_id ;
    private Integer fahrzeug_id ;
    private String public_id = "";
    private Integer reservierungs_id;
    private String start = "";
    private String ziel = "";
    private Float entfernung;

    public ListItem_Fahrten(Integer fahrt_id, Integer reservierungs_id, String public_id, String start, String ziel, Float entfernung, Integer fahrzeug_id) {

        this.fahrt_id = fahrt_id;
        this.start = start;
        this.ziel = ziel;
        this.entfernung = entfernung;
        this.fahrzeug_id = fahrzeug_id;
        this.public_id = public_id;
        this.reservierungs_id = reservierungs_id;
    }

    public Integer getReservierungs_id() {
        return reservierungs_id;
    }
    public Integer getFahrt_id() {
        return fahrt_id;
    }
    public Integer getFahrzeug_id() {
        return fahrzeug_id;
    }
    public Float getEntfernung() {
        return entfernung;
    }
    public String getPublic_id() {
        return public_id;
    }
    public String getStart() {
        return start;
    }
    public String getZiel() { return ziel; }
}

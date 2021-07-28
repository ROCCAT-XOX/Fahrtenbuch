package com.example.fahrtenbuch;

public class ListItem_Fleet {

    private Integer car_id;
    private String marke = "";
    private String modell = "";
    private Float kilometerstand;
    private String verfügbar;
    private Integer ps;

    public ListItem_Fleet(Integer car_id, String marke, String modell, Float kilometerstand, Integer ps,String verfügbar) {

        this.car_id = car_id;
        this.marke = marke;
        this.modell = modell;
        this.kilometerstand = kilometerstand;
        this.ps = ps;
        this.verfügbar = verfügbar;
    }

    public Integer getCar_id() {
        return car_id;
    }
    public String getMarke() {
        return marke;
    }
    public String getModell() {
        return modell;
    }
    public Float getKilometerstand() {
        return kilometerstand;
    }
    public Integer getPs(){return ps;}
    public String getVerfügbar() {return verfügbar;}

}

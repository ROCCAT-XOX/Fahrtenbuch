package com.example.fahrtenbuch;

public class ListItem_Fleet {

    private Integer car_id;
    private String marke = "";
    private String modell = "";
    private Float kilometerstand;

    public ListItem_Fleet(Integer car_id, String marke, String modell, Float kilometerstand) {

        this.car_id = car_id;
        this.marke = marke;
        this.modell = modell;
        this.kilometerstand = kilometerstand;
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

}

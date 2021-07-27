package com.example.fahrtenbuch;

public class ListItem {

    private Integer id ;
    private String marke = "";
    private String modell = "";
    private Float kilometerstand;

    public ListItem(Integer id, String marke, String modell, Float kilometerstand) {

        this.id = id;
        this.marke = marke;
        this.modell = modell;
        this.kilometerstand = kilometerstand;
    }

    public Integer getId() {
        return id;
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
